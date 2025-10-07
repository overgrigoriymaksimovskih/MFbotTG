package pro.masterfood.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/api/**").permitAll()  // Разрешить публичные: главная и API
                        .anyRequest().authenticated()  // Остальное требует аутентификации
                )
                .csrf(csrf -> csrf.disable())  // Отключить CSRF (для API от Telegram)
                .formLogin(formLogin -> formLogin.disable())  // Отключить форму логина
                .httpBasic(httpBasic -> httpBasic.disable());  // Отключить HTTP Basic Auth

        return http.build();
    }
}


