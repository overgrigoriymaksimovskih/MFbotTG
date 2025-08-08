package pro.masterfood.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import java.util.HashMap;
import java.util.Map;


@Getter
@Configuration
public class RabbitConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;


    @Bean
    public MessageConverter jsonMessageConverter(){
        log.error("First test error entry. ALL OK");
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине (ничего умнее не придумал. Раббит проталкивающий брокер
        // и пара сообщений вешают весь сервер, даже если принимающий сервис очнется, сервер не даст обработать больше
        // 10 сообщений просто повиснет...)
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(textMessageUpdateQueue, true, false, false, args);
    }

    @Bean
    public Queue photoMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине (ничего умнее не придумал. Раббит проталкивающий брокер
        // и пара сообщений вешают весь сервер, даже если принимающий сервис очнется, сервер не даст обработать больше
        // 10 сообщений просто повиснет...)
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(photoMessageUpdateQueue, true, false, false, args);
    }

    @Bean
    public Queue answerMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине (ничего умнее не придумал. Раббит проталкивающий брокер
        // и пара сообщений вешают весь сервер, даже если принимающий сервис очнется, сервер не даст обработать больше
        // 10 сообщений просто повиснет...)
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(answerMessageQueue, true, false, false, args);
    }
}
