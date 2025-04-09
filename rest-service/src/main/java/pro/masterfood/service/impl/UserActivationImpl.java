package pro.masterfood.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.service.UserActivatonService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.GeneratorRequestMethodPostForCheckUser;

import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserActivationImpl implements UserActivatonService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;

    public UserActivationImpl(GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser, AppUserDAO appUserDAO, Decoder decoder) {
        this.generatorRequestMethodPostForCheckUser = generatorRequestMethodPostForCheckUser;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = decoder.idOf(cryptoUserId);
        log.debug(String.format("User activation with user-id=%s", userId));
        if (userId == null) {
            return false;
        }

        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
    @Override
    public Map<String, Object> activationMf(String action,//
                                            String email,//
                                            String password,//
                                            String check_num,//
                                            String token) {//
        // 1. Создаем POST-запрос
        HttpEntity<MultiValueMap<String, String>> request = generatorRequestMethodPostForCheckUser.buildPostRequest(action, email, password, check_num, token);
        // 2. Отправляем POST-запрос
//        ResponseEntity<Boolean> response = sendPostRequest(request);
//        ResponseEntity<String> response = sendPostRequest(request);
        ResponseEntity<Map<String, Object>> response = sendPostRequest(request);
        // 3. Обрабатываем результат
//        boolean isAuthorized = (response.getBody() != null && response.getBody());


//        String responseBody = response.getBody();
//        boolean isAuthorized = false;
//
//        if (responseBody != null) {
//            Document doc = Jsoup.parse(responseBody);
//            Element h1 = doc.select("h1:contains(Личный кабинет)").first(); // Ищем тег h1, содержащий текст "Личный кабинет"
//            if (h1 != null) {
//                isAuthorized = true; // Если тег найден, значит, авторизация прошла успешно
//            }
//        }

        boolean isAuthorized = false;

        if (response.getBody() != null && response.getBody().containsKey("Status")) {
            String status = (String) response.getBody().get("Status");
            isAuthorized = "success".equals(status); // Проверяем, что Status равен "success"
        }

        // 4. Создаем Map для возврата
        Map<String, Object> result = new HashMap<>();
        result.put("isAuthorized", isAuthorized);
        result.put("action", action);
        result.put("email", email);
        result.put("password", password);
        result.put("check_num", check_num);
        result.put("token", token);

        return result;
    }
    // Метод для отправки POST-запроса
//    private ResponseEntity<Boolean> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
    private ResponseEntity<Map<String, Object>> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://81.200.158.74:8086/user/checkPostMf"; // URL checkPostMf
        String url = "https://master-food.pro/";
//        return restTemplate.postForEntity(url, request, Boolean.class);
//        return restTemplate.postForEntity(url, request, String.class); // Измените Boolean.class на String.class
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }
}
