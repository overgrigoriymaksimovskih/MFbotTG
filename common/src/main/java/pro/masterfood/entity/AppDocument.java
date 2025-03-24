package pro.masterfood.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_document")
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramField;

    private String docName;

    @OneToOne
    private BinaryContent binaryContent;

    private String mimeType;

    private Long fileSize;

    public AppDocument() {
    }

    public AppDocument(Long id, String telegramField, String docName, BinaryContent binaryContent, String mimeType, Long fileSize) {
        this.id = id;
        this.telegramField = telegramField;
        this.docName = docName;
        this.binaryContent = binaryContent;
        this.mimeType = mimeType;
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

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public BinaryContent getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(BinaryContent binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppDocument that = (AppDocument) o;
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
                ", docName='" + docName + '\'' +
                ", binaryContent=" + binaryContent +
                ", mimeType='" + mimeType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String telegramField;
        private String docName;
        private BinaryContent binaryContent;
        private String mimeType;
        private Long fileSize;

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

        public Builder docName(String docName) {
            this.docName = docName;
            return this;
        }

        public Builder binaryContent(BinaryContent binaryContent) {
            this.binaryContent = binaryContent;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public AppDocument build() {
            AppDocument appDocument = new AppDocument();
            appDocument.setId(id);
            appDocument.setTelegramField(telegramField);
            appDocument.setDocName(docName);
            appDocument.setBinaryContent(binaryContent);
            appDocument.setMimeType(mimeType);
            appDocument.setFileSize(fileSize);
            return appDocument;
        }
    }
}
