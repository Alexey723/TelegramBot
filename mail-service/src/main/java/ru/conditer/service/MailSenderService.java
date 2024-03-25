package ru.conditer.service;


import ru.conditer.utils.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
