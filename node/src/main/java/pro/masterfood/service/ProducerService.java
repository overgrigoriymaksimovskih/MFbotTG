package pro.masterfood.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerAnswer(SendMessage sendMessage);
    void producerAnswerTo1C(String answerTo1C );
}
