package pro.masterfood.service.impl;

import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.UpdateProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UpdateProducerImpl implements UpdateProducer {
    private static final Logger log = LoggerFactory.getLogger(UpdateProducerImpl.class);
    private final RabbitTemplate rabbitTemplate;
    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void produce(String rabbitQueue, Update update) {
        try {
            rabbitTemplate.convertAndSend(rabbitQueue, update);
        } catch (AmqpException e) {
            log.error("Ошибка при отправке сообщения в очередь {}: {}", rabbitQueue, e.getMessage(), e);
        }
    }
}
