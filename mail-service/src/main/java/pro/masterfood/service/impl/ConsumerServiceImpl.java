package pro.masterfood.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pro.masterfood.dto.MailParams;
import pro.masterfood.service.ConsumerService;
import pro.masterfood.service.MailSenderService;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        try {
            mailSenderService.send(mailParams);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
            mailSenderService.sendAnswer("Ошибка при попытке отправки письма " + e.getMessage(), mailParams.getChatId());
            // Здесь можно добавить логику повторной отправки или отправки в DLQ
        }
    }
}
