package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.LoginParams;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.UserActivationService;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final UserActivationService userActivationService;
    private final AppUserDAO appUserDAO;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.login}")
    public void consumeLogin(LoginParams mailParams) {

        var optional = appUserDAO.findById(1L);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
        }


        String email = mailParams.getEmail();
        String password = mailParams.getPassword();
        var res = userActivationService.activationFromSite(email, password);
        // 1. Извлекаем Map "isAuthorized"
        Map<String, Object> isAuthorizedMap = (Map<String, Object>) res.get("isAuthorized");


        // 2. Извлекаем значение "Status" из isAuthorizedMap
        String status = "failure"; // Значение по умолчанию
        String message = "Не удалось связаться с сервисом авторизации...";
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
    }
}
