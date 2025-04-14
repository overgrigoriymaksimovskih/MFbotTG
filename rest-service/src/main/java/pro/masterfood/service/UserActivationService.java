package pro.masterfood.service;

import java.util.Map;

public interface UserActivationService {
    //    boolean activation(String cryptoUserId);
    Map<String, Object> activationFromSite(String email,
                                           String password);
}
