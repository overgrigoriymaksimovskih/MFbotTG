package pro.masterfood.service;

public interface UserActivatonService {
    boolean activation(String cryptoUserId);
    boolean activationMf(String action,
                         String email,
                         String password,
                         String check_num,
                         String token);
}
