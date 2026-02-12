package com.hyomyang.oauth2login.oauth2.security;


import com.hyomyang.oauth2login.oauth2.logger.TestLogger;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(TestLogger.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class JwtErrorMappingIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired @Qualifier("jwtSecretKey")
    SecretKey key;

    @Test
    void expiredToken_shouldRetrun401_TOKEN_EXPIRED() throws Exception {
        String expiredToken = Jwts.builder()
                .subject("1")
                .claim("type", "access")
                .claim("roles", List.of("ROLE_USER"))
                .issuedAt(Date.from(Instant.now().minusSeconds(3600)))
                .expiration(Date.from(Instant.now().minusSeconds(100))) // 이미 만료
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        mockMvc.perform(get("/api/secure/ping")
                .header("Authorization","Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").value("TOKEN_EXPIRED"));

    }

    @Test
    void invalidSignature_shouldReturn401_TOKEN_INVALID_SIGNITURE() throws Exception {
        SecretKey otherKey = Jwts.SIG.HS256.key().build();

        String badToken = Jwts.builder()
                .subject("1")
                .claim("type", "access")
                .claim("roles", List.of("ROLE_USER","ROLE_ADMIN"))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .id(UUID.randomUUID().toString())
                .signWith(otherKey, Jwts.SIG.HS256)
                .compact();

        String normal = Jwts.builder()
                .subject("1")
                .claim("type", "access")
                .claim("roles", List.of("ROLE_USER","ROLE_ADMIN"))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        mockMvc.perform(get("/api/secure/admin/ping")
                        .header("Authorization", "Bearer " + normal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("admin-pong:1"));
    }

}
