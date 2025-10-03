package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.service.RedirectService;

@Component
public class RedirectServiceImpl implements RedirectService {

    private final AppUserDAO appUserDAO;
    public RedirectServiceImpl(AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }

    @Override
    public String producerAnswer(Long telegramUserId) {
        String redirectLink = "{\"status\": \"success\", \"userId\": " + "User not found" + "}";

        var optional = appUserDAO.findByTelegramUserId(telegramUserId);
        var user = optional.get();
        if(user != null){
            if(user.getIsActive()){
                redirectLink = "{\"status\": \"success\", \"userSiteId\": " + user.getSiteUserId().toString() + "}";
            }else{
                redirectLink = "{\"status\": \"success\", \"userIsNotActiveSiteId\": " + user.getSiteUserId().toString() + "}";
            }

        }

        return redirectLink;
    }
}
