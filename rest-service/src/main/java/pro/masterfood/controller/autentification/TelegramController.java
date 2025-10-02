//package pro.masterfood.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.view.RedirectView;
//
//@RequestMapping("/api")
//@RestController
//public class OauthUserController {
//
//    @Value("${redirect.link}")
//    private String redirectLink;
//    @GetMapping("/redirect")
//    public RedirectView redirectToMasterFood() {
//        return new RedirectView(redirectLink);
//    }
//}

package pro.masterfood.controller.autentification;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TelegramController {

    @GetMapping("/api/redirect")
    public String redirectPage() {
        // Просто возвращаем имя шаблона Thymeleaf
        return "redirect";  // Это имя файла шаблона без .html
    }
}
