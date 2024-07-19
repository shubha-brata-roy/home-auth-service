package com.royhome.homeauthservice.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.application")
@Data
public class JWTConfiguration {
    private String name;
    private String secret;
    private Long jwtExpirationInMs;
}
