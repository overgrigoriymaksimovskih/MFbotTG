package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;

public interface UserInformationProvider {
    void consumeGetBalance(RequestParams requestParams);
    void consumeGetOrderStatus(RequestParams requestParams);

    void sendAnswer(String output, Long chatId);
}
