package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.enums.RequestsToREST;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.service.UserInformationProvider;

import com.rabbitmq.client.Channel;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.amqp.support.AmqpHeaders;
import java.util.Map;
import java.io.IOException;





@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;
    private final UserInformationProvider userInformationProvider;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeRequestToREST(RequestParams requestParams, Channel channel, @Headers Map<String, Object> headers){

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            if(RequestsToREST.LOGIN_REQUEST.equals(requestParams.getRequestType())){
                userActivationService.consumeLogin(requestParams);
            } else if (RequestsToREST.PRESENTS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetBalance(requestParams);
            } else if (RequestsToREST.ORDER_STATUS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetOrderStatus(requestParams);
            }
//            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            try {
                channel.basicNack(deliveryTag, false, false); // Отклоняем сообщение, requeue=false - СООБЩЕНИЕ БУДЕТ УДАЛЕНО
            } catch (IOException ex) {
                System.err.println("Error sending Nack: " + ex.getMessage());
                // В этом месте ничего не можем сделать, ошибка отправки Nack маловероятна
            }
        }
    }
}
