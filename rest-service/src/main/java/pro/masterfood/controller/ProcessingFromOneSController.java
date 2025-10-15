package pro.masterfood.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    @PostMapping("/process")
    public DeferredResult<ProcessingResponseToOneS_Rest> processRequest(@RequestBody String jsonData) {  // Изменён тип возврата
        DeferredResult<ProcessingResponseToOneS_Rest> deferredResult = new DeferredResult<>(30000L);  // Изменён тип

        // Генерируем correlationId
        String correlationId = UUID.randomUUID().toString();

        // Создаём объект запроса
        ProcessingRequestFromOneS_Queue request = new ProcessingRequestFromOneS_Queue(correlationId, jsonData);

        // Сохраняем DeferredResult (сервис должен принимать обобщённый тип, см. ниже)
        pendingRequestsService.addPendingRequest(correlationId, deferredResult);

        // Сериализуем и отправляем в очередь
        try {
            String correlationIdAndRequest = objectMapper.writeValueAsString(request);
            producerService.producerFromOneS(correlationIdAndRequest);
        } catch (JsonProcessingException e) {
            deferredResult.setErrorResult(new RuntimeException("Failed to serialize request"));
            return deferredResult;
        }

        // Обработка таймаута и ошибок (теперь для твоего типа, но логика та же)
        deferredResult.onTimeout(() -> {
            pendingRequestsService.handleTimeout(correlationId);
        });

        deferredResult.onError((throwable) -> {
            pendingRequestsService.handleError(correlationId, throwable);
        });

        return deferredResult;
    }

}
