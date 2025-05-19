package pro.masterfood.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelpButton {

    public SendMessage getHelpMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
//        message.setText("Доступные команды:\n/help - Показать справку");

        // Создаем клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Подгоняем размер клавиатуры
        keyboardMarkup.setSelective(false);    // показывать клавиатуру всем, а не только тем, кто @упомянут
        keyboardMarkup.setIsPersistent(true);    // keyboard is always visible for the user

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        // Создаем кнопку "/help"
        KeyboardButton helpButton = new KeyboardButton();
        helpButton.setText("/help");
        row.add(helpButton);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

//        message.setParseMode("MarkdownV2");
        message.setDisableWebPagePreview(true); // Отключаем сниппеты

        return message;
    }
}

//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class HelpButton {
//
//    public SendMessage getHelpMessage(Long chatId) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Доступные команды:\n/help - Показать справку");  // Можно убрать, если используешь другой текст
//
//        // Создаем InlineKeyboardMarkup
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//        List<InlineKeyboardButton> rowInline = new ArrayList<>();
//
//        InlineKeyboardButton helpButton = new InlineKeyboardButton();
//        helpButton.setText("Помощь");
//        helpButton.setCallbackData("/help");
//
//        rowInline.add(helpButton);
//        rowsInline.add(rowInline);
//        markupInline.setKeyboard(rowsInline);
//        message.setReplyMarkup(markupInline);
//
//        return message;
//    }
//}
