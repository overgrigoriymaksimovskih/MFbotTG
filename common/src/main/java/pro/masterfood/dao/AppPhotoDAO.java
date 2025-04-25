package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.AppPhoto;

import java.util.Optional;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
    Optional<AppPhoto> findById(Long id);
}
