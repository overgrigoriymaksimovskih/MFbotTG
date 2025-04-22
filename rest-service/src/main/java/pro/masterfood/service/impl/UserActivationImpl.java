package pro.masterfood.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.SiteData;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static pro.masterfood.enums.UserState.BASIC_STATE;
import static pro.masterfood.enums.UserState.WAIT_FOR_EMAIL_STATE;

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
    public void consumeLogin(RequestParams requestParams) {

        var optional = appUserDAO.findById(requestParams.getId());

        String email = requestParams.getEmail();
        String password = requestParams.getPassword();

        var res = activationFromSite(email, password);

        sendAnswer(res.get("Message") + " " + res.get("PhoneNumber") + " " + res.get("SiteUid"), requestParams.getChatId());


//        // 1. Извлекаем Map "isAuthorized"
//        Map<String, Object> isAuthorizedMap = (Map<String, Object>) res.get("isAuthorized");
//        Long siteUid = Long.parseLong((String) res.get("siteUid"));
//        String phoneNumber = res.get("phoneNumber").toString();
//
//
//        // 2. Извлекаем значение "Status" из isAuthorizedMap
//        String message = "Ошибка авторизации, попробуйте еще раз...";
//        if (isAuthorizedMap != null && isAuthorizedMap.containsKey("Result") && isAuthorizedMap.get("Result") instanceof Map) {
//            Map<?, ?> resultMap = (Map<?, ?>) isAuthorizedMap.get("Result");
//            if (resultMap.containsKey("Status") && resultMap.get("Status") instanceof String) {
//                String statusValue = (String) resultMap.get("Status");
//                if ("success".equalsIgnoreCase(statusValue) && null != optional.get().getEmail()) {
//
//                    //-----------------------------------------------------------------
//                    if (optional.isPresent()) {
//                        var user = optional.get();
//
//                        user.setIsActive(true);
//                        user.setState(BASIC_STATE);
//                        user.setSiteUserId(siteUid);
//                        user.setPhoneNumber(phoneNumber);
//                        appUserDAO.save(user);
//                        sendAnswer("Успешно", requestParams.getChatId());
//                    }
//                    //-----------------------------------------------------------------
//
//                }else if(!optional.get().getIsActive()){
//                    if (resultMap.containsKey("Msg") && resultMap.get("Msg") instanceof String) {
//                        message = (String) resultMap.get("Msg");
//                        if (optional.isPresent()) {
//                            var user = optional.get();
//                            user.setEmail(null);
//                            user.setState(WAIT_FOR_EMAIL_STATE);
//                            appUserDAO.save(user);
//                            sendAnswer(message + " введите email", requestParams.getChatId());
//                        }
//                    }else{
//                        var user = optional.get();
//                        user.setEmail(null);
//                        user.setState(WAIT_FOR_EMAIL_STATE);
//                        appUserDAO.save(user);
//                        sendAnswer(message + " введите email", requestParams.getChatId());
//                    }
//                }
//            }
//        }
    }



    @Override
    public void sendAnswer(String output, Long chatId) {
//        var message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    public Map<String, String> activationFromSite(String email,
                                                  String password) {

        Map<String, String> result = new HashMap<>();

        WebDriver driver = null;
        try {
            // Создаем драйвер
            driver = siteData.createWebDriver();
            // Настраиваем драйвер на страницу
            driver = siteData.setWebDriver(driver, "https://master-food.pro/private/");

            // Создаем POST-запрос
            HttpEntity<MultiValueMap<String, String>> request = siteData.buildPostRequest(driver, email, password);
            // Отправляем POST-запрос
            Map<String, Object> response = siteData.sendPostRequest(request);

            // Обрабатываем ответ от сайта на пост запрос
            if(response.containsKey("Result")){
                String respResult = response.get("Result").toString();
                if(!"success".equalsIgnoreCase(respResult)){
                    result.put("Message", "Post - success");

                    //Настраиваем наш драйвер на страницу
                    driver = siteData.setWebDriver(driver, "https://master-food.pro/private/personal/");
                    // Теперь страница загружена в наш драйвер, просто спарсим итересующие нас данные из нее
                    Map<String, String> resultOfParse = siteData.parsePage(driver);

                    result.put("Message", resultOfParse.get("Message"));
                    result.put("PhoneNumber", resultOfParse.get("PhoneNumber"));
                    result.put("SiteUid", resultOfParse.get("SiteUid"));
                }else if (response.containsKey("Msg")){
                    result.put("Message", response.get("Msg").toString());
                }else{
                    result.put("Message", "Что то пошло не так, ответ на пост запрос: " + response.get("Result").toString());
                }
            }else{
                result.put("Message", "В ответе на пост запрос отсутствует key: Result");
//                result.put("isAuthorized", response);
//                result.put("action", "login");
//                result.put("email", email);
//                result.put("password", password);
//
//                result.put("siteUid", siteUid);
//                result.put("phoneNumber", phoneNumber);
            }








        } finally {
            driver.quit();
        }

        return result;
    }
}
