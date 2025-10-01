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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  // Замени System.out на logger
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
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class RedirectController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);  // Логгер

    @Value("${telegram.bot.token}")
    private String botToken;

    private final Gson gson = new Gson();

    @GetMapping("/redirect")
    public String handleRedirect(@RequestParam(required = false) String initData, Model model) {
        logger.info("Получен initData: {}", initData);  // Лог initData

        if (initData == null || initData.isEmpty()) {
            model.addAttribute("error", "initData не найден. Откройте приложение в Telegram.");
            return "redirect-view";
        }

        try {
            // Верификация initData
            boolean isValid = verifyInitData(initData, botToken);
            logger.info("Верификация initData: {}", isValid);  // Лог результата верификации
            if (!isValid) {
                model.addAttribute("error", "Неверные данные. Попробуйте снова.");
                return "redirect-view";
            }

            // Парсинг initData
            Map<String, String> params = parseInitData(initData);
            String userJson = params.get("user");

            if (userJson != null) {
                Map<String, Object> userData = gson.fromJson(userJson, new TypeToken<Map<String, Object>>(){}.getType());
                model.addAttribute("userData", userData);
                logger.info("Успешно распарсен userData: {}", userData);  // Лог данных пользователя
            } else {
                model.addAttribute("error", "Информация о пользователе не найдена.");
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки initData: {}", e.getMessage(), e);  // Лог ошибки
            model.addAttribute("error", "Ошибка обработки: " + e.getMessage());
        }

        return "redirect-view";
    }

    private boolean verifyInitData(String initData, String botToken) throws Exception {
        // Получаем секрет: SHA256-хэш бот-токена
        String secretKeyHex = getSecretKey(botToken);
        logger.debug("Секретный ключ (SHA256 токена): {}", secretKeyHex);  // Лог секрета (убери в продакшене)

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

        if (hash == null) {
            logger.warn("Хэш не найден в initData");
            return false;
        }

        // Создаем строку для хэширования
        StringBuilder dataCheckString = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> dataCheckString.append(entry.getKey()).append("=").append(entry.getValue()).append("\n"));
        if (dataCheckString.length() > 0) {
            dataCheckString.setLength(dataCheckString.length() - 1);  // Убираем последний \n
        }

        logger.debug("Строка для хэширования: {}", dataCheckString.toString());  // Лог строки

        // Хэшируем с HMAC-SHA256
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(hexStringToByteArray(secretKeyHex), "HmacSHA256");
        mac.init(secretKey);
        byte[] computedHash = mac.doFinal(dataCheckString.toString().getBytes(StandardCharsets.UTF_8));
        String computedHashHex = bytesToHex(computedHash);

        logger.debug("Вычисленный хэш: {}, Ожидаемый: {}", computedHashHex, hash);  // Лог хэшей

        return computedHashHex.equals(hash);
    }

    private String getSecretKey(String botToken) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

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
