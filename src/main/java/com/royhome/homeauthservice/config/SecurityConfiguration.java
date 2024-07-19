package com.royhome.homeauthservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> {
                                requests
                                        .requestMatchers("/users/**", "/login.html", "/registration.html").permitAll()
                                        .anyRequest().permitAll();
                                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/**"
                ));

        return http.build();
    }
}

