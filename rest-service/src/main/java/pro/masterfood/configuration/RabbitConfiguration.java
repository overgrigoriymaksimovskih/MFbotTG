package pro.masterfood.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;


@Getter
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.queues.login}")
    private String loginQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue loginMessageQueue() {
        //установим время жизни для сообщения после его попадания в очередь, на случай если консьюмер
        // не сможет его обработать по какой то причине
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 6000);
        return new Queue(loginQueue, true, false, false, args);
    }



    // Этот бин (ниже) нужен для создания бина rabbitListenerContainerFactory, который будет использоваться
    // для создания слушателей сообщений, аннотированных @RabbitListener. И в этих слушателях станет доступно использование
    // chanel, что в свою очередь позволит отклонять сообщения которые не могут быть обработаны в листенере и они
    // соответственно не будут висеть в очереди в попытке быть обработанными
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // factory.setChannelTransacted(true); // Опционально, если нужна транзакционность
        return factory;
    }

}
