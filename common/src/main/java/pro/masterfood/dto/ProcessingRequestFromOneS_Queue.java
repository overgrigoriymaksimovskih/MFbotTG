package pro.masterfood.dto;

import java.util.List;

public class ProcessingRequestFromOneS_Queue {
    private String correlationId; // Генерируется контроллером
    private String messageText;   // Текст сообщения
    private List<String> userList; // Список пользователей

    public ProcessingRequestFromOneS_Queue() {}

    public ProcessingRequestFromOneS_Queue(String correlationId, String messageText, List<String> userList) {
        this.correlationId = correlationId;
        this.messageText = messageText;
        this.userList = userList;
    }

    // Геттеры и сеттеры
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
