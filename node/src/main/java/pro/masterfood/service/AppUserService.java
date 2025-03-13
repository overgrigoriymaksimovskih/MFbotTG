package pro.masterfood.service;

import pro.masterfood.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail (AppUser appUser, String email);
}
