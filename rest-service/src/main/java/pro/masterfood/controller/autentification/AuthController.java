//package pro.masterfood.controller.autentification;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import pro.masterfood.dto.AuthResponse;
//import pro.masterfood.service.AuthService;
//
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//public class AuthController {
//
//    private final AuthService authService;
//    private final ObjectMapper objectMapper = new ObjectMapper();  // Для JSON сериализации/десериализации только для логгирования
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/api/verify-token")  // Сайт отправляет POST сюда с токеном
//    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> payload) {
//        String token = payload.get("token");
//        if (token == null || token.isEmpty()) {
//            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Token is required");
//            // Логируем тело ответа-------------------------------------------------------------------------------------
//            try {
//                String responseJson = objectMapper.writeValueAsString(errorResponse);
//                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
//            } catch (JsonProcessingException e) {
//                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
//            }
//            //----------------------------------------------------------------------------------------------------------
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//
//        Optional<AuthResponse> authData = authService.validateAndGetUser(token);
//        if (authData.isEmpty()) {
//            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Invalid or expired token");
//            // Логируем тело ответа-------------------------------------------------------------------------------------
//            try {
//                String responseJson = objectMapper.writeValueAsString(errorResponse);
//                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
//            } catch (JsonProcessingException e) {
//                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
//            }
//            //----------------------------------------------------------------------------------------------------------
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//
//        AuthResponse response = authData.get();
//        Map<String, Object> successResponse = Map.of(
//                "status", "success",
//                "authorized", true,  // Флаг авторизации
//                "userId", response.getUserId(),
//                "phone", response.getPhone()  // Номер телефона
//        );
//        // Логируем тело ответа-----------------------------------------------------------------------------------------
//        try {
//            String responseJson = objectMapper.writeValueAsString(successResponse);
//            System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
//        } catch (JsonProcessingException e) {
//            System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
//        }
//        //--------------------------------------------------------------------------------------------------------------
//        return ResponseEntity.ok(successResponse);
//    }
//}

package pro.masterfood.controller.autentification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;  // Добавлено для чтения из application.properties
import org.springframework.http.HttpStatus;  // Добавлено для статуса 401
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;  // Добавлено для @RequestHeader
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.service.AuthService;

import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();  // Для JSON сериализации/десериализации только для логгирования

    // Добавлено: читаем API-ключ из application.properties
    @Value("${app.api.key}")
    private String apiKey;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/verify-token")  // Сайт отправляет POST сюда с токеном
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> payload,
                                                           @RequestHeader(value = "X-API-Key", required = false) String requestApiKey) {  // Добавлено: проверка заголовка
        // Добавлено: проверяем API-ключ перед всем остальным
        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Invalid API key");
            // Логируем тело ответа-------------------------------------------------------------------------------------
            try {
                String responseJson = objectMapper.writeValueAsString(errorResponse);
                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
            } catch (JsonProcessingException e) {
                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
            }
            //----------------------------------------------------------------------------------------------------------
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);  // 401 вместо 400
        }

        String token = payload.get("token");
        if (token == null || token.isEmpty()) {
            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Token is required");
            // Логируем тело ответа-------------------------------------------------------------------------------------
            try {
                String responseJson = objectMapper.writeValueAsString(errorResponse);
                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
            } catch (JsonProcessingException e) {
                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
            }
            //----------------------------------------------------------------------------------------------------------
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Optional<AuthResponse> authData = authService.validateAndGetUser(token);
        if (authData.isEmpty()) {
            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Invalid or expired token");
            // Логируем тело ответа-------------------------------------------------------------------------------------
            try {
                String responseJson = objectMapper.writeValueAsString(errorResponse);
                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
            } catch (JsonProcessingException e) {
                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
            }
            //----------------------------------------------------------------------------------------------------------
            return ResponseEntity.badRequest().body(errorResponse);
        }

        AuthResponse response = authData.get();
        Map<String, Object> successResponse = Map.of(
                "status", "success",
                "authorized", true,  // Флаг авторизации
                "userId", response.getUserId(),
                "phone", response.getPhone()  // Номер телефона
        );
        // Логируем тело ответа-----------------------------------------------------------------------------------------
        try {
            String responseJson = objectMapper.writeValueAsString(successResponse);
            System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
        } catch (JsonProcessingException e) {
            System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
        }
        //--------------------------------------------------------------------------------------------------------------
        return ResponseEntity.ok(successResponse);
    }
}
