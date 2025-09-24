package pro.masterfood.service;

import pro.masterfood.entity.AppUser;

public interface AppUserService {
    String chooseLoginType(AppUser appUser);
    String loginByPassword(AppUser appUser);
//-------------------------------------------------------
    String loginByPhone(AppUser appUser);
    String loginByPhoneManualInput(AppUser appUser);
    String loginByPhoneShare(AppUser appUser);
//-------------------------------------------------------
    String checkContact(Long chatId, AppUser appUser, String phone);
    String checkPhone(Long chatId, AppUser appUser, String phone);
    String checkSMS (Long chatId, AppUser appUser, String sms);

    String setEmail (AppUser appUser, String email);
    String checkPassword (Long chatId, AppUser appUser, String password);

    String checkBalance (Long chatId, AppUser appUser);
    String checkStatus (Long chatId, AppUser appUser);

    String createReportMail(Long chatId, AppUser appUser);
    String sendReportMail(Long chatId, AppUser appUser, String message);

    String quit(Long chatId, AppUser appUser);
    String exit(Long chatId, AppUser appUser);
}
