package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
    BinaryContent findByFilePath(String filePath); // Метод для поиска по filePath
}
