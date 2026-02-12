package com.hyomyang.oauth2login.oauth2.config;


import com.hyomyang.oauth2login.oauth2.config.properties.JwtProperties;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.time.Clock;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AppConfig {

    @Bean
    public SecretKey jwtSecretKey(JwtProperties jwtProps) {
        return Keys.hmacShaKeyFor(jwtProps.secretKeyBytes());
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
