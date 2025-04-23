package pro.masterfood.service;

import pro.masterfood.dto.MailParams;

public interface MailSenderService {
    void send (MailParams mailParams);
    void sendAnswer(String output, Long chatId);
}
