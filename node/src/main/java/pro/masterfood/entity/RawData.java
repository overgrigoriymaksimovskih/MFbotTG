package pro.masterfood.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.Hibernate;
import org.hibernate.type.SqlTypes;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Objects;

@Entity
@Table(name = "raw_data")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Update event;

    public RawData() {
        super(); // Вызов конструктора суперкласса (Object), если необходимо
    }

    public RawData(Update event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Update getEvent() {
        return event;
    }

    public void setEvent(Update event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "id=" + id +
                ", event=" + event +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Update event;

        private Builder() {}

        public Builder event(Update event) {
            this.event = event;
            return this;
        }

        public RawData build() {
            RawData rawData = new RawData();
            rawData.setEvent(event);
            return rawData;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RawData rawData = (RawData) o;
        return id != null && Objects.equals(id, rawData.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}