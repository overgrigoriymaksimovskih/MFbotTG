package pro.masterfood.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        sendAnswer("Успешно получено в метод send - MailSenderServiceImpl", mailParams.getChatId());
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
        var photos = mailParams.getPhotos();

//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(emailFrom);
//        mailMessage.setTo(emailTo);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(messageBody);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true - multipart

            helper.setFrom(emailFrom); // Используй emailFrom из настроек
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(messageBody, false); // false - plain text

            // Добавляем вложения, если они есть
            if (photos != null && !photos.isEmpty()) {
                int i = 1;
                for (byte[] photo : photos) {
                    ByteArrayResource resource = new ByteArrayResource(photo);
                    helper.addAttachment("photo_" + i + ".jpg", resource); // Укажи правильное имя и расширение
                    i++;
                }
            }
            sendAnswer("Успешно отправлено MailSenderServiceImpl", mailParams.getChatId());
        } catch (MessagingException e) {
            sendAnswer("Ошибка отправки (MessagingException): " + e.getMessage(), mailParams.getChatId());
        } catch (MailException e) {
            sendAnswer("Ошибка отправки (MailException): " + e.getMessage(), mailParams.getChatId());
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
