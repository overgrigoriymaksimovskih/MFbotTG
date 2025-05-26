package pro.masterfood.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Getter
@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

//    @Value("${spring.rabbitmq.queues.doc-message-update}")
//    private String docMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;


    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue textMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(textMessageUpdateQueue, true, false, false, args);
    }

//    @Bean
//    public Queue docMessageQueue() {
//        return new Queue(docMessageUpdateQueue);
//    }

    @Bean
    public Queue photoMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(photoMessageUpdateQueue, true, false, false, args);
    }

    @Bean
    public Queue answerMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(answerMessageQueue, true, false, false, args);
    }
}
