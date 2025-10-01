package pro.masterfood.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/api")
@RestController
public class OauthUserController {

    @Value("${redirect.link}")
    private String redirectLink;
    @GetMapping("/redirect")
    public RedirectView redirectToMasterFood() {
        return new RedirectView(redirectLink);
    }
}
