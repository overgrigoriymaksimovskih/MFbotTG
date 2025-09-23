package pro.masterfood.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.configuration.RestServiceConfiguration;  // Импорт ServerConfiguration
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequiredArgsConstructor
@Component
public class CustomPostClient {

    private static final Logger log = LoggerFactory.getLogger(CustomPostClient.class);
    private final RestTemplate restTemplate;
    private final RestServiceConfiguration restServiceConfiguration;  // Внедрение ServerConfiguration

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
}
