package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import pro.masterfood.dto.ProcessingResponseToOneS_Queue;
import pro.masterfood.dto.ProcessingResponseToOneS_Rest;
import pro.masterfood.service.PendingRequestsService;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRequestsServiceImpl implements PendingRequestsService {
    // Это карта (словарь), где ключ — уникальный correlationId (строка),
    // а значение — объект DeferredResult, который "ждёт" результата.
    // ConcurrentHashMap — потокобезопасная (можно использовать из разных потоков без проблем).
    private final ConcurrentHashMap<String, DeferredResult<ProcessingResponseToOneS_Rest>> pendingRequests = new ConcurrentHashMap<>();

    // Метод для добавления нового "ожидающего" запроса.
    // Когда контроллер получает запрос, он генерирует correlationId и создаёт DeferredResult.
    // Этот метод сохраняет их в карту, чтобы потом найти по correlationId.
    @Override
    public void addPendingRequest(String correlationId, DeferredResult<ProcessingResponseToOneS_Rest> deferredResult) {
        pendingRequests.put(correlationId, deferredResult);
    }

    // Метод для получения и удаления DeferredResult по correlationId.
    // Когда listener получает ответ из RabbitMQ, он вызывает этот метод,
    // чтобы найти "ожидающий" запрос и установить результат.
    // remove() удаляет из карты, чтобы не было утечек памяти.
    @Override
    public DeferredResult<ProcessingResponseToOneS_Rest> getAndRemoveDeferredResult(String correlationId) {
        return pendingRequests.remove(correlationId);
    }

    // Метод для обработки таймаута.
    // Если ответ не приходит вовремя (например, через 30 секунд), контроллер вызывает это.
    // Мы удаляем DeferredResult из карты и устанавливаем ошибку (клиент получит ошибку).
    @Override
    public void handleTimeout(String correlationId) {
        DeferredResult<ProcessingResponseToOneS_Rest> deferredResult = pendingRequests.remove(correlationId);
        if (deferredResult != null) {
            deferredResult.setErrorResult(new RuntimeException("Request timed out"));
        }
    }

    @Override
    public void handleError(String correlationId, Throwable throwable) {
        DeferredResult<ProcessingResponseToOneS_Rest> deferredResult = pendingRequests.remove(correlationId);
        if (deferredResult != null) {
            deferredResult.setErrorResult(throwable);
        }
    }
}
