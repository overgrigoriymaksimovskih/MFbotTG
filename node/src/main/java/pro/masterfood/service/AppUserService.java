package pro.masterfood.service;

import pro.masterfood.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail (AppUser appUser, String email);
    String checkPassword (Long chatId, AppUser appUser, String password);

    String checkBalance (Long chatId, AppUser appUser);
    String checkStatus (Long chatId, AppUser appUser);

    String createReportMail(Long chatId, AppUser appUser);
    String sendReportMail(Long chatId, AppUser appUser, String message);

    String quit(Long chatId, AppUser appUser);
    String exit(Long chatId, AppUser appUser);
}
