package pro.masterfood;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pro.masterfood.utils.WebDriverPool;

@EnableJpaRepositories("pro.masterfood.*")
@EntityScan("pro.masterfood.*")
@ComponentScan("pro.masterfood.*")
@SpringBootApplication
public class RestService {
    private static WebDriverPool webDriverPool;
    public static void main(String[] args) {

        // Запуск Spring Boot
        SpringApplication.run(RestService.class);
        // Создаем пул WebDriver'ов при запуске приложения
        webDriverPool = new WebDriverPool(10); // 10 - это максимальный размер пула
    }
    @PreDestroy
    public void onDestroy() {
        // Закрываем все WebDriver'ы при завершении работы приложения
        webDriverPool.closeAll();
    }

    public static WebDriverPool getWebDriverPool() {
        return webDriverPool;
    }
}
