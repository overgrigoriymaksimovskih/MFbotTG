package pro.masterfood.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_photo")
public class AppPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramField;

    @OneToOne
    private BinaryContent binaryContent;

    private Integer fileSize;

    public AppPhoto() {
    }

    public AppPhoto(Long id, String telegramField, BinaryContent binaryContent, Integer fileSize) {
        this.id = id;
        this.telegramField = telegramField;
        this.binaryContent = binaryContent;
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                ", telegramField='" + telegramField + '\'' +
                ", binaryContent=" + binaryContent +
                ", fileSize=" + fileSize +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String telegramField;
        private BinaryContent binaryContent;
        private Integer fileSize;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public Builder fileSize(Integer fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public AppPhoto build() {
            AppPhoto appDocument = new AppPhoto();
            appDocument.setId(id);
            appDocument.setTelegramField(telegramField);
            appDocument.setBinaryContent(binaryContent);
            appDocument.setFileSize(fileSize);
            return appDocument;
        }
    }
}
