package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {

}
