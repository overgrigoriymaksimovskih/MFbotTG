package pro.masterfood.dto;

import java.util.List;

public class ProcessingResponseToOneS_Queue {
    private String correlationId; // Тот же, что в запросе
    private String messageFromNode; // описание того что происходит в node, чтобы можно было понять где ошибка
    private List<String> processedUsers; // Результат обработки

    public ProcessingResponseToOneS_Queue() {}

    public ProcessingResponseToOneS_Queue(String correlationId, String messageFromNode, List<String> processedUsers) {
        this.correlationId = correlationId;
        this.messageFromNode = messageFromNode;
        this.processedUsers = processedUsers;
    }

    // getters и setters
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getMessageFromNode() { return messageFromNode; }
    public void setMessageFromNode(String messageFromNode) { this.messageFromNode = messageFromNode; }
    public List<String> getProcessedUsers() { return processedUsers; }
    public void setProcessedUsers(List<String> processedUsers) { this.processedUsers = processedUsers; }
}