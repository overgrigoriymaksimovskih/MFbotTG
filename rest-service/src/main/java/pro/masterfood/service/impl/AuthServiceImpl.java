package pro.masterfood.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserDAO appUserDAO;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public AuthServiceImpl(AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    /**
     * Валидирует JWT-токен и извлекает данные пользователя.
     * @param token JWT-токен
     * @return Optional с данными пользователя (userId, phone, и т.д.), если токен валиден
     */
    public Optional<AuthResponse> validateAndGetUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());  // userId из JWT
            Optional<AppUser> optionalUser = appUserDAO.findById(userId);  // Или другая метода поиска

            if (optionalUser.isEmpty()) {
                return Optional.empty();  // Пользователь не найден
            }
            AppUser user = optionalUser.get();
            return Optional.of(new AuthResponse(user.getSiteUserId(), user.getPhoneNumber()));

        } catch (JwtException | NumberFormatException e) {
            return Optional.empty();  // Токен недействителен (прошёл срок, неверная подпись и т.д.)
        }
    }
}
