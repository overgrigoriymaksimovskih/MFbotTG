package pro.masterfood.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.service.UserActivatonService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.GeneratorRequestMethodPostForCheckUser;

import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
        Map<String, Object> response = sendPostRequest(request);
        // 3. Обрабатываем результат

        String isAuthorized = "empty";

        if(response == null){
            isAuthorized = "null";
        }else{
            isAuthorized = response.toString();
        }

//        if (response != null && response.containsKey("Status")) {
//            String status = (String) response.get("Status");
//            isAuthorized = "success".equals(status); // Проверяем, что Status равен "success"
//        }

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
//    private Map<String, Object> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://master-food.pro/";
//        try {
//            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
//
//            if (response.getBody() instanceof Map) {
//                return (Map<String, Object>) response.getBody();
//            } else {
//                Map<String, Object> result = new HashMap<>();
//                result.put("Error", "Не удалось преобразовать тело ответа в Map");
//                return result;
//            }
//        } catch (RestClientException e) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("Error", "Ошибка при отправке POST-запроса: " + e.getMessage());
//            return result;
//        }
//    }
    private Map<String, Object> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
        RestTemplate restTemplate = new RestTemplate();

//        try {
//            ResponseEntity<byte[]> response = restTemplate.postForEntity("https://master-food.pro/", request, byte[].class);
//            byte[] gzippedHtml = response.getBody();
//
//            // Decompress gzip manually
//            String html = decompressGzip(gzippedHtml);
//
//            //Parse JSON
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> result = mapper.readValue(html, Map.class);
//
//            return result;
//
//        } catch (Exception e) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("Error", "Ошибка при отправке POST-запроса: " + e.getMessage());
//            return result;
//        }//---

        try {
            ResponseEntity<byte[]> response = restTemplate.postForEntity("https://master-food.pro/", request, byte[].class);
            byte[] gzippedHtml = response.getBody();
            // Decompress gzip manually
            String html = "empty";
            Map<String, Object> result = null;
            try {
                html = decompressGzip(gzippedHtml);
            } catch (IOException e) {
                result.put("Result", e.getMessage());
                return result;
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                result = mapper.readValue(html, Map.class);
            } catch (JsonProcessingException e) {
                result.put("Result", e.getMessage());
                return result;
            }
            result.put("Result", html);
            return result;




        } catch (RestClientException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("Error", "Ошибка при отправке POST-запроса: " + e.getMessage());
            return result;
        }
    }
    // Helper function to decompress gzip
    private String decompressGzip(byte[] compressed) throws IOException {
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        // Convert to String using windows-1251
        return new String(baos.toByteArray(), "windows-1251");
    }

}
