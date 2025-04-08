package pro.masterfood.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class GeneratorRequestMethodPostForCheckUser {
    // Метод для создания POST-запроса
    public HttpEntity<MultiValueMap<String, String>> buildPostRequest(String email,
                                                                      String password) {
        String action = "login";
        String check_num = null;
        String token = null;

        WebDriver driver = null;
        HttpHeaders headers = new HttpHeaders();

        try {
            // 1. Настройка Selenium и ChromeDriver
            String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
            if (chromeDriverPath == null) {
                chromeDriverPath = "/usr/local/bin/chromedriver"; // Значение по умолчанию (если переменная окружения не установлена)
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);


            ChromeOptions options = new ChromeOptions();
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
            options.addArguments("--ignore-certificate-errors"); // Игнорируем ошибки сертификатов SSL/TLS
            options.addArguments("--headless"); // Запуск Chrome в headless режиме (без GUI)
            options.addArguments("--enable-javascript");
            options.addArguments("--no-sandbox"); // Обязательно для Docker
            options.addArguments("--disable-dev-shm-usage");  // Рекомендуется для Docker
            options.setBinary("/usr/local/bin/chrome-headless-shell"); // Укажите ПРАВИЛЬНЫЙ путь!
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setCapability("strictFileInteractability", false);

            driver = new ChromeDriver(options); // Инициализация driver ЗДЕСЬ
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            String loginPageUrl = "https://master-food.pro/private/";
            driver.manage().window().setSize(new Dimension(1920, 1080));
            driver.get(loginPageUrl);

            // Получение check_num и token (после выполнения JavaScript)
            WebElement checkNumInput = driver.findElement(By.name("check_num"));
            check_num = checkNumInput.getAttribute("value");

            WebElement tokenInput = driver.findElement(By.name("token"));
            token = tokenInput.getAttribute("value");


        } catch (Exception e) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("action", "Exception with get token...");
            map.add("email", email);
            map.add("password", password);
            map.add("check_num", check_num);
            map.add("token", token);
            return new HttpEntity<>(map, headers);

        } finally {
            if (driver != null) {
                driver.quit(); // Закрываем браузер
            }
        }

        //--------------------------------------------------------------------------------------------------------------
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("action", action);
        map.add("email", email);
        map.add("password", password);
        map.add("check_num", check_num);
        map.add("token", token);
        //--//

        return new HttpEntity<>(map, headers);
    }
}
