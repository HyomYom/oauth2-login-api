package com.hyomyang.springaiboot.ai.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyomyang.springaiboot.ai.domain.User;
import com.hyomyang.springaiboot.ai.logger.TestLogger;
import com.hyomyang.springaiboot.ai.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(TestLogger.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthFlowIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    private Long userId;

    @BeforeEach
    void setUp() {

        User user = userRepository.findById(1L)
                .orElseGet(() -> userRepository.save(new User("pagooo@naver.com", "test", "ROLE_USER")));

        userId = user.getId();
    }

    @Test
    void login_shouldIssueAccessAndRefresh() throws Exception {
        Map<String, String> req = Map.of("username", "test", "password", "1234");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }


    @Test
    void refresh_rotation_shouldRejectReuse() throws Exception {
        // 1) login
        Map<String, Object> req = Map.of("id", userId, "role", List.of("ROLE_USER"));
        String loginsRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String r1 = JsonPath.read(loginsRes, "$.data.refreshToken");

        // 2) refresh with r1 (success)
        String refreshRes1 = mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + r1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn().getResponse().getContentAsString();

        String r2 = JsonPath.read(refreshRes1, "$.data.refreshToken");

        // 3) reuse r1 -> 401
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + r1))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").value("REFRESH_REVOKED_OR_REUSED"));


        // 4) r2 -> should succeed
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + r2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

    }





}
