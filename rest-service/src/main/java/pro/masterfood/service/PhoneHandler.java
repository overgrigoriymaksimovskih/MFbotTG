package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;

public interface PhoneHandler {
    void handlePhone(RequestParams requestParams);
    void sendAnswer(String output, Long chatId);
}
