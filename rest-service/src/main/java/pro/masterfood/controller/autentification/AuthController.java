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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.exceptions.*;
import pro.masterfood.service.AuthService;

import java.util.Map;

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
                                                           @RequestHeader(value = "X-API-Key", required = false) String requestApiKey) {
        // Проверяем API-ключ перед всем остальным если он кривой или отсутствует возвращаем 401 еррор инвалид кей
        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Invalid API key");
            // Логируем тело ответа-------------------------------------------------------------------------------------
            try {
                String responseJson = objectMapper.writeValueAsString(errorResponse);
                System.out.println("Сервис проверки токена возвращает строку ответа (JSON): " + responseJson);
            } catch (JsonProcessingException e) {
                System.out.println("Ошибка сериализации ответа в JSON для логгирования: " + e.getMessage());
            }
            //----------------------------------------------------------------------------------------------------------
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);  // 401 вместо 400
        }

        // Проверяем что токен вообще есть, если его нет возвращаем 400 еррор токен из емпти
        String token = payload.get("token");
        if (token == null || token.isEmpty()) {
            Map<String, Object> errorResponse = Map.of("status", "error", "message", "Token is empty");
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

        // Обрабатываем токен и сохраняем ответ на пришедший ПОСТ запрос. Вернет
        // или 200 "userId":111,"phone":"79514773111","status":"success","authorized":true
        // или какую то из 400... с причиной, смотря какой кастомный ексепшн
        try {
            AuthResponse authData = authService.validateAndGetUser(token);

            Map<String, Object> successResponse = Map.of(
                    "status", "success",
                    "userId", authData.getUserId(),
                    "phone", authData.getPhone()
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

        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)//401
                    .body(Map.of("status", "error", "message", "Токен истёк. Обновите токен: " + e.getMessage()));
        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)//401
                    .body(Map.of("status", "error", "message", "Недействительный токен"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)//404
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (UserSiteIdIsNullException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)//400
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (UserPhoneIsNullException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)//400
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
