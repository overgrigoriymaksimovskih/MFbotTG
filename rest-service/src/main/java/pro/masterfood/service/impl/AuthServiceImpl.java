package pro.masterfood.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dto.AuthResponse;
import pro.masterfood.entity.AppUser;
import pro.masterfood.exceptions.*;
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

    public AuthResponse validateAndGetUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());  // userId из JWT

            Optional<AppUser> optionalUser = appUserDAO.findById(userId);

            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException("ID " + userId + " ошибка консистентности 0. Пожалуйста отправьте эту ошибку в форме обратной связи на этом сайте");
            }

            AppUser user = optionalUser.get();

            // Проверки на null для полей пользователя
            Long siteUserId = user.getSiteUserId();
            String phoneNumber = user.getPhoneNumber();

            if (siteUserId == null) {
                throw new UserSiteIdIsNullException("ID " + userId + " ошибка консистентности 1. Пожалуйста отправьте эту ошибку в форме обратной связи на этом сайте");
            }

            if (phoneNumber == null) {
                throw new UserPhoneIsNullException("ID " + userId + " ошибка консистентности 2. Пожалуйста отправьте эту ошибку в форме обратной связи на этом сайте");
            }

            return new AuthResponse(siteUserId, phoneNumber);

        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Срок действия токена истёк: " + e.getMessage());
        } catch (JwtException e) {
            // Ловит остальные JwtException (SignatureException, MalformedJwtException и т.д.)
            throw new TokenInvalidException("Токен недействителен: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new TokenInvalidException("Ошибка консистентности 3. Пожалуйста отправьте эту ошибку в форме обратной связи на этом сайте");
        }
    }
}
