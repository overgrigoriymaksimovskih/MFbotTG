package pro.masterfood.dto;

//record (коротко и удобно) неизменяемый
//public record ChatInfo(Long chatId, String siteId) {}

// Или простой класс (для старых версий Java)
public class ChatInfo {
    private final Long chatId;
    private final String siteId;

    public ChatInfo(Long chatId, String siteId) {
        this.chatId = chatId;
        this.siteId = siteId;
    }

    public Long getChatId() { return chatId; }
    public String getSiteId() { return siteId; }

    // equals/hashCode не обязательны, если не используешь в Set/Map
}

