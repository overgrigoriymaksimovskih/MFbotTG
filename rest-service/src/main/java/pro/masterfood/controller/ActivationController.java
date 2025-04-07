package pro.masterfood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivatonService;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivatonService userActivatonService;


    public ActivationController(UserActivatonService userActivatonService) {
        this.userActivatonService = userActivatonService;
    }

//----------------------------------------------------------------------------------------------------------------------

//    //Это сервис проверки на нашем хостинге в реале ее надо убрать и метод sendPostRequest должен будет отправлять
//    //пост запрос на сайт мастерфуда
//    @PostMapping("/checkPostMf")
//    public ResponseEntity<Boolean> checkPostMf(@RequestParam("action") String action,
//                                               @RequestParam("email") String email,
//                                               @RequestParam("password") String password,
//                                               @RequestParam("check_num") String check_num,
//                                               @RequestParam("token") String token) {
//        if (action.equals("a")
//                && email.equals("b")
//                && password.equals("c")
//                && check_num.equals("d")
//                && token.equals("e")){
//            return ResponseEntity.ok(true);
//        }else{
//            return ResponseEntity.ok(false);
//        }
//
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/hellooo")
    public ResponseEntity<?> activationMf(@RequestParam("action") String action,// no using
                                          @RequestParam("email") String email,
                                          @RequestParam("password") String password,
                                          @RequestParam("check_num") String check_num,// no using
                                          @RequestParam("token") String token)// no using
    {
        var res = userActivatonService.activationMf(email, password);
        if (res == true){
            return ResponseEntity.ok().body("Пользователь MF авторизован");
        }
        return ResponseEntity.internalServerError().build();
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
