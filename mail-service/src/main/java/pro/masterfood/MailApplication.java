package pro.masterfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan("pro.masterfood.*")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class})
public class MailApplication {
    public static void main(String[] args) {

        // Запуск Spring Boot
        SpringApplication.run(MailApplication.class);
    }
}
