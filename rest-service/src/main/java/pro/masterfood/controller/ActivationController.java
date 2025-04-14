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
        // 1. Извлекаем Map "isAuthorized"
        Map<String, Object> isAuthorizedMap = (Map<String, Object>) res.get("isAuthorized");

        // 2. Извлекаем значение "Status" из isAuthorizedMap
        String status = "failure"; // Значение по умолчанию
        String message = "Не удалось связаться с сервисом авторизации...";
        String sessionId = null;
        if (isAuthorizedMap != null && isAuthorizedMap.containsKey("Result") && isAuthorizedMap.get("Result") instanceof Map) {
            Map<?, ?> resultMap = (Map<?, ?>) isAuthorizedMap.get("Result");
            if (resultMap.containsKey("Status") && resultMap.get("Status") instanceof String) {
                String statusValue = (String) resultMap.get("Status");
                if ("success".equalsIgnoreCase(statusValue)) {
                    status = "success";
                }
            }
            if (resultMap.containsKey("Msg") && resultMap.get("Msg") instanceof String) {
                message = (String) resultMap.get("Msg");
            }
        }

        // 3. Заменяем isAuthorized на статус
        res.put("isAuthorized", status);
        res.put("Message", message);

        return ResponseEntity.ok(res);
    }
}
