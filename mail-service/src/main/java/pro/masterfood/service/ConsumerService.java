package pro.masterfood.service;

import pro.masterfood.dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail(MailParams mailParams);
}
