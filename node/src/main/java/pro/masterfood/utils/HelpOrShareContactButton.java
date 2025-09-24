package pro.masterfood.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelpOrShareContactButton {
    // Перегруженный метод для простого вызова кнопки help без кнопки shared contact:
    public SendMessage getHelpOrShareContactMessage(Long chatId) {
        return getHelpOrShareContactMessage(chatId, false);
    }

    // Метод для вызова кнопки shared contact:
    public SendMessage getHelpOrShareContactMessage(Long chatId, boolean NeedToShareContactButton) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        // Создаем клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);   // Подгоняем размер клавиатуры
        keyboardMarkup.setSelective(false);  // Показываем клавиатуру всем
        keyboardMarkup.setIsPersistent(true);   // Клавиатура всегда видима

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        // Если требуется кнопка "/help"
        if (!NeedToShareContactButton) {
            KeyboardRow firstRow = new KeyboardRow();
            KeyboardButton helpButton = new KeyboardButton();
            helpButton.setText("/helpa");
            firstRow.add(helpButton);
            keyboardRows.add(firstRow);
        }

        // Если требуется кнопка "Поделиться контактом"
        if (NeedToShareContactButton) {
            KeyboardRow secondRow = new KeyboardRow();
            KeyboardButton shareContactButton = new KeyboardButton();
            shareContactButton.setText("Поделиться контактом");
            shareContactButton.setRequestContact(true);
            secondRow.add(shareContactButton);
            keyboardRows.add(secondRow);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        message.setDisableWebPagePreview(true); // Отключаем сниппеты

        return message;
    }
}
