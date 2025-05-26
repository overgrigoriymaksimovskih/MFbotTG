package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.MainService;

@Component
public class ConsumerServiceImpl implements ConsumerService {
    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: photo message is received");
        mainService.processPhotoMessage(update);;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.message-from-1C}")
    public void consumeDocMessageUpdates(String oneCmessage) {
        log.debug("NODE: document message is received");
        mainService.processDocMessage(oneCmessage);
    }
}
