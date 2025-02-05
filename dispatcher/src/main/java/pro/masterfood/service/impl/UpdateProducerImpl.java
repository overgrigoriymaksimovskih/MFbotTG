package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.UpdateProducer;

@Component
public class UpdateProducerImpl implements UpdateProducer {
    private static final Logger log = LoggerFactory.getLogger(UpdateProducerImpl.class);
    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
    }
}
