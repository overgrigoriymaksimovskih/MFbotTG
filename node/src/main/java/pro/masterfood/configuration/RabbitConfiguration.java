package pro.masterfood.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Value("${spring.rabbitmq.queues.answer-to-1C}")
    private String answerTo1CQueue;

    @Value("${spring.rabbitmq.queues.message-from-1C}")
    private String messageFrom1CQueue;

    @Bean
    public MessageConverter jsonMessageConverter(){
        log.error("First test error entry. ALL OK");
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue answerTo1CQueue() {
        return new Queue(answerTo1CQueue);
    }

    @Bean
    public Queue messageFrom1CQueue() {
        return new Queue(messageFrom1CQueue);
    }

}
