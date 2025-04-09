package pro.masterfood.service;

import java.util.Map;

public interface UserActivatonService {
    boolean activation(String cryptoUserId);
    Map<String, Object> activationMf(String action,
                                     String email,
                                     String password,
                                     String check_num,
                                     String token);
}
