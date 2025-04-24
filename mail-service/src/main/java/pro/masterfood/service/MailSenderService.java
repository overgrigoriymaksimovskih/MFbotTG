package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.masterfood.dto.MailParams;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;

public interface MailSenderService {
    void send (MailParams mailParams);
    void sendAnswer(String output, Long chatId);
}
