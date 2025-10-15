package pro.masterfood.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ConsumerServiceImpl implements ConsumerService {
    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextAndContactMessageUpdates(Update update) {
        log.debug("NODE: text message is received");
        mainService.processTextAndContactMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: photo message is received");
        mainService.processPhotoMessage(update);;
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.message-from-1C}")
    public void consumeDocMessageUpdates(String messageFromRest) {
        log.debug("NODE: document message is received");
        mainService.processDocMessage(messageFromRest);
    }
}
