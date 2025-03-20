package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.MailParams;
import pro.masterfood.entity.AppUser;
import pro.masterfood.entity.enums.UserState;
import pro.masterfood.service.AppUserService;
import pro.masterfood.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Component
public class AppUserServiceImpl implements AppUserService {
    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool, RabbitTemplate rabbitTemplate) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;


    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегистрированы";
        } else if (appUser.getEmail() != null){
            return  "Вам на почту уже было выслано письмо \n"
                    + " перейдите по ссылке для завершения регистрации";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите e-mail";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try{
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e){
            return "Введите пожалуйста корректный адрес. Для отмены команды введите /cancel";
        }
        var appUserOpt = appUserDAO.findByEmail(email);
        if (appUserOpt.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email);
            return "Вам на почту было выслано письмо \n"
                    + " перейдите по ссылке в письме для завершения регистрации";
        } else {
            return "Этот e-mail уже используется, введите корректный адрес эл. почты... \n"
                    + "Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}
