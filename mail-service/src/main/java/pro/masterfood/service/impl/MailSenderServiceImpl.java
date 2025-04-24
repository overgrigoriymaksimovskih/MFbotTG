package pro.masterfood.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.dto.MailParams;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.service.MailSenderService;
import pro.masterfood.service.ProducerService;

import pro.masterfood.dao.AppPhotoDAO;

import java.util.List;

@Component
public class MailSenderServiceImpl implements MailSenderService {
    private final AppPhotoDAO appPhotoDAO;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    private final ProducerService producerService;
    public MailSenderServiceImpl(AppPhotoDAO appPhotoDAO, JavaMailSender javaMailSender, ProducerService producerService) {
        this.javaMailSender = javaMailSender;
        this.producerService = producerService;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public void send(MailParams mailParams) {
        var subject = "Тестовое письмо из бота";
//        var messageBody = getActivationMailBody(mailParams.getId());
//        var emailTo = mailParams.getEmailTo();
        List<AppPhoto> appPhotoList = getAllPhotosByUserId(1L);
        StringBuilder sb = new StringBuilder();

        for (AppPhoto a : appPhotoList) {
            sb.append(a.getTelegramField() + "\n");
        }

        var messageBody = "Текст тестового письма из бота: \n"
                + sb.toString() + "\n"
                + mailParams.getAppUser().getUsername() + " "
                + mailParams.getAppUser().getLastName() + " "
                + mailParams.getAppUser().getFirstName() +"\n"
                + mailParams.getAppUser().getEmail() + "\n"
                + mailParams.getAppUser().getEmail() + "\n"
                + mailParams.getAppUser().getPhoneNumber() + "\n"
                + mailParams.getAppUser().getSiteUserId()
                ;
        var emailTo = "master-2m@yandex.ru";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);
        javaMailSender.send(mailMessage);
    }

    public List<AppPhoto> getAllPhotosByUserId(Long userId) {
        return appPhotoDAO.findByOwnerId(userId);
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
