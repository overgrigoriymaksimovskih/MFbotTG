package pro.masterfood.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import pro.masterfood.dto.ProcessingRequestFromOneS_Queue;
import pro.masterfood.dto.ProcessingResponseToOneS_Rest;
import pro.masterfood.service.PendingRequestsService;
import pro.masterfood.service.ProducerService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProcessingFromOneSController {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private PendingRequestsService pendingRequestsService;

    @Autowired
    private ObjectMapper objectMapper;

    // Читаем API-ключ из application.properties
    @Value("${app.api.key}")
    private String apiKey;

    @PostMapping("/process")
    public DeferredResult<ProcessingResponseToOneS_Rest> processRequest(
            @RequestHeader(value = "X-API-Key", required = true) String requestApiKey,
            @RequestBody String requestJson) {  // Принимаем сырую JSON-строку

        DeferredResult<ProcessingResponseToOneS_Rest> deferredResult = new DeferredResult<>(30000L);

        // 1. Проверяем API-ключ
        if (!apiKey.equals(requestApiKey)) {
            deferredResult.setErrorResult(new RuntimeException("Invalid API key"));
            return deferredResult;
        }

        // 2. Проверяем, что строка в JSON-формате
        try {
            objectMapper.readTree(requestJson);  // Проверяем валидность JSON
        } catch (JsonProcessingException e) {
            deferredResult.setErrorResult(new RuntimeException("Invalid JSON format"));
            return deferredResult;
        }

        // 3. Проверяем, что объект создаётся из JSON-строки и ни одно поле не null
        ProcessingRequestFromOneS_Queue request;
        try {
            request = objectMapper.readValue(requestJson, ProcessingRequestFromOneS_Queue.class);
        } catch (JsonProcessingException e) {
            deferredResult.setErrorResult(new RuntimeException("Failed to deserialize JSON to object"));
            return deferredResult;
        }
        // Ручная проверка полей (messageText не null/пустое, userList не null)
        if (request.getMessageText() == null || request.getMessageText().trim().isEmpty() || request.getUserList() == null) {
            deferredResult.setErrorResult(new RuntimeException("Required fields are null or empty"));
            return deferredResult;
        }

        // Теперь объект валиден — присваиваем в validatedRequest для ясности
        ProcessingRequestFromOneS_Queue validatedRequest = request;

        // 4. Генерируем correlationId
        String correlationId = UUID.randomUUID().toString();
        validatedRequest.setCorrelationId(correlationId);

        // 5. Сохраняем DeferredResult
        pendingRequestsService.addPendingRequest(correlationId, deferredResult);

        // 6. Сериализуем и отправляем в очередь (сериализуем весь валидированный DTO-объект)
        try {
            String message = objectMapper.writeValueAsString(validatedRequest);
            producerService.producerFromOneS(message);
        } catch (JsonProcessingException e) {
            deferredResult.setErrorResult(new RuntimeException("Failed to serialize request"));
            pendingRequestsService.handleError(correlationId, e);
            return deferredResult;
        } catch (AmqpException e) {
            deferredResult.setErrorResult(new RuntimeException("Failed to send to queue"));
            pendingRequestsService.handleError(correlationId, e);
            return deferredResult;
        }

        // Обработка таймаута и ошибок
        deferredResult.onTimeout(() -> {
            pendingRequestsService.handleTimeout(correlationId);
        });

        deferredResult.onError((throwable) -> {
            pendingRequestsService.handleError(correlationId, throwable);
        });

        return deferredResult;
    }
}
