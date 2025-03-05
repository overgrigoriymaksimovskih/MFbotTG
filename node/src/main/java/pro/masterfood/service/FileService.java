package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.masterfood.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
