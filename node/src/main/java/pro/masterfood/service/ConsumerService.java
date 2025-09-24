package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextAndContactMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);

    void consumeDocMessageUpdates(String oneCmessage);
}
