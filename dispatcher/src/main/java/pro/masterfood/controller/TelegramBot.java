package pro.masterfood.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private UpdateController updateController;

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    public TelegramBot (UpdateController updateController){
        this.updateController = updateController;
    }
    @PostConstruct
    public void init(){
        updateController.registerBot(this);
        setBotCommands();  // Установка команд
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage (SendMessage message){
        if (message != null){
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error with sending answer message to user:  " + message.getChatId() + " " + e.getMessage());
            }
        }
    }

    // Метод для установки команд
    public void setBotCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/present", "Накопления на подарок и бонусы"));
        listOfCommands.add(new BotCommand("/status", "Статус текущего заказа"));
        listOfCommands.add(new BotCommand("/report", "Отправить жалобу"));
        listOfCommands.add(new BotCommand("/cancel", "Отмена выполнения текущей команды"));
        listOfCommands.add(new BotCommand("/quit", "Выйти"));

        try {
            SetMyCommands setMyCommands = new SetMyCommands();
            setMyCommands.setCommands(listOfCommands);
            this.execute(setMyCommands);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при установке команд бота: " + e.getMessage());
        }
    }

}
