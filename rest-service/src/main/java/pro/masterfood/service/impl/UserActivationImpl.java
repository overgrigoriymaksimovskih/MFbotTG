package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.service.UserActivationService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.SendPostForCheckUser;

import java.util.Map;

@Component
public class UserActivationImpl implements UserActivationService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final SendPostForCheckUser sendPostForCheckUser;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;

    public UserActivationImpl(SendPostForCheckUser sendPostForCheckUser, AppUserDAO appUserDAO, Decoder decoder) {
        this.sendPostForCheckUser = sendPostForCheckUser;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
    }

//    @Override
//    public boolean activation(String cryptoUserId) {
//        var userId = decoder.idOf(cryptoUserId);
//        log.debug(String.format("User activation with user-id=%s", userId));
//        if (userId == null) {
//            return false;
//        }
//
//        var optional = appUserDAO.findById(userId);
//        if (optional.isPresent()) {
//            var user = optional.get();
//            user.setIsActive(true);
//            appUserDAO.save(user);
//            return true;
//        }
//        return false;
//    }
    @Override
    public Map<String, Object> activationMf(String email,
                                            String password) {

        return sendPostForCheckUser.getLoginResponse(email, password);
    }
}
