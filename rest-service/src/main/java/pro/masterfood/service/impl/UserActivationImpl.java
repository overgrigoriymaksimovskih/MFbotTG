package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.SimpleHttpClient;
import pro.masterfood.utils.SiteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static pro.masterfood.enums.UserState.*;

@Component
public class UserActivationImpl implements UserActivationService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final SiteData siteData;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;
    private final SimpleHttpClient simpleHttpClient;
    private final ProducerService producerService;

    public UserActivationImpl(SiteData siteData, AppUserDAO appUserDAO, Decoder decoder, SimpleHttpClient simpleHttpClient, ProducerService producerService) {
        this.siteData = siteData;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
        this.simpleHttpClient = simpleHttpClient;
        this.producerService = producerService;
    }

    @Override
    public void consumeLogin(RequestParams requestParams) {

        var optional = appUserDAO.findById(requestParams.getId());

        String email = requestParams.getEmail();
        String password = requestParams.getPassword();



        var user = optional.get();
        user.setState(WAIT_FOR_ANSWER);
        appUserDAO.save(user);
        Map<String, String> res = null;
        try {
            res = siteData.activationFromSite(email, password);
        } catch (Exception e) {
            user.setState(BASIC_STATE);
            user.setEmail(null);
            appUserDAO.save(user);
            log.error("Error in utils -> SiteData " + e.getMessage(), e);
            sendAnswer("Ошибка проверки логина/пароля на сайте с использованием siteData.activationFromSite: " + e.getMessage(), requestParams.getChatId());
        }
        if(res.containsKey("PhoneNumber") && res.containsKey("SiteUid")){
            if(!res.get("PhoneNumber").equalsIgnoreCase("null") && !res.get("SiteUid").equalsIgnoreCase("0")){
                user.setIsActive(true);
                user.setState(BASIC_STATE);
                user.setPhoneNumber(res.get("PhoneNumber").toString());
                user.setSiteUserId(Long.valueOf(res.get("SiteUid").toString()));
                appUserDAO.save(user);
                sendAnswer(res.get("Message") + "\nСписок доступных команд:\n\n"
                                + "Накопления на подарок и бонусы: /present\n"
                                + "Статус текущего заказа: /status\n"
                                + "Отправить жалобу: /report\n"
                                + "Отмена выполнения текущей команды: /cancel\n "
                                + "\n"
                                + "Выйти: /quit"
                        , requestParams.getChatId())
                ;
            }else{
                log.error("Error with handle answer in UserActivationImpl " + requestParams.getChatId());
                sendAnswer("Ошибка в интерпритации ответа при авторизации \n" + "Введите email..." , requestParams.getChatId());
            }

        }else{
            user.setEmail(null);
            user.setState(WAIT_FOR_EMAIL_STATE);
            appUserDAO.save(user);
            sendAnswer(res.get("Message").replace("uid::", "") + "\n" + "Введите email...", requestParams.getChatId());
        }
    }

    @Override
    public void consumeContact(RequestParams requestParams) {
        var optional = appUserDAO.findById(requestParams.getId());

        String phoneNumber = requestParams.getPhoneNumber();
        var user = optional.get();
        user.setState(WAIT_FOR_ANSWER);
        appUserDAO.save(user);
        String resUserSiteId = null;

        try {
//            resUserSiteId = "97220";// тут потом надо обращаться к сайт апи чтобы получить реальный сайтЮзерИд
            resUserSiteId = simpleHttpClient.getSiteId(phoneNumber);
        } catch (Exception e) {
            user.setState(BASIC_STATE);
            user.setPhoneNumber(null);
            appUserDAO.save(user);
            log.error("Error in utils -> SimpleHttpClient: " + e.getMessage(), e);
            sendAnswer("Ошибка проверки логина/пароля на сайте с использованием SimpleHttpClient: " + e.getMessage(), requestParams.getChatId());
        }

        if (resUserSiteId != null && !"notfound".equals(resUserSiteId)) {
            user.setSiteUserId(Long.valueOf(resUserSiteId));
            user.setIsActive(true);
            user.setState(BASIC_STATE);
            appUserDAO.save(user);

            sendAnswer("Добро пожаловать!" + "\nСписок доступных команд:\n\n"
                            + "Накопления на подарок и бонусы: /present\n"
                            + "Статус текущего заказа: /status\n"
                            + "Отправить жалобу: /report\n"
                            + "Отмена выполнения текущей команды: /cancel\n "
                            + "\n"
                            + "Выйти: /quit"
                    , requestParams.getChatId())
            ;


        }else if ("notfound".equals(resUserSiteId)){
            user.setPhoneNumber(null);
            user.setState(BASIC_STATE);
            appUserDAO.save(user);
            sendAnswer("Номер на который зарегистрирован Telegram не зарегистрирован на сайте master-food.pro: \n" +
                    "ввести номер вручную /phoneinput \n" +
                    "список доступных момманд: /help",requestParams.getChatId());
        }else{
            user.setPhoneNumber(null);
            user.setState(BASIC_STATE);
            appUserDAO.save(user);
            sendAnswer("Ошибка обработки телефонного номера на сайте: \n" +
                    "список доступных момманд: /help", requestParams.getChatId());
        }
    }


    @Override
    public void consumeSMS(RequestParams requestParams) {

        var optional = appUserDAO.findById(requestParams.getId());

        var user = optional.get();
        String smsFromDB = user.getSmsCode();
        String sms = requestParams.getSmsCode();

        user.setState(WAIT_FOR_ANSWER);
        appUserDAO.save(user);

        if(smsFromDB.equals(sms)){
            user.setIsActive(true);
            user.setState(BASIC_STATE);
            user.setSmsCode(null);
            appUserDAO.save(user);

            sendAnswer("Добро пожаловать!" + "\nСписок доступных команд:\n\n"
                            + "Накопления на подарок и бонусы: /present\n"
                            + "Статус текущего заказа: /status\n"
                            + "Отправить жалобу: /report\n"
                            + "Отмена выполнения текущей команды: /cancel\n "
                            + "\n"
                            + "Выйти: /quit"
                    , requestParams.getChatId())
            ;
        }else{
            user.setPhoneNumber(null);
            user.setSmsCode(null);
            user.setSiteUserId(null);
            user.setState(WAIT_FOR_PHONE_MANUAL_INPUT_STATE);
            appUserDAO.save(user);
            sendAnswer("Неверный код подтверждения\n" +"" +
                    "Введите номер в формате: 7 *** *** ** **\n" +
                    "или отмените процесс авторизации /cancel", requestParams.getChatId());
        }
    }


    @Override
    public void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }
}
