package pro.masterfood.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_photo")
public class AppPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Одна фотография принадлежит одному пользователю
    @JoinColumn(name = "owner_id") // Указываем имя колонки, связывающей таблицы
    private AppUser owner; // Ссылка на сущность AppUser
    private String telegramField;

    @OneToOne(cascade = CascadeType.REMOVE)
    private BinaryContent binaryContent;

    private String message;

    private Integer fileSize;

    public AppPhoto() {
    }

    public AppPhoto(Long id, String telegramField, BinaryContent binaryContent,String message, Integer fileSize) {
        this.id = id;
        this.telegramField = telegramField;
        this.binaryContent = binaryContent;
        this.message = message;
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public String getTelegramField() {
        return telegramField;
    }

    public void setTelegramField(String telegramField) {
        this.telegramField = telegramField;
    }

    public BinaryContent getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(BinaryContent binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppPhoto that = (AppPhoto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AppDocument{" +
                "id=" + id +
                "owner=" + owner +
                ", telegramField='" + telegramField + '\'' +
                ", binaryContent=" + binaryContent +
                ", message=" + message +
                ", fileSize=" + fileSize +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AppUser owner;
        private String telegramField;
        private BinaryContent binaryContent;
        private String message;
        private Integer fileSize;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder owner(AppUser owner) {
            this.owner = owner;
            return this;
        }

        public Builder telegramField(String telegramField) {
            this.telegramField = telegramField;
            return this;
        }

        public Builder binaryContent(BinaryContent binaryContent) {
            this.binaryContent = binaryContent;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder fileSize(Integer fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public AppPhoto build() {
            AppPhoto appDocument = new AppPhoto();
            appDocument.setId(id);
            appDocument.setOwner(owner);
            appDocument.setTelegramField(telegramField);
            appDocument.setBinaryContent(binaryContent);
            appDocument.setMessage(message);
            appDocument.setFileSize(fileSize);
            return appDocument;
        }
    }
}
