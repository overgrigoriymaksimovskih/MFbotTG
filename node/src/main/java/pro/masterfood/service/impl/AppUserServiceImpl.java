package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hashids.Hashids;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.dto.MailParams;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;
import pro.masterfood.enums.RequestsToREST;
import pro.masterfood.service.AppUserService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.List;

import static pro.masterfood.enums.UserState.*;

@Component
public class AppUserServiceImpl implements AppUserService {
    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;
    private final AppUserDAO appUserDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final Hashids hashids;

    public AppUserServiceImpl(AppUserDAO appUserDAO, RabbitTemplate rabbitTemplate, AppPhotoDAO appPhotoDAO, Hashids hashids) {
        this.appUserDAO = appUserDAO;
        this.rabbitTemplate = rabbitTemplate;
        this.appPhotoDAO = appPhotoDAO;
        this.hashids = hashids;
    }

    @Value("${spring.rabbitmq.queues.login}")
    private String registrationLoginQueue;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;


    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегистрированы";
        } else if (appUser.getEmail() != null && !appUser.getIsActive()){
            appUser.setState(WAIT_FOR_PASSWORD_STATE);
            appUserDAO.save(appUser);
            return  "Ваш email " +  appUser.getEmail() +"\n"
                    + " введите пароль";
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
//        String email = appUser.getEmail();
//        sendLoginPassword(email, password);
        var loginParams = RequestParams.builder()
                .requestType(RequestsToREST.LOGIN_REQUEST)
                .id(appUser.getId())
                .chatId(chatId)
                .email(appUser.getEmail())
                .password(password)
                .build();
        rabbitTemplate.convertAndSend(registrationLoginQueue, loginParams);
        return "Отправлено на проверку...";
    }

    @Override
    public String checkBalance(Long chatId, AppUser appUser) {
        var getBalanceParams = RequestParams.builder()
                .requestType(RequestsToREST.PRESENTS_REQUEST)
                .id(appUser.getId())
                .chatId(chatId)
                .build();
        rabbitTemplate.convertAndSend(registrationLoginQueue, getBalanceParams);
        return "Уточняем...";
    }

    @Override
    public String checkStatus(Long chatId, AppUser appUser) {
        var getOrderStatus = RequestParams.builder()
                .requestType(RequestsToREST.ORDER_STATUS_REQUEST)
                .id(appUser.getId())
                .chatId(chatId)
                .build();
        rabbitTemplate.convertAndSend(registrationLoginQueue, getOrderStatus);
        return "Уточняем...";
    }

//    private void sendLoginPassword(String email, String password) {
//        var loginParams = LoginParams.builder()
//                .email(email)
//                .password(password)
//                .build();
//        rabbitTemplate.convertAndSend(registrationLoginQueue, loginParams);
//    }

//    private void sendRegistrationMail(String cryptoUserId, String email) {
//        var mailParams = MailParams.builder()
//                .id(cryptoUserId)
//                .emailTo(email)
//                .build();
//        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
//    }

    @Override
    @Transactional
    public String sendReportMail(Long chatId, AppUser appUser) {
            Long userId = appUser.getId();
        //Начинаем цирк с конями... Сейчас сохраним сюда ИД юзера, чтобы ниже получить его же из БД
        // а все это потому что хибернейт не может связать две сущности вызванные в рамках разных сессий
        // поэтому надо получать из БД пользователя и лист его фоток в одной сессии
        try {
            AppUser appUsero = appUserDAO.getById(1L);
            List<AppPhoto> appPhotos = appUsero.getPhotos();

            // Создаем List<byte[]> для всех вложений
            List<byte[]> attachments = new ArrayList<>();
            for (AppPhoto appPhoto : appPhotos) {
                byte[] binaryContent = appPhoto.getBinaryContent().getFileAsArrayOfBytes();
                attachments.add(binaryContent);
            }

            var mailParams = MailParams.builder()
                    .id(appUser.getId())
                    .chatId(chatId)
                    .email(appUser.getEmail())
                    .siteUid((appUser.getSiteUserId()))
                    .phoneNumber(appUser.getPhoneNumber())
                    .message("qwerty")
                    .photos(attachments)
                    .build();
            rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
            for (AppPhoto appPhoto : appPhotos) {
                appPhotoDAO.delete(appPhoto);
            }
            return "Отправляем в очередь registrationMailQueue, mailParams";

        } catch (RuntimeException e) {

            return "error in sendReportMail" + e.getMessage();
        }

    }
}
