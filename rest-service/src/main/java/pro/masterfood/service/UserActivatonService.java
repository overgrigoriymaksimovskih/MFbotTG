package pro.masterfood.service;

public interface UserActivatonService {
    boolean activation(String cryptoUserId);
    boolean activationMf(String id, String pass);
}
