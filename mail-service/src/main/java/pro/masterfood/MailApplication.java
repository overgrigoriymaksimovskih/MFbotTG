package pro.masterfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class})
public class MailApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class);
    }
}
