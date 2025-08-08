package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.service.ProducerService;

@Component
public class ProducerServiceImpl implements ProducerService {
    private static final Logger log = LoggerFactory.getLogger(ProducerServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Value("${spring.rabbitmq.queues.answer-to-1C}")
    private String answerTo1CQueue; // Получаем только имя очереди

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        try {
            rabbitTemplate.convertAndSend(answerMessageQueue, sendMessage);
        } catch (AmqpException e) {
            log.error("Ошибка при отправке сообщения в очередь {}: {}", answerMessageQueue, e.getMessage(), e);
        }
    }

    @Override
    public void producerAnswerTo1C(String answerTo1C ) {
        try {
            rabbitTemplate.convertAndSend(answerTo1CQueue, answerTo1C); // Используем внедренное имя очереди
        } catch (AmqpException e) {
            log.error("Ошибка при отправке сообщения в очередь {}: {}", answerMessageQueue, e.getMessage(), e);
        }
    }
}
