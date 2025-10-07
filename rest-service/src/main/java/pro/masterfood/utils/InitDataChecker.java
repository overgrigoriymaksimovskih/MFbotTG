package pro.masterfood.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Component
public class InitDataChecker {
    public boolean validate(String initData, String botToken) {
        try {
            // Шаг 1: Парсинг init data
            Map<String, String> params = parseInitData(initData);
            String receivedHash = params.remove("hash"); // Извлечь и удалить hash
            if (receivedHash == null) return false;

            // Шаг 2: Создание массива пар ключ=значение (исключая hash)
            List<String> pairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(entry.getKey() + "=" + entry.getValue());
            }

            // Шаг 3: Сортировка в алфавитном порядке и объединение через \n
            Collections.sort(pairs);
            String dataCheckString = String.join("\n", pairs);

            // Шаг 4: Создание сигнатуры бота (HMAC-SHA256(botToken, "WebAppData"))
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] botKey = mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8)); // Это ключевое значение

            // Шаг 5: Создание финальной HMAC сигнатуры (HMAC-SHA256(botKey, dataCheckString))
            SecretKeySpec finalKey = new SecretKeySpec(botKey, "HmacSHA256");
            mac.init(finalKey);
            byte[] finalHash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            // Шаг 6: Конвертация в hex и сравнение
            String computedHash = bytesToHex(finalHash);
            System.out.println(computedHash);
            System.out.println(receivedHash);
            return computedHash.equals(receivedHash);

        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, String> parseInitData(String initData) throws Exception {
        Map<String, String> map = new HashMap<>();
        String[] pairs = initData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString());
                map.put(key, value);
            }
        }
        return map;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
