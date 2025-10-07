package pro.masterfood.controller.autentification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pro.masterfood.service.RedirectService;

import java.util.*;

@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    @PostMapping("/api/initdatahandler")
    public String handleInitData(@RequestBody Map<String, String> payload) {
        String initData = payload.get("initData");
//        System.out.println("Received initData: " + initData);
        if (initData == null || initData.isEmpty()) {
            System.out.println("initData is empty");
            return "{\"status\": \"error\", \"message\": \"initData is empty\"}";

        }else if(!redirectService.validateInitData(initData, BOT_TOKEN)){
            return "{\"status\": \"error\", \"message\": \"Invalid initData: not from this bot\"}";
        }

        // Генерируем токен и редирект-URL
        String token = redirectService.generateToken(initData);
        if (token.contains("Ошибка")) {
            return "{\"status\": \"error\", \"message\": \"" + token + "\"}";
        }

        // Предполагаем, что ваш сайт — это, например, https://your-site.com
        String redirectUrl = "https://smakmart.ru/api/test-verify?token=" + token;
//        return "{\"status\": \"success\", \"redirectUrl\":" + redirectUrl + "}";
        return "{\"status\": \"success\", \"redirectUrl\": \"" + redirectUrl + "\"}";
    }
}

