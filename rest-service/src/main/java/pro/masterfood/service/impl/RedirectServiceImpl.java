//package pro.masterfood.service.impl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import org.springframework.stereotype.Component;
//import pro.masterfood.dao.AppUserDAO;
//import pro.masterfood.entity.AppUser;
//import pro.masterfood.service.RedirectService;
//import pro.masterfood.utils.InitDataChecker;
//
//import  com.fasterxml.jackson.databind.JsonNode;
//import  com.fasterxml.jackson.databind.ObjectMapper;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Component
//public class RedirectServiceImpl implements RedirectService {
//    private final InitDataChecker initDataChecker;
//    private final AppUserDAO appUserDAO;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    public RedirectServiceImpl(AppUserDAO appUserDAO, InitDataChecker initDataChecker) {
//        this.appUserDAO = appUserDAO;
//        this.initDataChecker = initDataChecker;
//    }
//
//    @Override
//    public boolean validateInitData(String initData, String botToken) {
//        return initDataChecker.validate(initData, botToken);
//    }
//
//    @Override
//    public String generateToken(String initData) {
//        long telegramUserID;
//        try{
//            JsonNode rootNode = objectMapper.readTree(initData);
//            JsonNode userNode = rootNode.path("user");
//            if (userNode.isMissingNode()){
//
//            }
//            telegramUserID = userNode.path("id").asLong();
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        Optional<AppUser> userDataBaseId = appUserDAO.findByTelegramUserId(telegramUserID);
//        return userDataBaseId + "token";
//    }
//
//
//
//}
////    String redirectLink = "{\"status\": \"success\", \"userId\": " + "User not found" + "}";
////
////    var optional = appUserDAO.findByTelegramUserId(telegramUserId);
////    var user = optional.get();
////        if(user != null){
////                if(user.getIsActive()){
////                redirectLink = "{\"status\": \"success\", \"userSiteId\": " + user.getSiteUserId().toString() + "}";
////                }else{
////                redirectLink = "{\"status\": \"success\", \"userIsNotActiveSiteId\": " + user.getSiteUserId().toString() + "}";
////                }
////
////                }
////
////                return redirectLink;

package pro.masterfood.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.RedirectService;
import pro.masterfood.utils.InitDataChecker;

import java.util.Date;
import java.util.Optional;

@Component
public class RedirectServiceImpl implements RedirectService {
    private final InitDataChecker initDataChecker;
    private final AppUserDAO appUserDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")  // Добавьте в application.properties: jwt.secret=your-secure-secret-key-here
    private String JWT_SECRET;

    public RedirectServiceImpl(AppUserDAO appUserDAO, InitDataChecker initDataChecker) {
        this.appUserDAO = appUserDAO;
        this.initDataChecker = initDataChecker;
    }

    @Override
    public boolean validateInitData(String initData, String botToken) {
        return initDataChecker.validate(initData, botToken);
    }

    @Override
    public String generateToken(String initData) {
        long telegramUserID;
        try {
            JsonNode rootNode = objectMapper.readTree(initData);
            JsonNode userNode = rootNode.path("user");
            if (userNode.isMissingNode()) {
                return null;  // Ошибка: пользователь не указан в initData
            }
            telegramUserID = userNode.path("id").asLong();
        } catch (Exception e) {
            return null;
        }

        // Ищем пользователя в БД
        Optional<AppUser> optionalUser = appUserDAO.findByTelegramUserId(telegramUserID);
        if (optionalUser.isEmpty()) {
            return null;  // Пользователь не найден — фронтенд должен обработать ошибку
        }
        AppUser user = optionalUser.get();

        // Генерируем JWT с userId (не с phone!)
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))  // userId из БД
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000))  // 2 минуты
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}
