package pro.masterfood.service;

public interface UserActivatonService {
    boolean activation(String cryptoUserId);
    String activationMf(String email,
                         String password);
}
