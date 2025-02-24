package pro.masterfood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.service.UpdateProducer;
import pro.masterfood.utils.MessageUtils;

import static pro.masterfood.model.RabbitQueue.*;

@Component
public class UpdateController {
    private static final Logger log = LoggerFactory.getLogger(UpdateController.class);
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received object is null");
            return;
        }
        if (update != null){
            distributeMessageByType(update);
        }else{
            log.error("Unsupported message type received: " + update);
        }
    }

    private void distributeMessageByType (Update update){
        Message message = update.getMessage();
        if(message.hasText()){
            processTextMessage(update);
        }else if (message.hasDocument()){
            processDocMessage(update);
        }else if (message.hasPhoto()){
            processPhotoMessage(update);
        }else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update){
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Не поддерживаемый тип сообщения");
        setView(sendMessage);
    }
    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен и обрабатывается ...");
        setView(sendMessage);
    }

    public void setView (SendMessage sendMessage){
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage (Update update){
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage (Update update){
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
    private void processTextMessage (Update update){
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
