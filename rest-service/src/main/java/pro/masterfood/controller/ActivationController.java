package pro.masterfood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivatonService;
import pro.masterfood.utils.GeneratorRequestMethodPostForCheckUser;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivatonService userActivatonService;
    private final GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser;

    public ActivationController(UserActivatonService userActivatonService, GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser) {
        this.userActivatonService = userActivatonService;
        this.generatorRequestMethodPostForCheckUser = generatorRequestMethodPostForCheckUser;
    }

//----------------------------------------------------------------------------------------------------------------------
    //ссылка по которой происходит проверка регистрации пользователя на master-food.pro
    // 1. Пользователь вбивает в адресную строку: 81.200.158.74:8086/user/hellooo
    @GetMapping("/hellooo") // Используем GET, чтобы пользователь просто вводил URL в браузере
    public ResponseEntity<?> createPostMf(@RequestParam("id") String id, @RequestParam("pass") String pass) {
        // 1. Создаем POST-запрос
        HttpEntity<MultiValueMap<String, String>> request = generatorRequestMethodPostForCheckUser.buildPostRequest(id, pass);

        // 2. Отправляем POST-запрос
        ResponseEntity<Boolean> response = sendPostRequest(request);

        //3. Обрабатываем результат
        if (response.getBody() != null && response.getBody()) {
            return ResponseEntity.ok().body("Такой пользователь MF существует");
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Метод для отправки POST-запроса
    private ResponseEntity<Boolean> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://81.200.158.74:8086/user/checkPostMf"; // URL checkPostMf
        return restTemplate.postForEntity(url, request, Boolean.class);
    }

    //Это сервис проверки на нашем хостинге в реале ее надо убрать и метод sendPostRequest должен будет отправлять
    //пост запрос на сайт мастерфуда
    @PostMapping("/checkPostMf")
    public ResponseEntity<Boolean> checkPostMf(@RequestParam("id") String id, @RequestParam("pass") String pass) {
        if (id.equals("a") && pass.equals("b")){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        var res = userActivatonService.activation(id);
        if (res == true){
            return ResponseEntity.ok().body("Регистрация успешно завершена");
        }
        return ResponseEntity.internalServerError().build();
    }
}
