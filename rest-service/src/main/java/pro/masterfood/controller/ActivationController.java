package pro.masterfood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivationService;

import java.util.Map;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivatonService;


    public ActivationController(UserActivationService userActivatonService) {
        this.userActivatonService = userActivatonService;
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/activation")
//    public ResponseEntity<?> activation(@RequestParam("id") String id){
//        var res = userActivatonService.activation(id);
//        if (res == true){
//            return ResponseEntity.ok().body("Регистрация успешно завершена");
//        }
//        return ResponseEntity.internalServerError().build();
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/hellooo")
    public ResponseEntity<Map<String, Object>> activationMf(@RequestParam("email") String email,
                                                            @RequestParam("password") String password)
    {
        var res = userActivatonService.activationFromSite(email, password);
        return ResponseEntity.ok(res);
    }
}
