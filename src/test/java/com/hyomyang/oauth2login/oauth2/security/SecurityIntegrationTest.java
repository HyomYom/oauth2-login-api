package com.hyomyang.oauth2login.oauth2.security;


import com.hyomyang.oauth2login.oauth2.logger.TestLogger;
import com.hyomyang.oauth2login.oauth2.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(TestLogger.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtTokenProvider tokenProvider;

    @Test
    void noToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/secure/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
        ;
    }


    @Test
    void accessToken_shouldReturn200() throws Exception {
        Set<String> roles = new HashSet<>(List.of("ROLE_ADMIN"));
        String accessToken = tokenProvider.createAccessToken(1L, roles);
        mockMvc.perform(get("/api/secure/admin/ping")
                        .header("Authorization","Bearer " + accessToken ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("admin-pong:1"));

    }

    @Test
    void accessToken_shouldReturn401() throws Exception {

        Set<String> roles = new HashSet<>(List.of("ROLE_USER"));
        String accessToken = tokenProvider.createAccessToken(1L, roles);
        mockMvc.perform(get("/api/secure/admin/ping")
                        .header("Authorization","Bearer " + accessToken ))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    void refreshToken_shouldReturn401() throws Exception {
        String refreshToken = tokenProvider.createRefreshToken(1L);

        mockMvc.perform(get("/api/secure/ping")
                .header("Authorization","Bearer " + refreshToken ))
                .andExpect(status().isUnauthorized());
    }
}
