package pro.masterfood.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.hashids.Hashids;

@Configuration
public class RestServiceConfiguration {

    @Value("${salt}")
    private String salt;

    @Bean
    public Hashids getHashids() {
        var minHashLength = 10;
        return new Hashids(salt, minHashLength);
    }
}
