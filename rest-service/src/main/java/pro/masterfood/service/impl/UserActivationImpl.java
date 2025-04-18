package pro.masterfood.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.LoginParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.GeneratorRequestMethodPostForCheckUser;

import java.util.HashMap;
import java.util.Map;

import static pro.masterfood.enums.UserState.BASIC_STATE;
import static pro.masterfood.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Component
public class UserActivationImpl implements UserActivationService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;
    private final ProducerService producerService;

    public UserActivationImpl(GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser, AppUserDAO appUserDAO, Decoder decoder, ProducerService producerService) {
        this.generatorRequestMethodPostForCheckUser = generatorRequestMethodPostForCheckUser;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
        this.producerService = producerService;
    }

    //    @Override
//    public boolean activation(String cryptoUserId) {
//        var userId = decoder.idOf(cryptoUserId);
//        log.debug(String.format("User activation with user-id=%s", userId));
//        if (userId == null) {
//            return false;
//        }
//
//        var optional = appUserDAO.findById(userId);
//        if (optional.isPresent()) {
//            var user = optional.get();
//            user.setIsActive(true);
//            appUserDAO.save(user);
//            return true;
//        }
//        return false;
//    }
    @Override
    public void consumeLogin(LoginParams loginParams) {

        var optional = appUserDAO.findById(loginParams.getId());

        String email = loginParams.getEmail();
        String password = loginParams.getPassword();
        var res = activationFromSite(email, password);
        // 1. Извлекаем Map "isAuthorized"
        Map<String, Object> isAuthorizedMap = (Map<String, Object>) res.get("isAuthorized");


        // 2. Извлекаем значение "Status" из isAuthorizedMap
        String message = "Не удалось связаться с сервисом авторизации...";
        if (isAuthorizedMap != null && isAuthorizedMap.containsKey("Result") && isAuthorizedMap.get("Result") instanceof Map) {
            Map<?, ?> resultMap = (Map<?, ?>) isAuthorizedMap.get("Result");
            if (resultMap.containsKey("Status") && resultMap.get("Status") instanceof String) {
                String statusValue = (String) resultMap.get("Status");
                if ("success".equalsIgnoreCase(statusValue) && null != optional.get().getEmail()) {

                    if (optional.isPresent()) {
                        var user = optional.get();

                        user.setIsActive(true);
                        user.setState(BASIC_STATE);
                        appUserDAO.save(user);
                        sendAnswer("Успешно", loginParams.getChatId());
                    }

                }else if(!optional.get().getIsActive()){
                    if (resultMap.containsKey("Msg") && resultMap.get("Msg") instanceof String) {
                        message = (String) resultMap.get("Msg");
                        if (optional.isPresent()) {
                            var user = optional.get();
                            user.setEmail(null);
                            user.setState(WAIT_FOR_EMAIL_STATE);
                            appUserDAO.save(user);
                            sendAnswer(message + " введите email", loginParams.getChatId());
                        }
                    }
                }
            }
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

    @Override
    public Map<String, Object> activationFromSite(String email,
                                                  String password) {
        // 1. Создаем POST-запрос
        HttpEntity<MultiValueMap<String, String>> request = generatorRequestMethodPostForCheckUser.buildPostRequest(email, password);
        // 2. Отправляем POST-запрос
        Map<String, Object> response = sendPostRequest(request);

        // 3. Создаем Map для возврата
        Map<String, Object> result = new HashMap<>();
        result.put("isAuthorized", response);
        result.put("action", "login");
        result.put("email", email);
        result.put("password", password);

        return result;
    }

    private Map<String, Object> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity("https://master-food.pro/", request, String.class); // Get response as String
            String html = response.getBody();
            Map<String, Object> result = new HashMap<>();

            // Parse JSON from the HTML string
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> answer = null;
            try {
                answer = mapper.readValue(html, Map.class);
            } catch (JsonProcessingException e) {
                result.put("Result", "Cannot read value with mapper from answer (selenium not worked....)");
                return result;
            }

            result.put("Result", answer);
            return result;

        } catch (RestClientException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("Error", "Ошибка при отправке POST-запроса: " + e.getMessage());
            return result;
        }
    }
}
