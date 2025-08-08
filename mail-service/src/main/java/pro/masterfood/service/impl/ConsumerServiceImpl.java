package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dto.MailParams;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.MailSenderService;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        try {
            mailSenderService.send(mailParams);
        } catch (Exception e) {
            mailSenderService.sendAnswer("Ошибка при попытке отправки письма " + e.getMessage(), mailParams.getChatId());
            log.error("Ошибка отправки (MailServiceSenderImpl -> ConsumerServiceImpl): " + e.getMessage(), mailParams.getChatId());
            // Здесь можно добавить логику повторной отправки или отправки в DLQ
        }
    }
}
