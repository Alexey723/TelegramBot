package ru.conditer.controller;


import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private UpdateController updateController;

    public TelegramBot(UpdateController updateController){
        this.updateController=updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registrBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
    @Override
    public void onUpdateReceived(Update update) {
/*         var originalMessage = update.getMessage();  // получаем сообщение из чата
       log.debug(originalMessage.getText());
        // Готовим ответ
        var response = new SendMessage();
        response.setChatId(originalMessage.getChatId().toString());
        if(originalMessage.getText().equalsIgnoreCase("картинки")){
            response.setText("Сейчас намутим картинки (* ^ ω ^)");
        } else {
            response.setText("Спасибо за Ваше сообщение, работаю над ним....");
        }
        sendAnswerMessage(response);*/
        updateController.processUpdate(update);
    }
public void sendAnswerMessage(SendMessage message){
        if(message!=null){
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
}


}
