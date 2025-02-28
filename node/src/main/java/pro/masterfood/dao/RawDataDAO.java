package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
