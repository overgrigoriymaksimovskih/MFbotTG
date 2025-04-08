package pro.masterfood.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivatonService;

import java.util.Map;
import java.util.HashMap;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivatonService userActivatonService;


    public ActivationController(UserActivatonService userActivatonService) {
        this.userActivatonService = userActivatonService;
    }

//----------------------------------------------------------------------------------------------------------------------

    //Это сервис проверки на нашем хостинге в реале ее надо убрать и метод sendPostRequest должен будет отправлять
    //пост запрос на сайт мастерфуда
    @PostMapping("/checkPostMf")
    public ResponseEntity<Map<String, String>> checkPostMf(@RequestParam("action") String action,
                                                           @RequestParam("email") String email,
                                                           @RequestParam("password") String password,
                                                           @RequestParam("check_num") String check_num,
                                                           @RequestParam("token") String token) {
        Map<String, String> params = new HashMap<>();
        params.put("action", action);
        params.put("email", email);
        params.put("password", password);
        params.put("check_num", check_num);
        params.put("token", token);

        return ResponseEntity.ok(params);

    }

//---------------------------------------------------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        var res = userActivatonService.activation(id);
        if (res == true){
            return ResponseEntity.ok().body("Регистрация успешно завершена");
        }
        return ResponseEntity.internalServerError().build();
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/hellooo")
//    public ResponseEntity<?> activationMf(@RequestParam("email") String email,
//                                          @RequestParam("password") String password)
//    {
//        var res = userActivatonService.activationMf(email, password);
//        if (res == true){
//            return ResponseEntity.ok().body("Пользователь MF авторизован");
//        }
//        return ResponseEntity.internalServerError().build();
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/hellooo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> activationMf(@RequestParam("email") String email,
                                               @RequestParam("password") String password) {
        String result = userActivatonService.activationMf(email, password); // Получаем JSON строку с результатом и параметрами
        if (result.startsWith("Успешно!")) {
            return ResponseEntity.ok().body(result); // Возвращаем JSON строку
        } else {
            return ResponseEntity.internalServerError().body(result); // Возвращаем JSON строку с ошибкой
        }
    }

}
