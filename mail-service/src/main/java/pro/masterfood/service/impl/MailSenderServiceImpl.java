package pro.masterfood.service.impl;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import pro.masterfood.dto.MailParams;
import pro.masterfood.service.MailSenderService;
import pro.masterfood.service.ProducerService;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import pro.masterfood.utils.FileTypeDetector;

import java.io.IOException;
import java.util.List;


@Component
public class MailSenderServiceImpl  implements MailSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    private final JavaMailSender javaMailSender;
    private final ProducerService producerService;
    private final FileTypeDetector fileTypeDetector;

    public MailSenderServiceImpl(JavaMailSender javaMailSender, ProducerService producerService, FileTypeDetector fileTypeDetector) {
        this.javaMailSender = javaMailSender;
        this.producerService = producerService;
        this.fileTypeDetector = fileTypeDetector;
    }

//    public void sendSimpleEmail(String to, String subject, String text, MailParams mailParams) {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8"); // Укажем кодировку UTF-8
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, false); // false означает, что это plain text
//
//            javaMailSender.send(message);
//            sendAnswer("Успешно отправлено MailSenderServiceImpl", mailParams.getChatId());
//        } catch (MessagingException e) {
//            sendAnswer("Ошибка при отправке письма (MessagingException): " + e.getMessage(), mailParams.getChatId());
//        } catch (MailException e) {
//            sendAnswer("Ошибка при отправке письма (MailException): " + e.getMessage(), mailParams.getChatId());
//        }
//    }


    @Override
    public void send(MailParams mailParams) {
        sendAnswer("Успешно получено в метод send - MailSenderServiceImpl", mailParams.getChatId());
        var subject = "Тестовое письмо из бота (с вложениями)";
        var messageBody = "Текст тестового письма из бота: \n"
                + "От: "
                + mailParams.getEmail() + "\n"
                + "Телефон:  "
                + mailParams.getPhoneNumber() + "\n"
                + "ИД сайта:  "
                + mailParams.getSiteUid() + "\n"
                + "Текст сообщения:  " + "\n"
                + mailParams.getMessage() + "\n"
                + "\n"
                + "-------------------------------" + "\n"
                + "Ссылка для подтверждения получения :  "+ "\n"
                + "81.200.158.74" + "/api" + "/?id" + "=" + mailParams.getId();

        var emailTo = "master-2m@yandex.ru";
        List<byte[]> photos = mailParams.getPhotos();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8"); // true - multipart, кодировка UTF-8

            helper.setFrom(emailFrom); // Используй emailFrom из настроек
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(messageBody, false); // false - plain text

            int attachments = 0;
            if (photos != null && !photos.isEmpty()) {
                int i = 1;
                for (byte[] photo : photos) {
                    attachments++;
                    ByteArrayResource resource = new ByteArrayResource(photo);
                    String fileExtension = fileTypeDetector.detectFileType(photo);
                    String fileName = "photo_" + i + "." + fileExtension;
                    helper.addAttachment(fileName, resource);
                    i++;
                }
            }
            javaMailSender.send(message);
            if(attachments == 0){
                sendAnswer("Успешно отправлено MailSenderServiceImpl (без фото)", mailParams.getChatId());
            }else{
                sendAnswer("Успешно отправлено MailSenderServiceImpl (с " + attachments +" фото)", mailParams.getChatId());
            }



        } catch (MessagingException e) {
            sendAnswer("Ошибка отправки (MessagingException): " + e.getMessage(), mailParams.getChatId());
        } catch (MailException e) {
            sendAnswer("Ошибка отправки (MailException): " + e.getMessage(), mailParams.getChatId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

}
