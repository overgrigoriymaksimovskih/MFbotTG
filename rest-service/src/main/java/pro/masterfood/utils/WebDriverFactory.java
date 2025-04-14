package pro.masterfood.utils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverFactory extends BasePooledObjectFactory<WebDriver> {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    @Override
    public WebDriver create() throws Exception {
        logger.debug("Creating new WebDriver instance.");
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

        return new ChromeDriver(options);
    }

    @Override
    public PooledObject<WebDriver> wrap(WebDriver driver) {
        return new DefaultPooledObject<>(driver);
    }

    @Override
    public void destroyObject(PooledObject<WebDriver> p) throws Exception {
        logger.debug("Destroying WebDriver instance.");
        WebDriver driver = p.getObject();
        try {
            driver.quit();
        } catch (Exception e) {
            logger.error("Error closing WebDriver: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean validateObject(PooledObject<WebDriver> p) {
        WebDriver driver = p.getObject();
        try {
            // Проверка, что WebDriver еще жив.  Это простой пример, вы можете добавить более сложные проверки.
            driver.getTitle();
            return true;
        } catch (Exception e) {
            logger.warn("WebDriver instance is no longer valid: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<WebDriver> p) throws Exception {
        logger.debug("Activating WebDriver instance.");
        WebDriver driver = p.getObject();
        // Здесь можно выполнить действия по настройке WebDriver перед использованием
        // Например, очистка cookies:
        // driver.manage().deleteAllCookies();
    }

    @Override
    public void passivateObject(PooledObject<WebDriver> p) throws Exception {
        logger.debug("Passivating WebDriver instance.");
        WebDriver driver = p.getObject();
        // Здесь можно выполнить действия по "очистке" WebDriver после использования
        // Например, переход на пустую страницу:
        // driver.get("about:blank");
    }
}
