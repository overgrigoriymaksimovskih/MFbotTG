package pro.masterfood.service;

import pro.masterfood.dto.AuthResponse;
import java.util.Optional;

public interface AuthService {
    AuthResponse validateAndGetUser(String token);
}


