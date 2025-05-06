package pro.masterfood.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.service.ProducerService;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class ProducerServiceImpl implements ProducerService {
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
        rabbitTemplate.convertAndSend(answerMessageQueue, sendMessage);
    }

    @Override
    public void producerAnswerTo1C(String answerTo1C ) {

        try {
            // Создаем ObjectMapper для преобразования объекта в JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(answerTo1C); // Преобразуем String в JSON

            // Создаем MessageProperties
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON); // Указываем ContentType
            messageProperties.setContentEncoding("UTF-8"); // Указываем кодировку

            // Преобразуем JSON в byte[]
            byte[] messageBodyBytes = jsonMessage.getBytes("UTF-8");

            // Создаем Message
            Message message = new Message(messageBodyBytes, messageProperties);

            // Отправляем Message
            rabbitTemplate.send(answerTo1CQueue, message);

        } catch (Exception e) {
            e.printStackTrace();
        }



//        // Создаем MessageProperties
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN); // Указываем ContentType
//        messageProperties.setContentEncoding("UTF-8"); // Указываем кодировку
//
//        // Преобразуем строку в byte[]
//        byte[] messageBodyBytes = answerTo1C.getBytes();
//
//        // Создаем Message
//        Message message = new Message(messageBodyBytes, messageProperties);
//
//        // Отправляем Message
//        rabbitTemplate.send(answerTo1CQueue, message);
////        rabbitTemplate.convertAndSend(answerTo1CQueue, answerTo1C); // Используем внедренное имя очереди
    }
}
