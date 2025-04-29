package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.masterfood.entity.AppPhoto;

@Repository
public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {

}
