package ru.conditer.service;

import ru.conditer.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
