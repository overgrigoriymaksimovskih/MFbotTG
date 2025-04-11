package pro.masterfood.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivationService;

import org.springframework.http.HttpStatus;

import java.util.Map;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;


    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }



//    @RequestMapping(method = RequestMethod.GET, value = "/activation")
//    public ResponseEntity<?> activation(@RequestParam("id") String id){
//        var res = userActivationService.activation(id);
//        if (res == true){
//            return ResponseEntity.ok().body("Регистрация успешно завершена");
//        }
//        return ResponseEntity.internalServerError().build();
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/hellooo")
    public ResponseEntity<Map<String, Object>> activationMf(@RequestParam("email") String email,
                                                            @RequestParam("password") String password)
    {
        var res = userActivationService.activationMf(email, password);
        if (res.get("Status").toString().equals("success")) {
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.ok(res);
        }
    }
}
