package pro.masterfood.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.masterfood.entity.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);
    Optional<AppUser> findById(Long id);
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findBySiteUserId(Long siteUserId);
}
