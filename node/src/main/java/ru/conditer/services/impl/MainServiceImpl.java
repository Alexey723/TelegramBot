package ru.conditer.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.conditer.dao.AppUserDAO;
import ru.conditer.dao.RawDataDao;
import ru.conditer.entity.AppUser;
import ru.conditer.entity.RawData;
import ru.conditer.entity.enums.UserState;
import ru.conditer.services.MainService;
import ru.conditer.services.ProducerService;
import ru.conditer.services.enums.ServiceCommand;

import static ru.conditer.entity.enums.UserState.BASIC_STATE;
import static ru.conditer.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.conditer.services.enums.ServiceCommand.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDao rawDataDao, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку email
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
        //TODO добавить сохранения документа.
        var answer = "Документ успешно загружен: http://test.ru/get-doc/777";
        sendAnswer(answer, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO добавить сохранения фото.
        var answer = "Фото успешно загружено: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
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

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            //TODO добавить регистрацию
            return "Временно недоступно.";
        }else if (HELP.equals(cmd)){
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        }else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
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
        var appUserOpt = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (appUserOpt.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return appUserOpt.get();
    }
}
