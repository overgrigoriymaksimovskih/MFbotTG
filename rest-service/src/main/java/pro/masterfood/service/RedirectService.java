package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface RedirectService {
    String producerAnswer(Long telegramUserId);
}
