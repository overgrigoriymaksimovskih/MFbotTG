package pro.masterfood.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;
import pro.masterfood.entity.AppUser;
import pro.masterfood.dao.AppUserDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OneCmessageHandler {
    private final ObjectMapper objectMapper;
    private final AppUserDAO appUserDAO;

    public OneCmessageHandler(ObjectMapper objectMapper, AppUserDAO appUserDAO) {
        this.objectMapper = objectMapper;
        this.appUserDAO = appUserDAO;
    }

    public String getMessageText(String message) {
        try {
            DistributionMessage distributionMessage = objectMapper.readValue(message, DistributionMessage.class);
            return distributionMessage.getMessageText();
        } catch (IOException e) {
            System.err.println("Ошибка десериализации JSON: " + e.getMessage());
            return null; // Или выбросить исключение
        }
    }

    public List<Long> getChatsIds(String message) {
        try {
            DistributionMessage distributionMessage = objectMapper.readValue(message, DistributionMessage.class);
            List<Long> usersChatIds = new ArrayList<>();
            List<User> userList = distributionMessage.getUserList();

            for (User user : userList) {
                // Ищем пользователя в БД по siteUserId
                try {
                    Long siteUserId = Long.parseLong(user.getSiteUserId());
                    Optional<AppUser> appUserOptional = appUserDAO.findBySiteUserId(siteUserId); // Ищем по siteUserId

                    if (appUserOptional.isPresent()) {
                        AppUser appUser = appUserOptional.get();
                        Long telegramUserId = appUser.getTelegramUserId(); // Получаем telegramUserId
                        if (telegramUserId != null) {
                            usersChatIds.add(telegramUserId); // Добавляем telegramUserId в список
                        } else {
                            System.err.println("Для siteUserId " + user.getSiteUserId() + " не найден telegramUserId");
                        }

                    } else {
                        System.err.println("Не найден AppUser с siteUserId: " + user.getSiteUserId());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка преобразования siteUserId в Long: " + e.getMessage());
                }
            }
            return usersChatIds;

        } catch (IOException e) {
            return new ArrayList<>(610200129);
//            System.err.println("Ошибка десериализации JSON: " + e.getMessage());
//            return null; // Или выбросить исключение
        }

    }


    @Data
    static class DistributionMessage {
        private String messageText;
        private List<User> userList;
    }

    @Data
    static class User {
        private String siteUserId; //  siteUserId в виде строки
    }
}