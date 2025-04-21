package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserInformationProvider;
import pro.masterfood.utils.SimpleHttpClient;

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
        if (optional.isPresent()) {
            Long siteId = optional.get().getSiteUserId();
            String answer = simpleHttpClient.getBalance(siteId.toString());
            sendAnswer(answer, requestParams.getChatId());
        }else{
            sendAnswer("Пользователь с таким siteId не найден", requestParams.getChatId());
        }

    }

    @Override
    public void sendAnswer(String output, Long chatId) {
//        var message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }
}
