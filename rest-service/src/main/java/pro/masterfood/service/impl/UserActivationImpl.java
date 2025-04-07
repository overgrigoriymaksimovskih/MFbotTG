package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.masterfood.dao.AppUserDAO;
import pro.masterfood.service.UserActivatonService;
import pro.masterfood.utils.Decoder;
import pro.masterfood.utils.GeneratorRequestMethodPostForCheckUser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

@Component
public class UserActivationImpl implements UserActivatonService {
    private static final Logger log = LoggerFactory.getLogger(UserActivationImpl.class);
    private final GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser;
    private final AppUserDAO appUserDAO;
    private final Decoder decoder;

    public UserActivationImpl(GeneratorRequestMethodPostForCheckUser generatorRequestMethodPostForCheckUser, AppUserDAO appUserDAO, Decoder decoder) {
        this.generatorRequestMethodPostForCheckUser = generatorRequestMethodPostForCheckUser;
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = decoder.idOf(cryptoUserId);
        log.debug(String.format("User activation with user-id=%s", userId));
        if (userId == null) {
            return false;
        }

        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
    @Override
    public boolean activationMf(String email, String password) {
//        // 1. Создаем POST-запрос
//        HttpEntity<MultiValueMap<String, String>> request = generatorRequestMethodPostForCheckUser.buildPostRequest(action, email, password, check_num, token);
//        // 2. Отправляем POST-запрос
//        ResponseEntity<Boolean> response = sendPostRequest(request);
//        //3. Обрабатываем результат
//        if (response.getBody() != null && response.getBody()) {
//            return true;
//        } else {
//            return false;
//        }
        WebDriver driver = null;
        try {
            // 1. Настройка Selenium и ChromeDriver
            String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
            if (chromeDriverPath == null) {
                chromeDriverPath = "/usr/local/bin/chromedriver"; // Значение по умолчанию (если переменная окружения не установлена)
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--ignore-certificate-errors"); // Игнорируем ошибки сертификатов SSL/TLS
            options.addArguments("--headless"); // Запуск Chrome в headless режиме (без GUI)
            options.addArguments("--no-sandbox"); // Обязательно для Docker
            options.addArguments("--disable-dev-shm-usage");  // Рекомендуется для Docker
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});


            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            String loginPageUrl = "https://master-food.pro/private/";
            driver.get(loginPageUrl);

//            System.out.println("--driver.page success");

            // 3. Заполнение формы логина
            WebElement emailField = driver.findElement(By.name("email"));
            emailField.sendKeys(email);

            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.sendKeys(password);

            // 4. Получение check_num и token (после выполнения JavaScript)
            WebElement checkNumInput = driver.findElement(By.name("check_num"));
            String checkNum = checkNumInput.getAttribute("value");

            WebElement tokenInput = driver.findElement(By.name("token"));
            String token = tokenInput.getAttribute("value");

            // 5. Отправка формы
            WebElement submitButton = driver.findElement(By.className("btn-wrap"));
            submitButton.click();
            Thread.sleep(2000); // Даем время загрузиться следующей странице

            // 6. Получение HTML-кода после отправки формы (для анализа ответа)
            String pageSource = driver.getPageSource();

            // 7. Обработка ответа (анализ pageSource)
            if (pageSource.contains("Личный кабинет")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при авторизации: " + e.getMessage());
            return false;
        } finally {
            if (driver != null) {
                driver.quit(); // Закрываем браузер
            }
        }
    }
//    // Метод для отправки POST-запроса
//    private ResponseEntity<Boolean> sendPostRequest(HttpEntity<MultiValueMap<String, String>> request) {
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://81.200.158.74:8086/user/checkPostMf"; // URL checkPostMf
//        return restTemplate.postForEntity(url, request, Boolean.class);
//    }
}
