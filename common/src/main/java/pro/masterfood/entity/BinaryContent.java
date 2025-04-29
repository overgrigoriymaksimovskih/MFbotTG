package pro.masterfood.entity;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "binary_content")
public class BinaryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_as_array_of_bytes", columnDefinition = "bytea")
    private byte[] fileAsArrayOfBytes;

    @Column(name = "file_path") // Добавляем поле filePath
    private String filePath;

    public BinaryContent() {
    }

    public BinaryContent(Long id, byte[] fileAsArrayOfBytes, String filePath) {  // Добавляем в конструктор
        this.id = id;
        this.fileAsArrayOfBytes = fileAsArrayOfBytes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFileAsArrayOfBytes() {
        return fileAsArrayOfBytes;
    }

    public void setFileAsArrayOfBytes(byte[] fileAsArrayOfBytes) {
        this.fileAsArrayOfBytes = fileAsArrayOfBytes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryContent that = (BinaryContent) o;
        return Objects.equals(id, that.id) && Arrays.equals(fileAsArrayOfBytes, that.fileAsArrayOfBytes) && Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filePath);
        result = 31 * result + Arrays.hashCode(fileAsArrayOfBytes);
        return result;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", fileAsArrayOfBytes=" + Arrays.toString(fileAsArrayOfBytes) +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private byte[] fileAsArrayOfBytes;
        private String filePath;  //  Добавляем filePath в Builder

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileAsArrayOfBytes(byte[] fileAsArrayOfBytes) {
            this.fileAsArrayOfBytes = fileAsArrayOfBytes;
            return this;
        }

        public Builder filePath(String filePath) {  // Добавляем метод для установки filePath
            this.filePath = filePath;
            return this;
        }

        public BinaryContent build() {
            BinaryContent binaryContent = new BinaryContent();
            binaryContent.setId(id);
            binaryContent.setFileAsArrayOfBytes(fileAsArrayOfBytes);
            binaryContent.setFilePath(filePath);  // Устанавливаем filePath
            return binaryContent;
        }
    }
}