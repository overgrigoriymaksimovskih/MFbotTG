package pro.masterfood.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.configuration.RestServiceConfiguration;  // Импорт ServerConfiguration
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;


@RequiredArgsConstructor
@Component
public class CustomPostClient {

    private static final Logger log = LoggerFactory.getLogger(CustomPostClient.class);
    private final RestTemplate restTemplate;
    private final RestServiceConfiguration restServiceConfiguration;  // Внедрение ServerConfiguration
    private final ObjectMapper objectMapper = new ObjectMapper();  // Для JSON сериализации/десериализации

    public String sendPostRequest(String url, String requestBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // <-- Исправлено!
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                // Обработка ошибок - лучше выбросить исключение
                System.err.println("Ошибка при отправке POST запроса. Код ответа: " + response.getStatusCode());
                return "Ошибка: " + response.getStatusCode(); // Возвращаем код ошибки
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            return "Ошибка: " + e.getMessage(); // Возвращаем сообщение об ошибке
        }
    }

    // Метод специально для тестового POST с JSON, возвращает Map
    public ResponseEntity<Map<String, Object>> sendTestPostRequest(String url, Map<String, String> postBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody = objectMapper.writeValueAsString(postBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // Используем exchange вместо postForEntity для корректной обработки Map<String, Object>
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response;
            } else {
                log.error("Ошибка при отправке тестового POST запроса. Код ответа: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации JSON: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "JSON serialization error"));
        } catch (Exception e) {
            log.error("Ошибка при отправке тестового POST: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
