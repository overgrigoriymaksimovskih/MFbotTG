package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserInformationProvider;
import pro.masterfood.utils.SimpleHttpClient;
import static pro.masterfood.enums.UserState.BASIC_STATE;
import static pro.masterfood.enums.UserState.WAIT_FOR_ANSWER;

@Component
public class UserInformationProviderImpl implements UserInformationProvider {

    private final AppUserDAO appUserDAO;
    private final SimpleHttpClient simpleHttpClient;
    private final ProducerService producerService;

    public UserInformationProviderImpl(AppUserDAO appUserDAO, SimpleHttpClient simpleHttpClient, ProducerService producerService) {
        this.appUserDAO = appUserDAO;
        this.simpleHttpClient = simpleHttpClient;
        this.producerService = producerService;
    }

    @Override
    public void consumeGetBalance(RequestParams requestParams) {
        var optional = appUserDAO.findById(requestParams.getId());
        var user = optional.get();
        try {
            user.setState(WAIT_FOR_ANSWER);
            appUserDAO.save(user);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                sendAnswer("ошибка сна", requestParams.getChatId());
            }
            if (optional.isPresent()) {
                Long siteId = optional.get().getSiteUserId();
                String answer = simpleHttpClient.getBalance(siteId.toString());
                sendAnswer(answer, requestParams.getChatId());
            }else{
                sendAnswer("Пользователь с таким siteId не найден", requestParams.getChatId());
            }
        } finally {
            user.setState(BASIC_STATE);
            appUserDAO.save(user);
        }
    }

    @Override
    public void consumeGetOrderStatus(RequestParams requestParams) {
        var optional = appUserDAO.findById(requestParams.getId());
        var user = optional.get();
        try {
            user.setState(WAIT_FOR_ANSWER);
            appUserDAO.save(user);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                sendAnswer("ошибка сна", requestParams.getChatId());
            }
            if (optional.isPresent()) {
                String phoneNumber = optional.get().getPhoneNumber();
                String answer = simpleHttpClient.getOrderStatus(phoneNumber);
                sendAnswer(answer, requestParams.getChatId());
            }else{
                sendAnswer("Пользователь с таким siteId не найден", requestParams.getChatId());
            }
        } finally {
            user.setState(BASIC_STATE);
            appUserDAO.save(user);
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
