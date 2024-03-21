package ru.conditer.services;

import org.apache.logging.log4j.message.Message;
import ru.conditer.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    //AppPhoto processPhoto(Message telegramMessage);

    //String generateLink(Long docId, LinkType linkType);
}
