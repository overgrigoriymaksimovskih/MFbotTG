package pro.masterfood.service.impl;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.RequestParams;
import pro.masterfood.service.PhoneHandler;
import pro.masterfood.service.ProducerService;
import pro.masterfood.utils.CustomPostClient;
import pro.masterfood.utils.SimpleHttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.masterfood.enums.UserState.*;

@Component
public class PhoneHandlerImpl implements PhoneHandler {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final AppUserDAO appUserDAO;
    private final CustomPostClient customPostClient;
    private final SimpleHttpClient simpleHttpClient;
    private final ProducerService producerService;

    @Value("${streamtelecom.user}")
    private String streamTelecomUserName;
    @Value("${streamtelecom.password}")
    private String streamTelecomPassword;


    public PhoneHandlerImpl(AppUserDAO appUserDAO, CustomPostClient customPostClient, SimpleHttpClient simpleHttpClient, ProducerService producerService) {
        this.appUserDAO = appUserDAO;
        this.customPostClient = customPostClient;
        this.simpleHttpClient = simpleHttpClient;
        this.producerService = producerService;
    }

    @Override
    public void handlePhone(RequestParams requestParams){
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

            String url = "http://gateway.api.sc/telegram-code/";
            // Создаем тело запроса application/x-www-form-urlencoded
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("login", streamTelecomUserName);       // используем значения из application.properties
            map.add("pass", streamTelecomPassword);     // используем значения из application.properties
            map.add("code-gen", "4");
//        map.add("code", code);              // используем параметр из GET-запроса
            map.add("phone", phoneNumber);     // используем параметр из GET-запроса
//        map.add("callback_url", "https://example.com/my-webhook"); // используем значения из application.properties
            map.add("sms_originator", "MasterFood");
//            map.add("sms_text", "Ваш код подтверждения " + code);

            // Отправляем POST-запрос
            String response = customPostClient.sendPostRequest(url, mapToFormData(map));

            try {
                // Попытка извлечь JSON с помощью регулярного выражения
                Pattern pattern = Pattern.compile("\\{(.*)\\}");
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    response = matcher.group(); // Извлекаем JSON
                }

                JSONObject jsonResponse = new JSONObject(response); // Преобразуем строку в JSON объект
                String result = jsonResponse.getString("result"); // Получаем значение поля "result"

                if ("Success".equals(result)) {
                    // Обработка успешного результата
                    String code = jsonResponse.getString("code");
                    user.setSiteUserId(Long.parseLong(resUserSiteId));
                    user.setSmsCode(code);
                    user.setState(WAIT_FOR_SMS_STATE);
                    appUserDAO.save(user);
                    sendAnswer("Сообщение с кодом подтверждения отправлено, пришлите код полученный от \"Verification codes\"", requestParams.getChatId());
                } else if ("Error".equals(result)) {
                    // Обработка ошибки
                    user.setPhoneNumber(null);
                    user.setSiteUserId(null);
                    user.setState(BASIC_STATE);
                    appUserDAO.save(user);
                    sendAnswer("Ошибка: " + jsonResponse.getString("message") + " повторите процесс авторизации позже...", requestParams.getChatId());
                    log.error("Ошибка: " + jsonResponse.getString("message"));
                } else {
                    // Обработка неизвестного результата
                    user.setPhoneNumber(null);
                    user.setSiteUserId(null);
                    user.setState(BASIC_STATE);
                    appUserDAO.save(user);
                    sendAnswer("Ошибка: \"неизвестный результат\": " + result + "\n" + "Повторите процесс авторизации позже...", requestParams.getChatId());
                    log.error("Ошибка: \"неизвестный результат\": " + result);
                }

            }   catch (Exception e) {
                user.setPhoneNumber(null);
                user.setSiteUserId(null);
                user.setState(BASIC_STATE);
                appUserDAO.save(user);
                sendAnswer("Ошибка при обработке JSON: " + response + "\n" + e.getMessage() + "\n" + "Повторите процесс авторизации позже...", requestParams.getChatId() );
                log.error("Ошибка при обработке JSON: ", e); // Логируем исключение полностью
            }



        }else if ("notfound".equals(resUserSiteId)){
            user.setPhoneNumber(null);
            user.setState(WAIT_FOR_PHONE_MANUAL_INPUT_STATE);
            appUserDAO.save(user);
            sendAnswer("Номер не зарегистрирован на сайте master-food.pro Введите зарегистрированный номер: \n" +
                    "или отмените процесс авторизации /cancel", requestParams.getChatId());
        }else{
            user.setPhoneNumber(null);
            user.setState(BASIC_STATE);
            appUserDAO.save(user);
            sendAnswer("Ошибка обработки телефонного номера на сайте: \n" +
                    "список доступных момманд: /help", requestParams.getChatId());
        }

    }

    @Override
    public void sendAnswer(String output, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    // Конвертируем MultiValueMap в строку в формате application/x-www-form-urlencoded
    private String mapToFormData(MultiValueMap<String, String> map) {
        StringBuilder formData = new StringBuilder();
        for (String key : map.keySet()) {
            for (String value : map.get(key)) {
                if (formData.length() > 0) {
                    formData.append("&");
                }
                formData.append(key).append("=").append(value);
            }
        }
        String result = formData.toString();

//        System.out.println("FormData: " + result); // Смотрим что вернул сервис в логах контейнра
        return result;
    }

}
