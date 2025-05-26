package pro.masterfood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.masterfood.configuration.RabbitConfiguration;
import pro.masterfood.service.UpdateProducer;
import pro.masterfood.utils.MessageUtils;

import java.util.List;

@Component
public class UpdateController {
    private static final long MAX_MESSAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Logger log = LoggerFactory.getLogger(UpdateController.class);
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    private final RabbitConfiguration rabbitConfiguration;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer, RabbitConfiguration rabbitConfiguration){
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
        this.rabbitConfiguration = rabbitConfiguration;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received object is null");
            return;
        }
        if (update.hasMessage()){
            distributeMessageByType(update);
        }else{
            log.error("Unsupported message type received: " + update);
        }
    }

    private void distributeMessageByType (Update update){
        //TODO реализовать ограничение запросов в еденицу времени (redis или Token Bucket Algorithm...)
        Message message = update.getMessage();
        if (checkMessageSize(update)) {
            int contentCount = 0;

            // Проверяем все типы контента
            if (message.hasText()) {
                contentCount++;
            }
            if (message.hasDocument()) {
                contentCount++;
            }
            if (message.hasPhoto()) {
                contentCount++;
            }
            if (message.hasAudio()) {
                contentCount++;
            }
            if (message.hasVideo()) {
                contentCount++;
            }
            if (message.hasVoice()) {
                contentCount++;
            }
            if (message.hasSticker()) {
                contentCount++;
            }
            if (message.hasVideoNote()) {
                contentCount++;
            }
            if (message.hasContact()) {
                contentCount++;
            }
            if (message.hasLocation()) {
                contentCount++;
            }
            if (message.hasPoll()) {
                contentCount++;
            }

            if (contentCount == 1) {
                if(message.hasText()){
                    processTextMessage(update);
                }else if (message.hasPhoto()){
                    processPhotoMessage(update);
                }else{
                    setUnsupportedMessageTypeView(update);
                }
            }else {
                // Больше одного типа контента или ни одного
                setTooManyTypesOfContent(update);
            }


        } else {
            setTooBigMessageView(update);
        }
    }

    private void setTooBigMessageView(Update update){
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Размер сообщения не должен превышать 5мб.");
        setView(sendMessage);
    }

    private void setUnsupportedMessageTypeView(Update update){
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Не поддерживаемый тип сообщения" + "\n"
                        + "Допустимы только текст или фото");
        setView(sendMessage);
    }

    private void setTooManyTypesOfContent(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Пожалуйста отправляйте сообщения только с фото или только с текстом." + "\n"
                        +"Не отправляйте сообщения с текстом и фото одновременно." + "\n"
                        +"Такие сообщения не будут обработаны...");
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
        updateProducer.produce(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
        setFileIsReceivedView(update);
    }

    private void processTextMessage (Update update){
        updateProducer.produce(rabbitConfiguration.getTextMessageUpdateQueue(), update);
    }

    private boolean checkMessageSize(Update update){
        Message message = update.getMessage();
        long totalSize = 0; // Инициализируем общий размер сообщения

        // 1. Проверяем размер текста (если он есть)
        if (message.hasText()) {
            totalSize += message.getText().length(); // Количество символов (примерное представление размера)
        }

        // 2. Проверяем размер документа (если он есть)
        if (message.hasDocument()) {
            Document document = message.getDocument();
            totalSize += document.getFileSize(); // Получаем размер файла в байтах
        }

        // 3. Проверяем размер фотографии (если она есть)
        if (message.hasPhoto()) {
            // Photos представляют собой массив разных размеров, нужно выбрать самый большой
            List<PhotoSize> photos = message.getPhoto();
            long maxSize = 0;
            for (PhotoSize photo : photos) {
                maxSize = Math.max(maxSize, photo.getFileSize());
            }
            totalSize += maxSize;
        }

        // 4. Проверяем общий размер сообщения
        return totalSize <= MAX_MESSAGE_SIZE; // Сравниваем с максимальным допустимым размером
    }
}
