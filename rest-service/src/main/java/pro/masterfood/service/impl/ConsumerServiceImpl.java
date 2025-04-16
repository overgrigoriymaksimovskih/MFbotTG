package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dto.LoginParams;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.UserActivationService;


@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeRequestToREST(LoginParams loginParams){
        userActivationService.consumeLogin(loginParams);
    }
}
