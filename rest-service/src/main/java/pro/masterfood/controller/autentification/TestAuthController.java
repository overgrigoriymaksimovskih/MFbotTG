//package pro.masterfood.controller.autentification;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import pro.masterfood.dto.AuthResponse;
//import pro.masterfood.service.AuthService;
//
//import java.util.Optional;
//
//@Controller
//public class TestAuthController {
//
//    private final AuthService authService;
//
//    public TestAuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    // Эндпоинт, который имитирует сайт: принимает токен из URL и проверяет его
//    @GetMapping("/api/test-verify")  // Например, http://localhost:8080/test-verify?token=jwt-token
//    public String testVerify(@RequestParam(required = false) String token, Model model) {
//        if (token == null || token.isEmpty()) {
//            model.addAttribute("status", "failure");
//            model.addAttribute("message", "Токен отсутствует в URL.");
//            return "test-verify";  // Рендерим страницу с ошибкой
//        }
//
//        Optional<AuthResponse> authData = authService.validateAndGetUser(token);
//        if (authData.isEmpty()) {
//            model.addAttribute("status", "failure");
//            model.addAttribute("message", "Авторизация провалена: недействительный или истекший токен.");
//        } else {
//            AuthResponse response = authData.get();
//            model.addAttribute("status", "success");
//            model.addAttribute("message", "Авторизация успешна!");
//            model.addAttribute("userId", response.getUserId());
//            model.addAttribute("phone", response.getPhone());
//        }
//
//        return "test-verify";  // Рендерим шаблон с результатом
//    }
//}

package pro.masterfood.controller.autentification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.masterfood.utils.CustomPostClient;

import java.util.Map;

@Controller  // Рендерим HTML
public class TestAuthController {

    @Autowired
    private CustomPostClient customPostClient;  // Инжектим ваш компонент
    private final ObjectMapper objectMapper = new ObjectMapper();  // Для JSON сериализации/десериализации только для логгирования
    // Читаем API-ключ из application.properties
    @Value("${app.api.key}")
    private String apiKey;
    @GetMapping("/api/test-verify")
    public String testVerify(@RequestParam(required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("status", "failure");
            model.addAttribute("message", "Токен отсутствует.");
            return "test-verify";
        }

        // Подготавливаем тело POST: {"token": "значение"}
        Map<String, String> postBody = Map.of("token", token);

        // Подготавливаем заголовки, включая X-API-Key
        Map<String, String> headers = Map.of("X-API-Key", apiKey, "Content-Type", "application/json");

        // Логируем тело запроса----------------------------------------------------------------------------------------
        try {
            String requestJson = objectMapper.writeValueAsString(postBody);
            System.out.println("Тестовый сервис передает строку запроса (JSON): " + requestJson);
        } catch (JsonProcessingException e) {
            System.out.println("Ошибка сериализации запроса в JSON: " + e.getMessage());
        }
        //--------------------------------------------------------------------------------------------------------------

        String authUrl = "https://smakmart.ru/api/verify-token";  // Продакшен: ваш внешний URL

        // Вызываем ваш CustomPostClient для POST с JSON
//        ResponseEntity<Map<String, Object>> response = customPostClient.sendTestPostRequest(authUrl, postBody);
        // Передаем headers в customPostClient
        ResponseEntity<Map<String, Object>> response = customPostClient.sendTestPostRequest(authUrl, postBody, headers);


        // Логируем тело ответа, если есть------------------------------------------------------------------------------
        if (response.getBody() != null) {
            try {
                String responseJson = objectMapper.writeValueAsString(response.getBody());
                System.out.println("Наш сервис проверки токена вернул строку ответа (JSON): " + responseJson);
            } catch (JsonProcessingException e) {
                System.out.println("Ошибка сериализации ответа в JSON: " + e.getMessage());
            }
        } else {
            System.out.println("Наш сервис проверки токена вернул пустой ответ");
        }
        //--------------------------------------------------------------------------------------------------------------

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseData = response.getBody();
            if ("success".equals(responseData.get("status"))) {
                Boolean authorized = (Boolean) responseData.get("authorized");
                if (Boolean.TRUE.equals(authorized)) {
                    model.addAttribute("status", "success");
                    model.addAttribute("message", "Авторизация успешна!");
                    model.addAttribute("userId", responseData.get("userId"));
                    model.addAttribute("phone", responseData.get("phone"));
                    return "test-verify";
                } else {
                    model.addAttribute("status", "failure");
                    model.addAttribute("message", "Пользователь не авторизован.");
                    return "test-verify";
                }
            } else {
                String errorMessage = (String) responseData.get("message");
                model.addAttribute("status", "failure");
                model.addAttribute("message", "Ошибка: " + errorMessage);
                return "test-verify";
            }
        } else {
            // Ошибка HTTP от CustomPostClient
            Map<String, Object> errorBody = response.getBody();
            String errorMessage = errorBody != null ? (String) errorBody.get("error") : "Неизвестная ошибка";
            model.addAttribute("status", "failure");
            model.addAttribute("message", "Ошибка при проверке: " + errorMessage);
            return "test-verify";
        }
    }
}

