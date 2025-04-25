package pro.masterfood.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dto.MailParams;
import pro.masterfood.service.MailSenderService;
import pro.masterfood.service.ProducerService;

@Component
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;


    private final ProducerService producerService;
    public MailSenderServiceImpl(JavaMailSender javaMailSender, ProducerService producerService) {
        this.javaMailSender = javaMailSender;
        this.producerService = producerService;
    }

    @Override
    public void send(MailParams mailParams) {
        var subject = "Тестовое письмо из бота";
//        var messageBody = getActivationMailBody(mailParams.getId());
//        var emailTo = mailParams.getEmailTo();
        var messageBody = "Текст тестового письма из бота: \n"
                + mailParams.getEmail() + " "
                + mailParams.getPhoneNumber() + " "
                + mailParams.getSiteUid() +"\n"
                + mailParams.getMessage() + "\n"
                ;
        var emailTo = "master-2m@yandex.ru";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);
        try {
            javaMailSender.send(mailMessage);
            sendAnswer("Успешно отправлено", mailParams.getChatId());
        } catch (MailException e) {
            sendAnswer("Ошибка отправки " + e.getMessage(), mailParams.getChatId());
        }
    }

    @Override
    public void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }
//    private String getActivationMailBody(String id) {
//        var msg = String.format
//                ("Для завершения регистрации перейдите по ссылке:\n%s",
//                activationServiceUri);
//        return msg.replace("{id}", id);
//    }
}
