package pro.masterfood.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

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
        //headers.set("Content-Type", "text/html; charset=windows-1251"); // Добавьте это, только если сервер требует windows-1251, что очень не рекомендуется.
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,ro;q=0.6,az;q=0.5,sq;q=0.4,bg;q=0.3,ka;q=0.2,hy;q=0.1");
        headers.set("Origin", "https://master-food.pro");
        headers.set("Referer", "https://master-food.pro/private/");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-origin");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("X-CSRF-Token", token); // Используйте переданный csrfToken
        headers.set("http_x_requested_with", "XMLHttpRequest");


        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("action", action);
        map.add("email", email);
        map.add("password", password);
        map.add("check_num", check_num);
        map.add("token", token);

        return new HttpEntity<>(map, headers);
    }
}
