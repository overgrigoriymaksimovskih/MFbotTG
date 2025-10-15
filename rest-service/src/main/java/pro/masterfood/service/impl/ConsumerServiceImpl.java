package pro.masterfood.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dto.ProcessingResponseToOneS_Queue;
import pro.masterfood.dto.ProcessingResponseToOneS_Rest;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.enums.RequestsToREST;
import pro.masterfood.service.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;
    private final PhoneHandler phoneHandler;
    private final UserInformationProvider userInformationProvider;

    private final PendingRequestsService pendingRequestsService;
    private final ObjectMapper objectMapper;  // Для десериализации JSON


    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeRequestToREST(RequestParams requestParams){
        try {
            if(RequestsToREST.LOGIN_REQUEST.equals(requestParams.getRequestType())){
                userActivationService.consumeLogin(requestParams);
            } else if (RequestsToREST.CHECK_CONTACT_REQUEST.equals(requestParams.getRequestType())) {
                userActivationService.consumeContact(requestParams);

            } else if (RequestsToREST.CHECK_PHONE_REQUEST.equals(requestParams.getRequestType())) {
                phoneHandler.handlePhone(requestParams);
            } else if (RequestsToREST.CHECK_SMS_REQUEST.equals(requestParams.getRequestType())) {
                userActivationService.consumeSMS(requestParams);

            } else if (RequestsToREST.PRESENTS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetBalance(requestParams);
            } else if (RequestsToREST.ORDER_STATUS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetOrderStatus(requestParams);
            }else{
                userActivationService.sendAnswer("Сервис не может обработать сообщение " + requestParams.getRequestType(), requestParams.getChatId());
            }
        } catch (Exception e) {
            userActivationService.sendAnswer("Ошибка при обработке запроса: " + e.getMessage(), requestParams.getChatId());
        }
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-to-1C}")
    public void handleAnswerFromNodeToRestForOneS(String message) {
        try {
            // Десериализуем JSON в DTO из очереди
            ProcessingResponseToOneS_Queue processingResponse = objectMapper.readValue(message, ProcessingResponseToOneS_Queue.class);

            // Извлекаем correlationId (нужен только для поиска DeferredResult)
            String correlationId = processingResponse.getCorrelationId();

            // Получаем и удаляем DeferredResult
            var deferredResult = pendingRequestsService.getAndRemoveDeferredResult(correlationId);

            if (deferredResult != null) {
                // Создаём ответ для клиента на основе твоего DTO
                ProcessingResponseToOneS_Rest clientResponse = new ProcessingResponseToOneS_Rest(
                        processingResponse.getMessageFromNode(),  // Маппим в result
                        processingResponse.getProcessedUsers()   // Маппим в userList
                );

                // Завершаем DeferredResult с клиентским ответом
                deferredResult.setResult(clientResponse);
//                log.info("DeferredResult completed for correlationId: {} (client response prepared)", correlationId);
            } else {
//                log.warn("DeferredResult not found for correlationId: {} (possibly timed out)", correlationId);
            }

        } catch (Exception e) {
//            log.error("Error processing message from queue: {}", e.getMessage(), e);
            // Опционально: DLQ или retry
        }
    }
}
