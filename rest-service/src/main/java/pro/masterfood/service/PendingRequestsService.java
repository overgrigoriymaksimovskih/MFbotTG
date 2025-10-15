package pro.masterfood.service;

import org.springframework.web.context.request.async.DeferredResult;
import pro.masterfood.dto.ProcessingResponseToOneS_Rest;

public interface PendingRequestsService {

    // Метод для добавления нового DeferredResult
    void addPendingRequest(String correlationId, DeferredResult<ProcessingResponseToOneS_Rest> deferredResult);

    // Метод для получения и удаления DeferredResult при ответе
    DeferredResult<ProcessingResponseToOneS_Rest> getAndRemoveDeferredResult(String correlationId);

    // Метод для обработки таймаута (можно вызвать из контроллера)
    void handleTimeout(String correlationId);

    void handleError(String correlationId, Throwable throwable);
}
