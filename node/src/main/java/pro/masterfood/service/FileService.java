package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.enums.LinkType;

public interface FileService {
    AppPhoto processPhoto(Message telegramMessage, AppUser appUser, String message);
    String generateLink(Long docId, LinkType linkType);
}
