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

    public String getMessageText(String message, StringBuilder sb) {
        try {
            DistributionMessage distributionMessage = objectMapper.readValue(message, DistributionMessage.class);
            sb.append("Сообщение JSON десериализовано успешно\n");
            return distributionMessage.getMessageText();
        } catch (IOException e) {
            System.err.println("Ошибка десериализации JSON: " + e.getMessage());
            sb.append("Ошибка при десериализации сообщения из JSON \n");
            return null; // Или выбросить исключение
        }
    }

    public List<Long> getChatsIds(String message, StringBuilder sb) {
        try {
            DistributionMessage distributionMessage = objectMapper.readValue(message, DistributionMessage.class);
            List<Long> usersChatIds = new ArrayList<>();
            List<String> userList = distributionMessage.getUserList();
            int botUsersCount = 0;
            sb.append("Список пользователей САЙТА успешно десериализован из JSON\n");

            for (String siteUserIdString : userList) { // Итерируемся по списку строк
                try {
                    Long siteUserId = Long.parseLong(siteUserIdString); // Преобразуем строку в Long
                    List<AppUser> appUsers = appUserDAO.findBySiteUserId(siteUserId);
                    if (!appUsers.isEmpty()) {
                        for (AppUser appUser : appUsers) {
                            botUsersCount++;
                            Long telegramUserId = appUser.getTelegramUserId();
                            if (telegramUserId != null) {
                                usersChatIds.add(telegramUserId);
                            } else {
                                System.err.println("Для siteUserId " + siteUserIdString + " не найден telegramUserId");
                            }
                        }
                    } else {
                        sb.append("Не найден AppUser с siteUserId: " + siteUserIdString + "\n");
                        System.err.println("Не найден AppUser с siteUserId: " + siteUserIdString);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка преобразования siteUserId в Long: " + e.getMessage());
                    sb.append("Ошибка при составлении списка пользователей бота для рассылки\n");
                }
            }
            sb.append("Список пользователей БОТА для рассылки составлен успешно\n");
            sb.append("Найдено " + botUsersCount + " пользователей\n");
            return usersChatIds;

        } catch (IOException e) {
            System.err.println("Ошибка десериализации JSON: " + e.getMessage());
            sb.append("НЕ УДАЛОСЬ десериализовать список пользователей из JSON\n");
            return null; // Или выбросить исключение
        }
    }

    @Data
    static class DistributionMessage {
        private String messageText;
//        private List<User> userList;
        private List<String> userList; // Теперь это список строк
    }

    @Data
    static class User {
        private String siteUserId; //  siteUserId в виде строки
    }
}