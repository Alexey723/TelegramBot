package ru.conditer.services;

import org.apache.logging.log4j.message.Message;
import ru.conditer.entity.AppDocument;
import ru.conditer.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
