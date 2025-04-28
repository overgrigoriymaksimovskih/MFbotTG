package pro.masterfood.service.impl;



import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import pro.masterfood.dto.MailParams;
import pro.masterfood.service.MailSenderService;
import pro.masterfood.service.ProducerService;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import pro.masterfood.dto.MailParams;


@Component
public class MailSenderServiceImpl  implements MailSenderService {

    private final JavaMailSender javaMailSender;
    private final ProducerService producerService;

    public MailSenderServiceImpl(JavaMailSender javaMailSender, ProducerService producerService) {
        this.javaMailSender = javaMailSender;
        this.producerService = producerService;
    }

    public void sendSimpleEmail(String to, String subject, String text, MailParams mailParams) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8"); // Укажем кодировку UTF-8

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false означает, что это plain text

            javaMailSender.send(message);
            sendAnswer("Успешно отправлено MailSenderServiceImpl", mailParams.getChatId());
        } catch (MessagingException e) {
            sendAnswer("Ошибка при отправке письма (MessagingException): " + e.getMessage(), mailParams.getChatId());
        } catch (MailException e) {
            sendAnswer("Ошибка при отправке письма (MailException): " + e.getMessage(), mailParams.getChatId());
        }
    }


    @Override
    public void send(MailParams mailParams) {
        sendSimpleEmail("master-2m@yandex.ru", "Тестовое письмо", "Это простое тестовое письмо.", mailParams);
    }

    @Override
    public void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

}
