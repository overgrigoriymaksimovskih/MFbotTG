package pro.masterfood.entity;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import pro.masterfood.entity.enums.UserState;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

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
            appUser.setIsActive(isActive);
            appUser.setState(state);
            return appUser;
        }
    }
}