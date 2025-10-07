package pro.masterfood.controller.autentification;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.service.AuthService;

import java.util.Optional;

@Controller
public class TestAuthController {

    private final AuthService authService;

    public TestAuthController(AuthService authService) {
        this.authService = authService;
    }

    // Эндпоинт, который имитирует сайт: принимает токен из URL и проверяет его
    @GetMapping("/test-verify")  // Например, http://localhost:8080/test-verify?token=jwt-token
    public String testVerify(@RequestParam(required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("status", "failure");
            model.addAttribute("message", "Токен отсутствует в URL.");
            return "test-verify";  // Рендерим страницу с ошибкой
        }

        Optional<AuthResponse> authData = authService.validateAndGetUser(token);
        if (authData.isEmpty()) {
            model.addAttribute("status", "failure");
            model.addAttribute("message", "Авторизация провалена: недействительный или истекший токен.");
        } else {
            AuthResponse response = authData.get();
            model.addAttribute("status", "success");
            model.addAttribute("message", "Авторизация успешна!");
            model.addAttribute("userId", response.getUserId());
            model.addAttribute("phone", response.getPhone());
        }

        return "test-verify";  // Рендерим шаблон с результатом
    }
}
