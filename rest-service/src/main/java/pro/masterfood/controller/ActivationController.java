package pro.masterfood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
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

    //ссылка по которой происходит проверка регистрации пользователя на master-food.pro
    @RequestMapping(method = RequestMethod.POST, value = "/hellooo")
    public ResponseEntity<?> activationMf(@RequestParam("id") String id, @RequestParam("pass") String pass){
        var res = userActivatonService.activationMf(id, pass);
        if (res == true){
            return ResponseEntity.ok().body("Регистрация MF успешно MF завершена");
        }
        return ResponseEntity.internalServerError().build();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        var res = userActivatonService.activation(id);
        if (res == true){
            return ResponseEntity.ok().body("Регистрация успешно завершена");
        }
        return ResponseEntity.internalServerError().build();
    }
}
