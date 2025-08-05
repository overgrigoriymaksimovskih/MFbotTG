package pro.masterfood.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.masterfood.service.UserActivationService;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivatonService;
    public ActivationController(UserActivationService userActivatonService) {
        this.userActivatonService = userActivatonService;
    }
}
