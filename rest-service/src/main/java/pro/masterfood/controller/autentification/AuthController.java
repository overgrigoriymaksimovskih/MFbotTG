package pro.masterfood.controller.autentification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.service.AuthService;

import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/verify-token")  // Сайт отправляет POST сюда с токеном
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Token is required"));
        }

        Optional<AuthResponse> authData = authService.validateAndGetUser(token);
        if (authData.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid or expired token"));
        }

        AuthResponse response = authData.get();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "authorized", true,  // Флаг авторизации
                "userId", response.getUserId(),
                "phone", response.getPhone()  // Номер телефона
        ));
    }
}
