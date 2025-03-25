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

    @RequestMapping(method = RequestMethod.GET, value = "/hellooo")
    public String hello() {
        return "Hello from ActivationController!";
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
