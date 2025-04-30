package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.masterfood.entity.AppDocument;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;
import pro.masterfood.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage, AppUser appUser);
    String generateLink(Long docId, LinkType linkType);
}
