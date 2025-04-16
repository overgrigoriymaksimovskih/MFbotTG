package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.LoginParams;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserActivationService;

import java.util.Map;
import java.util.Optional;

import static pro.masterfood.enums.UserState.BASIC_STATE;
import static pro.masterfood.enums.UserState.WAIT_FOR_EMAIL_STATE;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;
//    private final AppUserDAO appUserDAO;
//    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeLogin(LoginParams loginParams){
        userActivationService.consumeLogin(loginParams);
    }
}
