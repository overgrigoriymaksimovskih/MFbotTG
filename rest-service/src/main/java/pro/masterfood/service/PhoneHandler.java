package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;

public interface PhoneHandler {
    void handleContact(RequestParams requestParams);
    void handlePhone(RequestParams requestParams);
    void sendAnswer(String output, Long chatId);
}
