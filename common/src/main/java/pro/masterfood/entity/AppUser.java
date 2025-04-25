package pro.masterfood.entity;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import pro.masterfood.enums.UserState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppPhoto> photos; // Список фотографий, принадлежащих пользователю

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String phoneNumber;

    private Long siteUserId;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState state;

    public AppUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Геттер для photos
    public List<AppPhoto> getPhotos() {
        return photos;
    }

    // Сеттер для photos (обычно не нужен, но зависит от логики)
    public void setPhotos(List<AppPhoto> photos) {
        this.photos = photos;
    }

    public Long getTelegramUserId() {
        return telegramUserId;
    }

    public void setTelegramUserId(Long telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

    public LocalDateTime getFirstLoginDate() {
        return firstLoginDate;
    }

    public void setFirstLoginDate(LocalDateTime firstLoginDate) {
        this.firstLoginDate = firstLoginDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSiteUserId() {
        return siteUserId;
    }

    public void setSiteUserId(Long siteUserId) {
        this.siteUserId = siteUserId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUser appUser = (AppUser) o;
        return id != null && Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", telegramUserId=" + telegramUserId +
                ", firstLoginDate=" + firstLoginDate +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", siteUserId='" + siteUserId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isActive=" + isActive +
                ", state=" + state +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long telegramUserId;
        private LocalDateTime firstLoginDate;
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private Long siteUserId;
        private String phoneNumber;
        private Boolean isActive;
        private UserState state;

        private Builder() {
        }

        public Builder telegramUserId(Long telegramUserId) {
            this.telegramUserId = telegramUserId;
            return this;
        }

        public Builder firstLoginDate(LocalDateTime firstLoginDate) {
            this.firstLoginDate = firstLoginDate;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder siteUserId(Long siteUserId) {
            this.siteUserId = siteUserId;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder state(UserState state) {
            this.state = state;
            return this;
        }

        public AppUser build() {
            AppUser appUser = new AppUser();
            appUser.setTelegramUserId(telegramUserId);
            appUser.setFirstLoginDate(firstLoginDate);
            appUser.setFirstName(firstName);
            appUser.setLastName(lastName);
            appUser.setUsername(username);
            appUser.setEmail(email);
            appUser.setSiteUserId(siteUserId);
            appUser.setPhoneNumber(phoneNumber);
            appUser.setIsActive(isActive);
            appUser.setState(state);
            return appUser;
        }
    }
}