package pro.masterfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
//import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class}) пока не работали с БД так было

@EnableJpaRepositories("pro.masterfood.*")
@EntityScan("pro.masterfood.*")
@ComponentScan("pro.masterfood.*")
@SpringBootApplication
public class MailApplication {
    public static void main(String[] args) {

        // Запуск Spring Boot
        SpringApplication.run(MailApplication.class);
    }
}
