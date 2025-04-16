package pro.masterfood.service;

import pro.masterfood.dto.LoginParams;

import java.util.Map;

public interface UserActivationService {
    //    boolean activation(String cryptoUserId);
    Map<String, Object> activationFromSite(String email,
                                           String password);
    void consumeLogin(LoginParams loginParams);

    void sendAnswer(String output, Long chatId);
}
