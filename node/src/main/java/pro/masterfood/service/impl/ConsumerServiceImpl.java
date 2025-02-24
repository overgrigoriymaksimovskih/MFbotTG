package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.ProducerService;

import static pro.masterfood.model.RabbitQueue.*;

@Component
public class ConsumerServiceImpl implements ConsumerService {
    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    private final ProducerService producerService;

    public ConsumerServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: text message is received");

        var message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Hello from NODE TEXT");
        producerService.producerAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: document message is received");
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: photo message is received");
    }
}
