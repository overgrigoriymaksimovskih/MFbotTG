//package pro.masterfood.controller.autentification;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//public class InitDataHandler {
//
//    @PostMapping("/api/initdatahandler")
//    public String handleInitData(@RequestBody Map<String, String> payload) {
//        String initData = payload.get("initData");
//        // Здесь валидируйте initData (например, проверьте хэш с помощью Telegram Bot API)
//        // Пример: логика валидации
//        if (isValidInitData(initData)) {
//            // Сохраните данные, обработайте пользователя и т.д.
//            return "{\"status\": \"success\"}";
//        } else {
//            return "{\"status\": \"error\", \"message\": \"Invalid initData\"}";
//        }
//    }
//
//    private boolean isValidInitData(String initData) {
//        // Реализуйте валидацию по документации Telegram: https://core.telegram.org/bots/webapps#validating-data-received-via-the-web-app
//        // Например, проверьте подпись с вашим bot_token
//        return true;  // Заглушка, замените на реальную логику
//    }
//}

package pro.masterfood.controller.autentification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class InitDataHandler {

    @PostMapping("/api/initdatahandler")
    public String handleInitData(@RequestBody Map<String, String> payload) {
        StringBuilder log = new StringBuilder();
        log.append("handleInitData called\n");

        // Проверяем, тестовый ли запрос
        if (payload.containsKey("test")) {
            log.append("Test request received: ").append(payload.get("test")).append("\n");
            log.append("Endpoint is accessible!\n");
            return log.toString();
        }

        // Обычная логика для initData
        String initData = payload.get("initData");
        log.append("Received initData: ").append(initData != null ? initData : "null").append("\n");

        if (initData == null || initData.isEmpty()) {
            log.append("Error: initData is empty or null\n");
            return log.toString();
        }

        try {
            log.append("Processing initData...\n");
            log.append("Success: Data processed!\n");
            return log.toString();
        } catch (Exception e) {
            log.append("Error during processing: ").append(e.getMessage()).append("\n");
            return log.toString();
        }
    }


//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @PostMapping("/api/initdatahandler")
//    public String handleInitData(@RequestBody Map<String, String> payload) {
//        System.out.println("handleInitData called");
//        String initData = payload.get("initData");
//        System.out.println("Received initData: " + initData);
//
//        if (initData == null || initData.isEmpty()) {
//            System.out.println("initData is empty");
//            return "{\"status\": \"error\", \"message\": \"initData is empty\"}";
//        }
//
//        // Парсим initData как query string
//        Map<String, String> params = parseQueryString(initData);
//        System.out.println("Parsed initData params: " + params);
//
//        String userJsonEncoded = params.get("user");
//        if (userJsonEncoded == null) {
//            System.out.println("No user parameter found in initData");
//            return "{\"status\": \"error\", \"message\": \"No user data\"}";
//        }
//
//        try {
//            // Декодируем user JSON
//            String userJson = URLDecoder.decode(userJsonEncoded, StandardCharsets.UTF_8);
//            System.out.println("Decoded user JSON: " + userJson);
//
//            // Парсим JSON, чтобы получить user id
//            JsonNode userNode = objectMapper.readTree(userJson);
//            long userId = userNode.get("id").asLong();
//            System.out.println("Extracted userId: " + userId);
//
//            // Здесь можно добавить логику логина пользователя по userId
//            // Пока просто логируем и возвращаем success
//            return "{\"status\": \"success\", \"userId\": " + userId + "}";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "{\"status\": \"error\", \"message\": \"Failed to parse user data\"}";
//        }
//    }
//
//    private Map<String, String> parseQueryString(String query) {
//        Map<String, String> map = new HashMap<>();
//        String[] pairs = query.split("&");
//        for (String pair : pairs) {
//            int idx = pair.indexOf('=');
//            if (idx > 0 && idx < pair.length() - 1) {
//                String key = pair.substring(0, idx);
//                String value = pair.substring(idx + 1);
//                map.put(key, value);
//            }
//        }
//        return map;
//    }
//
//    private boolean isValidInitData(String initData) {
//        // Пока заглушка
//        return true;
//    }
}
