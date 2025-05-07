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

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(registrationMailQueue, true, false, false, args);
    }

}
