package pro.masterfood.service;

import pro.masterfood.dto.AuthResponse;
import java.util.Optional;

public interface AuthService {
    Optional<AuthResponse> validateAndGetUser(String token);
}


