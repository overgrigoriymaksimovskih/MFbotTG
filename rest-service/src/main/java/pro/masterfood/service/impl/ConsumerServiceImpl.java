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





@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;
    private final UserInformationProvider userInformationProvider;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeRequestToREST(RequestParams requestParams){
        try {
            if(RequestsToREST.LOGIN_REQUEST.equals(requestParams.getRequestType())){
                userActivationService.consumeLogin(requestParams);
            } else if (RequestsToREST.PRESENTS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetBalance(requestParams);
            } else if (RequestsToREST.ORDER_STATUS_REQUEST.equals(requestParams.getRequestType())) {
                userInformationProvider.consumeGetOrderStatus(requestParams);
            }else{
                userActivationService.sendAnswer("Сервис не может обработать сообщение " + requestParams.getRequestType(), requestParams.getChatId());
            }
        } catch (Exception e) {
            userActivationService.sendAnswer("Ошибка при обработке запроса " + e.getMessage(), requestParams.getChatId());
        }
    }
}
