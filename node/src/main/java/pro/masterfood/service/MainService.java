package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processDocMessage(String oneCmessage);
    void processPhotoMessage(Update update);
}
