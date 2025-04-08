package pro.masterfood.service.impl;

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
    public boolean activationMf(String email,
                                String password) {
        // 1. Создаем POST-запрос
        HttpEntity<MultiValueMap<String, String>> request = generatorRequestMethodPostForCheckUser.buildPostRequest(email, password);
        // 2. Отправляем POST-запрос
        ResponseEntity<Boolean> response = sendPostRequest(request);
        //3. Обрабатываем результат
        if (response.getBody() != null && response.getBody()) {
            return true;
        } else {
            return false;
        }
    }
    // Метод для отправки POST-запроса
    private ResponseEntity<Boolean> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://81.200.158.74:8086/user/checkPostMf"; // URL checkPostMf

        String url = "https://master-food.pro/";
        return restTemplate.postForEntity(url, request, Boolean.class);
    }
}
