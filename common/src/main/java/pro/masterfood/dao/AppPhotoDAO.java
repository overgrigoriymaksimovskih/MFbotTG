package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.AppPhoto;

import java.util.List;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
    List<AppPhoto> findByKey(Long key);
}
