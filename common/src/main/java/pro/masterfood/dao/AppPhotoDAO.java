package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.AppPhoto;

import java.util.List;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
    List<AppPhoto> findByOwnerId(Long ownerId);  //  Метод для поиска фото по ID владельца
    void deleteByOwnerId(Long ownerId); // метод для удаления фото по ID владельца
}
