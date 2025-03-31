package pro.masterfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//import io.github.cdimascio.dotenv.Dotenv;
@EnableJpaRepositories("pro.masterfood.*")
@EntityScan("pro.masterfood.*")
@ComponentScan("pro.masterfood.*")
@SpringBootApplication
public class NodeApplication {
    public static void main(String[] args) {
//        //установка переменных окружения нужна только чтобы запускать из идеи сервис у которого переменные заданы в .env
//        //можно все указать прямо в properties и тогда этот блок не нужен
//
//        // Загрузка переменных окружения из файла .env
//        Dotenv dotenv = Dotenv.configure()
//                .directory("./") // Укажите каталог, где находится .env (по умолчанию текущий каталог)
//                .ignoreIfMalformed() // Игнорировать строки с неверным синтаксисом в .env
//                .ignoreIfMissing() // Игнорировать, если файл .env отсутствует
//                .load();
//
//        // Установка переменных окружения как системных свойств
//        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
//        // установка переменных окружения завершена
//

        // Запуск Spring Boot
        SpringApplication.run(NodeApplication.class);
    }
}
