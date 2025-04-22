package pro.masterfood.utils;

import org.openqa.selenium.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class SiteData {

    // Метод для создания web-драйвера
    public WebDriver createWebDriver(){
        WebDriver driver = null;



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

        return driver;
    }

    // Метод для настройки web-драйвера страницей
    public WebDriver setWebDriver (WebDriver driver, String pageUrl){
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
//        String loginPageUrl = "https://master-food.pro/private/";
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.get(pageUrl);

        return driver;
    }

    // Метод для создания POST-запроса
    public HttpEntity<MultiValueMap<String, String>> buildPostRequest(WebDriver driver,
                                                                      String email,
                                                                      String password) {
//        WebDriver driver = null;
        HttpHeaders headers = null;

        String parsedCheckNum = null;
        String parsedToken = null;

        try {
//            // 1. Настройка Selenium и ChromeDriver
//            String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
//            if (chromeDriverPath == null) {
//                chromeDriverPath = "/usr/local/bin/chromedriver"; // Значение по умолчанию (если переменная окружения не установлена)
//            }
//            System.setProperty("webdriver.chrome.driver", chromeDriverPath);


//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
//            options.addArguments("--ignore-certificate-errors"); // Игнорируем ошибки сертификатов SSL/TLS
//            options.addArguments("--headless"); // Запуск Chrome в headless режиме (без GUI)
//            options.addArguments("--enable-javascript");
//            options.addArguments("--no-sandbox"); // Обязательно для Docker
//            options.addArguments("--disable-dev-shm-usage");  // Рекомендуется для Docker
//            options.setBinary("/usr/local/bin/chrome-headless-shell"); // Укажите ПРАВИЛЬНЫЙ путь!
//            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
//            options.setCapability("strictFileInteractability", false);
//
//            driver = new ChromeDriver(options); // Инициализация driver ЗДЕСЬ
//            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
//            String loginPageUrl = "https://master-food.pro/private/";
//            driver.manage().window().setSize(new Dimension(1920, 1080));
//            driver.get(loginPageUrl);

            // Find the CSRF token (adjust the selector if needed)
            WebElement tokenElement = driver.findElement(By.cssSelector("meta[name='csrf-token']"));  // Example: <meta name="csrf-token" content="YOUR_TOKEN">
            String csrfToken = tokenElement.getAttribute("content");

            // Execute JavaScript to get check_num and token
            JavascriptExecutor js = (JavascriptExecutor) driver;
            parsedCheckNum = (String) js.executeScript("return document.querySelector('input[name=\"check_num\"]').value;");
            parsedToken = (String) js.executeScript("return document.querySelector('input[name=\"token\"]').value;");

            // Get all cookies
            Set<Cookie> cookies = driver.manage().getCookies();

            // Convert cookies to a string
            StringBuilder cookieString = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookieString.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
            }

            // 2. Set headers, including CSRF token and cookies
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
            headers.add("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.add("Origin", "https://master-food.pro");
            headers.add("Referer", "https://master-food.pro/private/");
            headers.add("X-Requested-With", "XMLHttpRequest");
            headers.add("x-csrf-token", csrfToken); // Use the token from Selenium
            headers.add("http_x_requested_with", "XMLHttpRequest");
            headers.add("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"");
            headers.add("sec-ch-ua-mobile", "?0");
            headers.add("sec-ch-ua-platform", "\"Windows\"");
            headers.add("Cookie", String.valueOf(cookieString)); // Use cookies from Selenium


        } catch (Exception e) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("action", "Error: " + e.getMessage());
            return new HttpEntity<>(map, headers);
        }
//        } finally {
//            if (driver != null) {
//                driver.quit(); // Закрываем браузер
//            }
//
//        }


        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("action", "login");
        map.add("email", email);
        map.add("password", password);
        map.add("check_num", parsedCheckNum);
        map.add("token", parsedToken);

        return new HttpEntity<>(map, headers);
    }
}
