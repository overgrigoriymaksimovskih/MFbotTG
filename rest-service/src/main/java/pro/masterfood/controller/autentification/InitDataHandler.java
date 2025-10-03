//package pro.masterfood.controller.autentification;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.digest.HmacAlgorithms;
//import org.apache.commons.codec.digest.HmacUtils;
//
//import pro.masterfood.service.RedirectService;
//
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.List;
//
//@RestController
//public class InitDataHandler {
//
//    private final RedirectService redirectService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public InitDataHandler(RedirectService redirectService) {
//        this.redirectService = redirectService;
//    }
//
//    @Value("${telegram.bot.token}")
//    private String BOT_TOKEN;
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
//        // Проверяем, есть ли hash и user
//        String hash = params.get("hash");
//        String userJsonEncoded = params.get("user");
//        if (hash == null || userJsonEncoded == null) {
//            System.out.println("Missing hash or user in initData");
//            return "{\"status\": \"error\", \"message\": \"Invalid initData: missing hash or user\"}";
//        }
//
//        // Верифицируем hash
//        if (!isValidInitData(params, BOT_TOKEN)) {
//            System.out.println("Invalid hash: initData not from this bot");
//            return "{\"status\": \"error\", \"message\": \"Invalid initData: not from this bot\"}";
//        }
//
//        // Опционально: проверьте auth_date (не старше 24 часов)
//        String authDateStr = params.get("auth_date");
//        if (authDateStr != null) {
//            try {
//                long authDate = Long.parseLong(authDateStr);
//                long currentTime = System.currentTimeMillis() / 1000;
//                if (currentTime - authDate > 86400) { // 24 часа
//                    System.out.println("auth_date is too old");
//                    return "{\"status\": \"error\", \"message\": \"initData is expired\"}";
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid auth_date format");
//                return "{\"status\": \"error\", \"message\": \"Invalid auth_date\"}";
//            }
//        }
//
//        try {
//            // Декодируем user JSON
//            String userJson = URLDecoder.decode(userJsonEncoded, StandardCharsets.UTF_8);
//            System.out.println("Decoded user JSON: " + userJson);
//
//            // Парсим JSON, чтобы получить user id как long
//            JsonNode userNode = objectMapper.readTree(userJson);
//            long userId = userNode.get("id").asLong(); // Уже long, как вы хотели
//            System.out.println("Extracted userId: " + userId);
//
//            // Здесь можно добавить логику логина пользователя по userId
//            // Пока просто логируем и возвращаем success
////            return "{\"status\": \"success\", \"userId\": " + userId + "}";
//            return redirectService.producerAnswer(userId);
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
//    // Метод для верификации initData
//    private boolean isValidInitData(Map<String, String> params, String botToken) {
//        try {
//            // Вычисляем секретный ключ: SHA256 от botToken
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));
//
//            // Собираем строку для подписи: сортируем ключи кроме hash и signature, декодируем значения, и соединяем key=value\n
//            List<String> keys = new ArrayList<>(params.keySet());
//            keys.remove("hash");
//            keys.remove("signature");
//            Collections.sort(keys);
//
//            StringBuilder dataCheckString = new StringBuilder();
//            for (String key : keys) {
//                String value = params.get(key);
//                // URL-декодируем значение
//                value = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
//                dataCheckString.append(key).append("=").append(value).append("\n");
//            }
//            // Убираем последний \n
//            if (dataCheckString.length() > 0) {
//                dataCheckString.setLength(dataCheckString.length() - 1);
//            }
//
//            // Для отладки: выведите dataCheckString и calculatedHash
//            System.out.println("Data check string (decoded): " + dataCheckString.toString());
//            HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
//            String calculatedHash = Hex.encodeHexString(hmac.hmac(dataCheckString.toString().getBytes(StandardCharsets.UTF_8)));
//            System.out.println("Calculated hash: " + calculatedHash);
//            System.out.println("Received hash: " + params.get("hash"));
//
//            // Сравниваем с полученным hash
//            return calculatedHash.equals(params.get("hash"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}

package pro.masterfood.controller.autentification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import pro.masterfood.service.RedirectService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class InitDataHandler {

    private final RedirectService redirectService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InitDataHandler(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    @PostMapping("/api/initdatahandler")
    public String handleInitData(@RequestBody Map<String, String> payload) {
        System.out.println("handleInitData called");
        String initData = payload.get("initData");
        System.out.println("Received initData: " + initData);

        if (initData == null || initData.isEmpty()) {
            System.out.println("initData is empty");
            return "{\"status\": \"error\", \"message\": \"initData is empty\"}";
        }

        // Парсим initData как query string (с однократным URL-декодированием значений)
        Map<String, String> params = parseQueryString(initData);
        System.out.println("Parsed initData params: " + params);

        // Проверяем, есть ли hash и user
        String hash = params.get("hash");
        String userJsonEncoded = params.get("user");
        if (hash == null || userJsonEncoded == null) {
            System.out.println("Missing hash or user in initData");
            return "{\"status\": \"error\", \"message\": \"Invalid initData: missing hash or user\"}";
        }

        // Верифицируем hash
        if (!isValidInitData(params, BOT_TOKEN)) {
            System.out.println("Invalid hash: initData not from this bot");
            return "{\"status\": \"error\", \"message\": \"Invalid initData: not from this bot\"}";
        }

        // Опционально: проверьте auth_date (не старше 24 часов)
        String authDateStr = params.get("auth_date");
        if (authDateStr != null) {
            try {
                long authDate = Long.parseLong(authDateStr);
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime - authDate > 86400) { // 24 часа
                    System.out.println("auth_date is too old");
                    return "{\"status\": \"error\", \"message\": \"initData is expired\"}";
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid auth_date format");
                return "{\"status\": \"error\", \"message\": \"Invalid auth_date\"}";
            }
        }

        try {
            // Декодируем user JSON (теперь user уже однократно декодирован в params, но для него нужно двойное декодирование)
            String userJson = URLDecoder.decode(userJsonEncoded, StandardCharsets.UTF_8);
            System.out.println("Decoded user JSON: " + userJson);

            // Парсим JSON, чтобы получить user id как long
            JsonNode userNode = objectMapper.readTree(userJson);
            long userId = userNode.get("id").asLong(); // Уже long, как вы хотели
            System.out.println("Extracted userId: " + userId);

            // Здесь можно добавить логику логина пользователя по userId
            // Пока просто логируем и возвращаем success
//            return "{\"status\": \"success\", \"userId\": " + userId + "}";
            return redirectService.producerAnswer(userId);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to parse user data\"}";
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                // Однократное URL-декодирование значения
                try {
                    value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    // Если декодирование не удалось, оставляем как есть
                }
                map.put(key, value);
            }
        }
        return map;
    }

    // Метод для верификации initData (исправленный: исключаем "signature", правильный порядок HMAC)
    private boolean isValidInitData(Map<String, String> params, String botToken) {
        try {
            // Вычисляем секретный ключ: HMAC-SHA256("WebAppData", botToken) — порядок key="WebAppData", data=botToken
            byte[] secretKey = HmacUtils.hmacSha256("WebAppData".getBytes(StandardCharsets.UTF_8), botToken.getBytes(StandardCharsets.UTF_8));

            // Создаём копию параметров и исключаем "hash" и "signature"
            Map<String, String> paramsCopy = new HashMap<>(params);
            paramsCopy.remove("hash");
            paramsCopy.remove("signature");  // Добавлено: исключаем "signature", так как оно не участвует в data-check-string

            // Для параметра "user" выполняем дополнительное URL-декодирование (двойное для JSON)
            if (paramsCopy.containsKey("user")) {
                String userValue = paramsCopy.get("user");
                try {
                    paramsCopy.put("user", URLDecoder.decode(userValue, StandardCharsets.UTF_8));
                } catch (Exception e) {
                    // Если декодирование не удалось, оставляем как есть
                }
            }

            // Собираем строку для подписи: сортируем ключи, формируем key=value\n и соединяем
            List<String> keys = new ArrayList<>(paramsCopy.keySet());
            Collections.sort(keys);

            StringBuilder dataCheckString = new StringBuilder();
            for (String key : keys) {
                String value = paramsCopy.get(key);
                dataCheckString.append(key).append("=").append(value).append("\n");
            }
            // Убираем последний \n
            if (dataCheckString.length() > 0) {
                dataCheckString.setLength(dataCheckString.length() - 1);
            }

            // Для отладки: выведите dataCheckString и calculatedHash
            System.out.println("Data check string: " + dataCheckString.toString());
            HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
            String calculatedHash = Hex.encodeHexString(hmac.hmac(dataCheckString.toString().getBytes(StandardCharsets.UTF_8)));
            System.out.println("Calculated hash: " + calculatedHash);
            System.out.println("Received hash: " + params.get("hash"));

            // Сравниваем с полученным hash
            return calculatedHash.equals(params.get("hash"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

