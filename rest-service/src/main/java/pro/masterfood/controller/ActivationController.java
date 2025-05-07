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
}
