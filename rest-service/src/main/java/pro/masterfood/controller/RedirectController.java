//package pro.masterfood.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.view.RedirectView;
//
//@RequestMapping("/api")
//@RestController
//public class OauthUserController {
//
//    @Value("${redirect.link}")
//    private String redirectLink;
//    @GetMapping("/redirect")
//    public RedirectView redirectToMasterFood() {
//        return new RedirectView(redirectLink);
//    }
//}

package pro.masterfood.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class RedirectController {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final Gson gson = new Gson();  // Для парсинга JSON

    @GetMapping("/redirect")
    public String handleRedirect(@RequestParam(required = false) String initData, Model model) {
        if (initData == null || initData.isEmpty()) {
            model.addAttribute("error", "initData не найден. Откройте приложение в Telegram.");
            return "redirect-view";
        }

        try {
            // Верификация initData
            if (!verifyInitData(initData, botToken)) {
                model.addAttribute("error", "Неверные данные. Попробуйте снова.");
                return "redirect-view";
            }

            // Парсинг initData
            Map<String, String> params = parseInitData(initData);
            String userJson = params.get("user");

            if (userJson != null) {
                // Парсим user JSON в Map
                Map<String, Object> userData = gson.fromJson(userJson, new TypeToken<Map<String, Object>>(){}.getType());
                model.addAttribute("userData", userData);
            } else {
                model.addAttribute("error", "Информация о пользователе не найдена.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка обработки: " + e.getMessage());
        }

        return "redirect-view";
    }

    // Метод верификации initData (без изменений)
    private boolean verifyInitData(String initData, String botToken) throws Exception {
        String[] parts = initData.split("&");
        Map<String, String> params = new HashMap<>();
        String hash = null;

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0];
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                if ("hash".equals(key)) {
                    hash = value;
                } else {
                    params.put(key, value);
                }
            }
        }

        if (hash == null) return false;

        // Создаем строку для хэширования
        StringBuilder dataCheckString = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> dataCheckString.append(entry.getKey()).append("=").append(entry.getValue()).append("\n"));
        dataCheckString.setLength(dataCheckString.length() - 1);  // Убираем последний \n

        // Хэшируем с HMAC-SHA256
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(botToken.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] computedHash = mac.doFinal(dataCheckString.toString().getBytes(StandardCharsets.UTF_8));
        String computedHashHex = bytesToHex(computedHash);

        return computedHashHex.equals(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Парсинг initData в Map (без изменений)
    private Map<String, String> parseInitData(String initData) {
        Map<String, String> params = new HashMap<>();
        String[] parts = initData.split("&");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}


