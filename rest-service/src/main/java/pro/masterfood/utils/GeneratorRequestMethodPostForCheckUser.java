package pro.masterfood.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Component
public class GeneratorRequestMethodPostForCheckUser {
    // Метод для создания POST-запроса
    public HttpEntity<MultiValueMap<String, String>> buildPostRequest(String action,
                                                                      String email,
                                                                      String password,
                                                                      String check_num,
                                                                      String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("action", action);
        map.add("email", email);
        map.add("password", password);
        map.add("check_num", check_num);
        map.add("token", token);

        return new HttpEntity<>(map, headers);
    }
}
