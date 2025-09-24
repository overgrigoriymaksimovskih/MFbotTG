package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;

import java.util.Map;

public interface UserActivationService {
    //    boolean activation(String cryptoUserId);
//    Map<String, Object> activationFromSite(String email,
//                                           String password);
    void consumeLogin(RequestParams requestParams);
    void consumeContact(RequestParams requestParams);
    void consumeSMS(RequestParams requestParams);

    void sendAnswer(String output, Long chatId);
}
