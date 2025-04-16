package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hashids.Hashids;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.LoginParams;
import pro.masterfood.dto.MailParams;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.AppUserService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static pro.masterfood.enums.UserState.*;

@Component
public class AppUserServiceImpl implements AppUserService {
    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;
    private final AppUserDAO appUserDAO;
    private final Hashids hashids;

    public AppUserServiceImpl(AppUserDAO appUserDAO, RabbitTemplate rabbitTemplate, Hashids hashids) {
        this.appUserDAO = appUserDAO;
        this.rabbitTemplate = rabbitTemplate;
        this.hashids = hashids;
    }

    @Value("${spring.rabbitmq.queues.login}")
    private String registrationLoginQueue;


    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегистрированы";
        } else if (appUser.getEmail() != null){
            return  "Вам на почту уже было выслано письмо \n"
                    + " перейдите по ссылке для завершения регистрации";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
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
//        var appUserOpt = appUserDAO.findByEmail(email);
//        if (appUserOpt.isEmpty()) {
//            appUser.setEmail(email);
//            appUser.setState(BASIC_STATE);
//            appUser = appUserDAO.save(appUser);
//
//            var cryptoUserId = hashids.encode(appUser.getId());
//            sendRegistrationMail(cryptoUserId, email);
//
//            return "Вам на почту было выслано письмо \n"
//                    + " перейдите по ссылке в письме для завершения регистрации";
//        } else {
//            return "Этот e-mail уже используется, введите корректный адрес эл. почты... \n"
//                    + "Для отмены команды введите /cancel";
//        }
        appUser.setEmail(email);
        appUser.setState(WAIT_FOR_PASSWORD_STATE);
        appUserDAO.save(appUser);
        return "Введите пароль";
    }
    @Override
    public String checkPassword(Long chatId, AppUser appUser, String password) {
        var loginParams = LoginParams.builder()
                .email(appUser.getEmail())
                .password(password)
                .chatId(chatId)
                .build();
        rabbitTemplate.convertAndSend(registrationLoginQueue, loginParams);
        return "Отправлено на проверку...";
    }

//    private void sendRegistrationMail(String cryptoUserId, String email) {
//        var mailParams = MailParams.builder()
//                .id(cryptoUserId)
//                .emailTo(email)
//                .build();
//        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
//    }
}
