package pro.masterfood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.masterfood.controller.TelegramBot;

@Component
public class BotInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BotInitializer.class);
    @Autowired
    private TelegramBot telegramBot;
    @Override
    public void run(String... args) throws Exception {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error with initializing bot:  " + e.getMessage());
        }
    }
}