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
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        try {
            mailSenderService.send(mailParams);
            System.out.println("Письмо успешно отправлено для: " + mailParams.getId()); // Добавьте логирование успеха
        } catch (Exception e) {
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
            e.printStackTrace();
            // Здесь можно добавить логику повторной отправки или отправки в DLQ
        }
    }
}
