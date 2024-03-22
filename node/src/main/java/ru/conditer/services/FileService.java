package ru.conditer.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.conditer.entity.AppDocument;
import ru.conditer.entity.AppPhoto;
import ru.conditer.services.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
