package pro.masterfood.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebDriverPool {

    private final BlockingQueue<WebDriver> pool;
    private final int maxSize;

    public WebDriverPool(int maxSize) {
        this.maxSize = maxSize;
        this.pool = new LinkedBlockingQueue<>(maxSize);

        // Инициализация пула
        for (int i = 0; i < maxSize; i++) {
            pool.add(createWebDriver());
        }
    }

    private WebDriver createWebDriver() {
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
        options.addArguments("--disable-dev-shm-usage"); // Предотвращает использование /dev/shm (может вызывать проблемы в Docker)
        options.addArguments("--disable-gpu"); // Отключаем GPU (графический процессор), что полезно для сред без графического интерфейса.
        options.addArguments("--window-size=1920,1080");
        // Отключаем логирование
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});

        // Добавляем capabilities
        //DesiredCapabilities capabilities = new DesiredCapabilities();
        //capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        //options.merge(capabilities);

        //options.setBinary("/usr/bin/google-chrome");

        return new ChromeDriver(options);
    }

    public WebDriver borrowObject() throws InterruptedException {
        return pool.take(); // Блокируется, если пул пуст
    }

    public void returnObject(WebDriver driver) {
        if (driver != null) {
            pool.offer(driver); // Не блокируется, если пул полон
        }
    }

    public void closeAll() {
        for (WebDriver driver : pool) {
            try {
                driver.quit(); // Закрываем браузер
            } catch (Exception e) {
                // Обрабатываем исключения при закрытии браузера
                System.err.println("Error closing WebDriver: " + e.getMessage());
            }
        }
        pool.clear(); // Очищаем пул
    }
}
