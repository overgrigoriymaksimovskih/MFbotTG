package pro.masterfood.dto;

public class ProcessingRequestFromOneS_Queue {
    private String correlationId; // Генерируется контроллером
    private String jsonData; // JSON-строка от клиента (без correlationId)

    public ProcessingRequestFromOneS_Queue() {}

    public ProcessingRequestFromOneS_Queue(String correlationId, String jsonData) {
        this.correlationId = correlationId;
        this.jsonData = jsonData;
    }

    // getters и setters
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getJsonData() { return jsonData; }
    public void setJsonData(String jsonData) { this.jsonData = jsonData; }
}