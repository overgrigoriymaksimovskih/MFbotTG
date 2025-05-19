package pro.masterfood.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.SiteData;

import static pro.masterfood.enums.UserState.*;

@Component
public class UserActivationImpl implements UserActivationService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final SiteData siteData;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;
    private final ProducerService producerService;

    public UserActivationImpl(SiteData siteData, AppUserDAO appUserDAO, Decoder decoder, ProducerService producerService) {
        this.siteData = siteData;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
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
        var res = siteData.activationFromSite(email, password);
        if(res.containsKey("PhoneNumber") && res.containsKey("SiteUid")){
            if(!res.get("PhoneNumber").equalsIgnoreCase("null") && !res.get("SiteUid").equalsIgnoreCase("0")){
                user.setIsActive(true);
                user.setState(BASIC_STATE);
                user.setPhoneNumber(res.get("PhoneNumber").toString());
                user.setSiteUserId(Long.valueOf(res.get("SiteUid").toString()));
                appUserDAO.save(user);
                sendAnswer(res.get("Message") + "\nСписок доступных команд: \n"
                                + "Накопления на подарок и бонусы: /present\n "
                                + "Статус текущего заказа: /status\n   "
                                + "Отправить жалобу: /report\n "
                                + "Отмена выполнения текущей команды: /cancel\n "
                                + "\n"
                                + "Выйти: /quit"
                        , requestParams.getChatId())
                ;
            }else{
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
    public void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }
}
