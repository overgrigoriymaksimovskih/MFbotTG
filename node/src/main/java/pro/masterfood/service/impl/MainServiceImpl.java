package pro.masterfood.service.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.dao.RawDataDAO;
import pro.masterfood.entity.AppUser;
import pro.masterfood.entity.RawData;
import pro.masterfood.exceptions.UploadFileException;
import pro.masterfood.service.AppUserService;
import pro.masterfood.service.FileService;
import pro.masterfood.service.MainService;
import pro.masterfood.service.ProducerService;
import pro.masterfood.service.enums.ServiceCommand;
import pro.masterfood.utils.HelpOrShareContactButton;
import pro.masterfood.utils.OneCmessageHandler;
import pro.masterfood.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static pro.masterfood.enums.UserState.*;
import static pro.masterfood.service.enums.ServiceCommand.*;

@Component
public class MainServiceImpl implements MainService {
    private static final Logger log = LoggerFactory.getLogger(MainServiceImpl.class);
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final OfferService offerService;
    private final OneCmessageHandler oneCmessageHandler;
    private final HelpOrShareContactButton helpOrShareContactButton;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService, OfferService offerService, OneCmessageHandler oneCmessageHandler, HelpOrShareContactButton helpOrShareContactButton) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
        this.offerService = offerService;
        this.oneCmessageHandler = oneCmessageHandler;
        this.helpOrShareContactButton = helpOrShareContactButton;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var chatId = update.getMessage().getChatId();

        String phoneNumber = null;
        if (update.getMessage().hasContact()) {
            // Пользователь поделился контактом
            Contact contact = update.getMessage().getContact();
            phoneNumber = contact.getPhoneNumber();
        }

        var serviceCommand = ServiceCommand.fromValue(text);
        //Сюда сначала попадают все команды
        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);

            //----------------------------------------------------------------------------------------------------------
            //Все команды имеющие БАСИК_СТЕЙТ обрабатываются в процессСервисеКомманд
            //это основная обрабатывающая комманда которая обрабатывает все текстовые сообщения в том числе сервисные
            //по сути вообще все, которые не касаются активации пользователя и отправки письма
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(chatId, appUser, text);

            //----------------------------------------------------------------------------------------------------------
            //Все команды с состоянием ОЖИДАЕМ ЕМАЙЛ обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);

            //Все команды с состоянием ОЖИДАЕМ ПАРОЛЬ обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_PASSWORD_STATE.equals(userState)) {
            output = appUserService.checkPassword(chatId, appUser, text);

            //Все команды с состоянием ОЖИДАЕМ ВВОД НОМЕРА ТЕЛЕФОНА ВРУЧНУЮ обрабатываются
            // отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_PHONE_MANUAL_INPUT_STATE.equals(userState)) {
            output = appUserService.checkPhone(chatId, appUser, text);

            //Все команды с состоянием ОЖИДАЕМ СООБЩЕНИЕ ДЕЛЕЛЕНИЯ КОНТАКТОМ обрабатываются
            // отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_PHONE_SHARE_STATE.equals(userState)) {
            output = appUserService.checkPhone(chatId, appUser, phoneNumber);

            //Все команды с состоянием ОЖИДАЕМ ВВОД SMS обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_SMS_STATE.equals(userState)) {
            output = appUserService.checkSMS(chatId, appUser, text);





            //Все команды с состоянием ОЖИДАЕМ ОТВЕТ возвращают ответ ОБОЖДИТЕ...
        } else if (WAIT_FOR_ANSWER.equals(userState)) {
            output = "Дождитесь выполнения команды... Если команда выполняется слишком долго - отмените ее... \n/cancel";
            //----------------------------------------------------------------------------------------------------------

            //Все команды с состоянием ОЖИДАЕМ СООБЩЕНИЕ ДЛЯ РЕПОРТА обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_REPORT_MESSAGE.equals(userState)) {
            output = appUserService.sendReportMail(chatId, appUser, text);
            //----------------------------------------------------------------------------------------------------------
        } else {
            log.error("Unknown user state: " + userState);
            output = "Ошибка! Завершите текущую операцию, либо введите /cancel и попробуйте снова...";
        }
        sendAnswer(output, chatId);
    }

    public void processDocMessage(String oneCmessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("Принято методом обработки очереди из 1С \n");

        String message = oneCmessageHandler.getMessageText(oneCmessage, sb);
        List<Long> listOfChatsIds = oneCmessageHandler.getChatsIds(oneCmessage, sb);

        if (listOfChatsIds != null && message != null) {
            int usersCount = 0;
            for (Long chatId : listOfChatsIds) {
                sendAnswer(message, chatId); // chatId - это уже telegramUserId
                usersCount++;
            }
            sb.append("сообщение: \"" + message + "\"" + " отправлено: " + usersCount + " пользователям");
        } else {
            log.error("Ошибка при обработке сообщения из 1С: listOfChatsIds is null or message is null");
            sb.append(", список id пользователей либо поле \"message\" = null");
        }
        producerService.producerAnswerTo1C(sb.toString());
    }

    @Override
    public void processPhotoMessage(Update update) {
        String message = update.getMessage().getCaption();
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            fileService.processPhoto(update.getMessage(), appUser, message);
            var answer = "Фото успешно загружено." ;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error("Process photo in MainServiceImpl was failed ", ex);
            String error = "Не удалось загрузить ФОТО... " + ex.getMessage();
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Активируйте "
                + "свою учетную запись";
            sendAnswer(error, chatId);
            return true;
        } else if (!WAIT_FOR_REPORT_MESSAGE.equals(userState)) {
            var error = "Чтобы отправить фото воспользуйтесь командой /report (Обратная связь)";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }



    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage;
//        if(output.equals("ВМЕСТО ЭТОГО СООБЩЕНИЯ HelpOrShareContactButton ОТПРАВИТ КНОПКУ ДЕЛЕНИЯ КОНТАКТОМ")){
//            sendMessage = helpOrShareContactButton.getHelpOrShareContactMessage(chatId, true);
//        }else{
//            sendMessage = helpOrShareContactButton.getHelpOrShareContactMessage(chatId);
//            sendMessage.setText(output);
//        }

        System.out.println("Comparing output string: " + output); // Логируем значение output

        if (output.equals("ВМЕСТО ЭТОГО СООБЩЕНИЯ HelpOrShareContactButton ОТПРАВИТ КНОПКУ ДЕЛЕНИЯ КОНТАКТОМ")) {
            System.out.println("Strings match! Sending share contact button.");
            sendMessage = helpOrShareContactButton.getHelpOrShareContactMessage(chatId, true);

            // Проверка, что в sendMessage действительно содержится ReplyMarkup с кнопкой "Поделиться контактом" (для отладки)
            if (sendMessage.getReplyMarkup() != null && sendMessage.getReplyMarkup() instanceof ReplyKeyboardMarkup) {
                ReplyKeyboardMarkup markup = (ReplyKeyboardMarkup) sendMessage.getReplyMarkup();
                if (markup.getKeyboard() != null && !markup.getKeyboard().isEmpty()) {
                    for (KeyboardRow row : markup.getKeyboard()) {
                        for (KeyboardButton button : row) {
                            if (button.getRequestContact()) {
                                System.out.println("The message contains a share contact button.");
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Keyboard is empty!");
                }
            } else {
                System.out.println("ReplyMarkup is null or not a ReplyKeyboardMarkup.");
            }


        } else {
            System.out.println("Strings do not match. Sending default message.");
            sendMessage = helpOrShareContactButton.getHelpOrShareContactMessage(chatId);
            sendMessage.setText(output);
        }

        producerService.producerAnswer(sendMessage);

    }

    private void sendHelpButton(Long chatId) {
        SendMessage sendMessage = helpOrShareContactButton.getHelpOrShareContactMessage(chatId);
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }



//----------------------------------------------------------------------------------------------------------------------
    private String processServiceCommand(Long chatId, AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        //Варианты авторизации
        //выбираем тип авторизации
        if (REGISTRATION.equals(serviceCommand)){
            return appUserService.chooseLoginType(appUser);
        //если тип по логину паролю
        } else if (REGISTRATIONEMAIL.equals(serviceCommand)) {
            return appUserService.loginByPassword(appUser);
        //если тип по телефону но еще не выбрано как именно
        } else if (REGISTRATIONPHONE.equals(serviceCommand)) {
            return appUserService.loginByPhone(appUser);
        //если тип по телефону Ручной ввод
        } else if (REGISTRATIONPHONEINPUT.equals(serviceCommand)) {
            return appUserService.loginByPhoneManualInput(appUser);
        //если тип по телефону Поделиться контактом
        } else if (REGISTRATIONPHONESHARE.equals(serviceCommand)) {
            return appUserService.loginByPhoneShare(appUser);
        //-------------------------------------------------------------------------

        } else if (GET_USER_INFO.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.checkBalance(chatId, appUser);

        } else if (STATUS.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.checkStatus(chatId, appUser);

        } else if (REPORT.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.createReportMail(chatId, appUser);

        } else if (QUIT.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.quit(chatId, appUser);
        } else if (EXIT.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.exit(chatId, appUser);
        //--------------------------------------------------------------------------------------------------------------
        } else if (HELP.equals(serviceCommand) && !appUser.getIsActive()) {
            return help();
        } else if (HELP.equals(serviceCommand)) {
            return helpIsActive();
        //--------------------------------------------------------------------------------------------------------------
        } else if (START.equals(serviceCommand)) {
            return "Здравствуйте. " +
                    "\n" +
                    "Для использования бота Вы должны быть  " +
                    "зарегистрированы на сайте master-food.pro " +
                    "если Вы еще не зарегистрированы, " +
                    "пройдите регистрацию: https://m.master-food.pro/private/register_new/ " +
                    "\n" +
                    "\n" +
                    "--> Авторизоваться в боте /login";
        } else {
//            return "Неизвестная команда, чтобы посмотреть список доступных команд введите /help";
            String offerDetails = offerService.handleTextMessage(cmd, appUser.getIsActive());
            return offerDetails;
        }
    }

    private String helpIsActive() {
        return "Список доступных команд: \n\n"
                + "Накопления на подарок и бонусы: /present\n"
                + "Статус текущего заказа: /status\n"
                + "Отправить жалобу: /report\n"
                + "Отмена выполнения текущей команды: /cancel\n"
                + "\n"
                + "Выйти: /quit";
    }

    private String help() {
        return "Список доступных команд: \n\n"
                + "/login - Авторизоваться в боте\n"
                + "/cancel - Отмена выполнения текущей команды\n";
    }

    private String cancelProcess(AppUser appUser) {
        if(WAIT_FOR_PASSWORD_STATE.equals(appUser.getState())
                || WAIT_FOR_ANSWER.equals(appUser.getState())){
            appUser.setEmail(null);
        }

        if(WAIT_FOR_PHONE_MANUAL_INPUT_STATE.equals(appUser.getState())){
            appUser.setPhoneNumber(null);
        }

        if(WAIT_FOR_SMS_STATE.equals(appUser.getState())){
            appUser.setSiteUserId(null);
            appUser.setSmsCode(null);
            appUser.setPhoneNumber(null);
        }

        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (optional.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return optional.get();
    }
    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
