package pro.masterfood;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.SpringApplication;

@EnableJpaRepositories("pro.masterfood.*")
@EntityScan("pro.masterfood.*")
@ComponentScan("pro.masterfood.*")
@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        SpringApplication.run(RestService.class);
    }
}
