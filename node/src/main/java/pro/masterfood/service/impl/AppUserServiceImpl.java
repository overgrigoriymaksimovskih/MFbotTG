package pro.masterfood.service.impl;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hashids.Hashids;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.dto.MailParams;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;
import pro.masterfood.enums.RequestsToREST;
import pro.masterfood.utils.CommandPatternChecker;
import static pro.masterfood.enums.UserState.*;
import pro.masterfood.service.AppUserService;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppUserServiceImpl implements AppUserService {
    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;
    private final AppUserDAO appUserDAO;
    private final CommandPatternChecker commandPatternChecker;
    private final AppPhotoDAO appPhotoDAO;
    private final Hashids hashids;

    public AppUserServiceImpl(AppUserDAO appUserDAO, RabbitTemplate rabbitTemplate, CommandPatternChecker commandPatternChecker, AppPhotoDAO appPhotoDAO, Hashids hashids) {
        this.appUserDAO = appUserDAO;
        this.rabbitTemplate = rabbitTemplate;
        this.commandPatternChecker = commandPatternChecker;
        this.appPhotoDAO = appPhotoDAO;
        this.hashids = hashids;
    }

    @Value("${spring.rabbitmq.queues.login}")
    private String registrationLoginQueue;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;


    @Override
    //Не использую транзакции потому что после метода сейв ничего не делаем ничего не произойдет что откатит сейв никогда
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы";
        } else if (appUser.getEmail() != null && !appUser.getIsActive()) {
            appUser.setState(WAIT_FOR_PASSWORD_STATE);
            appUserDAO.save(appUser);
            return "Ваш email " + appUser.getEmail() + "\n"
                    + " введите пароль";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return  "ВВЕДИТЕ E-MAIL: \n" +
                "(Если вы забыли свои \"логин/пароль\" воспользуйтесь СМС-сервисом восстановления:\n" +
                "https://m.master-food.pro/private/forgot/ )\n\n" +
                "ВВЕДИТЕ E-MAIL: \n"
                ;
    }

    @Override
    //Не использую транзакции потому что после метода сейв ничего не делаем ничего не произойдет что откатит сейв никогда
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите пожалуйста корректный e-mail адрес. Для отмены команды введите /cancel";
        }
        if(commandPatternChecker.isNotACommand(email)){
            appUser.setEmail(email);
            appUser.setState(WAIT_FOR_PASSWORD_STATE);
            appUserDAO.save(appUser);
            return "Введите пароль";
        }else{
            return "Введите e-mail\n" +
            "или отмените процесс авторизации /cancel";
        }
    }

    @Override
    public String checkPassword(Long chatId, AppUser appUser, String password) {
        if(commandPatternChecker.isNotACommand(password)){
            var loginParams = RequestParams.builder()
                    .requestType(RequestsToREST.LOGIN_REQUEST)
                    .id(appUser.getId())
                    .chatId(chatId)
                    .email(appUser.getEmail())
                    .password(password)
                    .build();
            try {
                rabbitTemplate.convertAndSend(registrationLoginQueue, loginParams);
            } catch (AmqpException e) {
                log.error("Ошибка при отправке сообщения в очередь {}: {}", registrationLoginQueue, e.getMessage(), e);
            }
            return "Отправлено на проверку...";
        }else{
            return "Введите пароль \n" +
                    "или отмените процесс авторизации /cancel";
        }

    }

    @Override
    public String checkBalance(Long chatId, AppUser appUser) {
        var getBalanceParams = RequestParams.builder()
                .requestType(RequestsToREST.PRESENTS_REQUEST)
                .id(appUser.getId())
                .chatId(chatId)
                .build();
        try {
            rabbitTemplate.convertAndSend(registrationLoginQueue, getBalanceParams);
        } catch (AmqpException e) {
            log.error("Ошибка при отправке сообщения в очередь {}: {}", registrationLoginQueue, e.getMessage(), e);
        }
        return "Уточняем...";
    }

    @Override
    public String checkStatus(Long chatId, AppUser appUser) {
        var getOrderStatus = RequestParams.builder()
                .requestType(RequestsToREST.ORDER_STATUS_REQUEST)
                .id(appUser.getId())
                .chatId(chatId)
                .build();
        try {
            rabbitTemplate.convertAndSend(registrationLoginQueue, getOrderStatus);
        } catch (AmqpException e) {
            log.error("Ошибка при отправке сообщения в очередь {}: {}", registrationLoginQueue, e.getMessage(), e);
        }
        return "Уточняем...";
    }


    @Override
    public String createReportMail(Long chatId, AppUser appUser) {
        appUser.setState(WAIT_FOR_REPORT_MESSAGE);
        appUserDAO.save(appUser);
        return "Отпрвьте фото, а затем отправьте текст сообщения, или просто отправтье сообщение без фотографий.";
    }

    @Override
    @Transactional // а вот тут мы уже то туда то сюда сохраняем поэтому надо все откатить если где то что то...
    public String sendReportMail(Long chatId, AppUser appUser, String message) {
        if(commandPatternChecker.isNotACommand(message)){
            try {
                AppUser userForSession = appUserDAO.findById(appUser.getId()).orElse(null);
                StringBuilder resultMesssage = new StringBuilder(message + "\n");
                if (userForSession != null) {
                    List<AppPhoto> appPhotos = userForSession.getPhotos();

                    List<byte[]> attachments = new ArrayList<>();
                    for (AppPhoto appPhoto : appPhotos) {
                        if (appPhoto.getBinaryContent() != null) {
                            if (appPhoto.getMessage() != null){
                                resultMesssage.append("\n + Дополнительный текст к фото во вложении: " + appPhoto.getMessage());
                            }
                            byte[] binaryContent = appPhoto.getBinaryContent().getFileAsArrayOfBytes();
                            attachments.add(binaryContent);
                        }
                    }
                    var mailParams = MailParams.builder()
                            .id(appUser.getId())
                            .chatId(chatId)
                            .email(appUser.getEmail())
                            .siteUid(appUser.getSiteUserId())
                            .phoneNumber(appUser.getPhoneNumber())
                            .message(resultMesssage.toString())
                            .photos(attachments)
                            .build();
                    try {
                        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
                    } catch (AmqpException e) {
                        log.error("Ошибка при отправке сообщения в очередь {}: {}", registrationLoginQueue, e.getMessage(), e);
                    }
                    appUser.setState(BASIC_STATE);
                    appUserDAO.save(appUser);
                    return "Отправляется...";

                } else {
                    appUser.setState(BASIC_STATE);
                    appUserDAO.save(appUser);
                    log.error("User is not found at method sendReportMail, chatId:" + chatId + " user:" + appUser + " message:" + message);
                    return "Пользователь не найден в методе sendReportMail";
                }
            } catch (Exception e) {
                e.printStackTrace();
                appUser.setState(BASIC_STATE);
                appUserDAO.save(appUser);
                log.error("Error with sending photos, chatId:" + chatId + " user:" + appUser + " message:" + message + " " + e.getMessage());
                return "Ошибка при отправке фотографий: " + e.getMessage();
            }
        }else{
            return "Введите сообщение чтобы отправить жалобу \n" +
                    "или отмените процесс отправки жалобы /cancel";
        }

    }

    @Override
    public String quit(Long chatId, AppUser appUser) {
        return "Подтвердите выход:\n"
                + "Хочу выйти: /quit_accept \n"
                + "Отмена: /cancel";
    }

    @Override
    public String exit(Long chatId, AppUser appUser) {
        appUser.setEmail(null);
        appUser.setSiteUserId(null);
        appUser.setPhoneNumber(null);
        appUser.setIsActive(false);
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Вы успешно вышли из аккаунта. Для входа в аккаунт пройдете авторизацию /registration";
    }

}
