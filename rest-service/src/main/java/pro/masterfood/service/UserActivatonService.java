package pro.masterfood.service;

import org.springframework.http.ResponseEntity;

public interface UserActivatonService {
    boolean activation(String cryptoUserId);
    ResponseEntity activationMf(String email, String password);
}
