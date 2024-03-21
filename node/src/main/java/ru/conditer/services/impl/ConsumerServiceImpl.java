package ru.conditer.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.conditer.services.ConsumerService;
import ru.conditer.services.ProducerService;

import static ru.conditer.model.RabbitQueue.*;

@Log4j
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainServiceImpl mainService;

    public ConsumerServiceImpl(MainServiceImpl mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Doc message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }
}
