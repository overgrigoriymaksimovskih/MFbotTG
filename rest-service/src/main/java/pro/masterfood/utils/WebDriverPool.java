package pro.masterfood.utils;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebDriverPool implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverPool.class);

    private final ObjectPool<WebDriver> objectPool;

    public WebDriverPool(@Value("${webdriver.pool.size:5}") int poolSize) {
        WebDriverFactory factory = new WebDriverFactory();
        this.objectPool = new GenericObjectPool<>(factory);
        ((GenericObjectPool<WebDriver>) this.objectPool).setMaxTotal(poolSize);
        logger.info("Initializing WebDriverPool with size: {}", poolSize);
    }

    public WebDriver borrowObject() throws Exception {
        logger.debug("Borrowing WebDriver from pool.");
        return objectPool.borrowObject();
    }

    public void returnObject(WebDriver driver) {
        try {
            objectPool.returnObject(driver);
            logger.debug("Returning WebDriver to pool.");
        } catch (Exception e) {
            logger.error("Error returning WebDriver to pool: {}", e.getMessage(), e);
            // Если не удалось вернуть объект в пул, уничтожаем его
            try {
                objectPool.invalidateObject(driver);
            } catch (Exception ex) {
                logger.error("Error invalidating WebDriver: {}", ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Closing WebDriverPool.");
        objectPool.close();
    }
}
