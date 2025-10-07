package pro.masterfood.dto;

public class AuthResponse {
    private Long siteUserId;
    private String phoneNumber;

    public AuthResponse(Long siteUserId, String phoneNumber) {
        this.siteUserId = siteUserId;
        this.phoneNumber = phoneNumber;
    }

    // Геттеры (для Jackson сериализации в JSON)
    public Long getUserId() { return siteUserId; }
    public String getPhone() { return phoneNumber; }

    // Сеттеры, если нужны
}
