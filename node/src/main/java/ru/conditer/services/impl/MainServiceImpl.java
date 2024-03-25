package ru.conditer.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.conditer.dao.AppUserDAO;
import ru.conditer.dao.RawDataDao;
import ru.conditer.entity.AppDocument;
import ru.conditer.entity.AppPhoto;
import ru.conditer.entity.AppUser;
import ru.conditer.entity.RawData;
import ru.conditer.exceptions.UploadFileException;
import ru.conditer.services.AppUserService;
import ru.conditer.services.FileService;
import ru.conditer.services.MainService;
import ru.conditer.services.ProducerService;
import ru.conditer.services.enums.LinkType;
import ru.conditer.services.enums.ServiceCommand;

import java.io.IOException;
import java.net.URISyntaxException;

import static ru.conditer.entity.enums.UserState.BASIC_STATE;
import static ru.conditer.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.conditer.services.enums.ServiceCommand.*;


@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
    private  WeatherForecastServiceImpl weatherForecastService;

    private final Long UserId = 5173993426L;

    public MainServiceImpl(RawDataDao rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO,
                           FileService fileService, AppUserService appUserService, WeatherForecastServiceImpl weatherForecastService) {
        this.rawDataDao = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
	    this.appUserService = appUserService;
        this.weatherForecastService = weatherForecastService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";        
        var serviceCommand = ServiceCommand.fromValue(text);
    	if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            try {
                output = processServiceCommand(appUser, text);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
	    AppDocument doc = fileService.processDoc(update.getMessage());
        String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
	    var answer = "Документ успешно загружен! "
			    + "Ссылка для скачивания: " + link;
	    sendAnswer(answer, chatId);
	} catch (UploadFileException ex) {
	    log.error(ex);
	    String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
	    sendAnswer(error, chatId);
	}
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено! "
                    + "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте "
			    + "свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        }else if(!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) throws IOException, URISyntaxException {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (WEATHER.equals(serviceCommand)) {
            return weatherForecastService.whatIsTheWeather();
        } else if (START.equals(serviceCommand)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }
    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/weather - Прогноз погоды в Краснодаре на 7 дней;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Комада отменена!";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update){
        var telegramUser = update.getMessage().getFrom();
        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (optional.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return optional.get();
    }
}
