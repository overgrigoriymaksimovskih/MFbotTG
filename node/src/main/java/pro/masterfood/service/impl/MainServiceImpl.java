package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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
import pro.masterfood.utils.OneCmessageHandler;

import java.util.List;
import java.util.Map;

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
    private final OneCmessageHandler oneCmessageHandler;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService, OneCmessageHandler oneCmessageHandler) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
        this.oneCmessageHandler = oneCmessageHandler;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var chatId = update.getMessage().getChatId();

        var serviceCommand = ServiceCommand.fromValue(text);
        //Сюда сначала попадают все команды
        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);

            //----------------------------------------------------------------------------------------------------------
            //Все команды имеющие БАСИК_СТЕЙТ обрабатываются в процессСервисеКомманд
            //это основная обрабатывающая комманда которая обрабатывает все текстовые сообщения в том числе сервисные
            //по сути вообще все, которые не касаются активации пользователя
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(chatId, appUser, text);

            //----------------------------------------------------------------------------------------------------------
            //Все команды с состоянием ОЖИДАЕМ ЕМАЙЛ обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
            //Все команды с состоянием ОЖИДАЕМ ПАРОЛЬ обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_PASSWORD_STATE.equals(userState)) {
            output = appUserService.checkPassword(chatId, appUser, text);
            //Все команды с состоянием ОЖИДАЕМ ОТВЕТ возвращают ответ ОБОЖДИТЕ...
        } else if (WAIT_FOR_ANSWER.equals(userState)) {
            output = "Дождитесь выполнения команды... Если команда выполняется слишком долго - отмените ее...";
            //----------------------------------------------------------------------------------------------------------

            //Все команды с состоянием ОЖИДАЕМ СООБЩЕНИЕ ДЛЯ РЕПОРТА обрабатываются отдельной коммандой в АппЮзерСервис
        } else if (WAIT_FOR_REPORT_MESSAGE.equals(userState)) {
            output = appUserService.sendReportMail(chatId, appUser, text);
            //----------------------------------------------------------------------------------------------------------
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! введите /cancel и попробуйте снова...";
        }
        sendAnswer(output, chatId);
    }

    public void processDocMessage(String oneCmessage) {
        String message = oneCmessageHandler.getMessageText(oneCmessage);
        List<Long> listOfChatsIds = oneCmessageHandler.getChatsIds(oneCmessage);

        if (listOfChatsIds != null && message != null) {
            for (Long chatId : listOfChatsIds) {
                sendAnswer(message, chatId); // chatId - это уже telegramUserId
            }
        } else {
            log.error("Ошибка при обработке сообщения из 1С: listOfChatsIds is null or message is null");
        }
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
//            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено." ;//+ link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error("Произошла ошибка при загрузке фото", ex);
            String error = "Не удалось загрузить ФОТО... " + ex.getMessage();
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте "
                + "свою учетную запись для загрузки контента";
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
//        var message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }



//----------------------------------------------------------------------------------------------------------------------
    private String processServiceCommand(Long chatId, AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)){
            return appUserService.registerUser(appUser);

        } else if (GET_USER_INFO.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.checkBalance(chatId, appUser);

        } else if (STATUS.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.checkStatus(chatId, appUser);

        } else if (REPORT.equals(serviceCommand) && appUser.getIsActive()) {
            return appUserService.createReportMail(chatId, appUser);
        //--------------------------------------------------------------------------------------------------------------
        } else if (HELP.equals(serviceCommand) && !appUser.getIsActive()) {
            return help();
        } else if (HELP.equals(serviceCommand)) {
            return helpIsActive();
        //--------------------------------------------------------------------------------------------------------------
        } else if (START.equals(serviceCommand)) {
            return "Здравствуйте, чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда, чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String helpIsActive() {
        return "Список доступных команд: \n"
                + "/present - накоплено на подарок\n"
                + "/status - статус текущего заказа\n"
                + "/report - отправить жалобу\n"
                + "/cancel - отмена выполнения текущей команды\n"
                + "\n"
                + "/quit - выйти\n";
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды\n"
                + "/registration - регистрация пользователя\n";
    }

    private String cancelProcess(AppUser appUser) {
        if(WAIT_FOR_PASSWORD_STATE.equals(appUser.getState())
        || WAIT_FOR_ANSWER.equals(appUser.getState())){
            appUser.setEmail(null);
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
