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

//    @Lob
    private byte[] fileAsArrayOfBytes;

    public BinaryContent() {
    }

    public BinaryContent(Long id, byte[] fileAsArrayOfBytes) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryContent that = (BinaryContent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", fileAsArrayOfBytes=" + Arrays.toString(fileAsArrayOfBytes) +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private byte[] fileAsArrayOfBytes;

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

        public BinaryContent build() {
            BinaryContent binaryContent = new BinaryContent();
            binaryContent.setId(id);
            binaryContent.setFileAsArrayOfBytes(fileAsArrayOfBytes);
            return binaryContent;
        }
    }
}