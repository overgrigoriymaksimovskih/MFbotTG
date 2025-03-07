package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.masterfood.entity.AppDocument;
import pro.masterfood.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
