package pro.masterfood.service.impl;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.service.ProducerService;

@Component
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Value("${spring.rabbitmq.queues.answer-to-1C}")
    private String answerTo1CQueue;



    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(answerMessageQueue, sendMessage);
    }

    @Override
    public void producerAnswerTo1C(String answerTo1C ) {
        rabbitTemplate.convertAndSend(answerTo1CQueue, answerTo1C);
    }
}
