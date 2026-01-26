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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired
    StringRedisTemplate redis;

    private String userId;
    private static final String TEST_DEVICE_ID = "test-device-123";

    @BeforeEach
    void setUp() {

        // ✅ Redis 상태 초기화
        redis.getConnectionFactory().getConnection().flushAll();

        // ✅ DB 상태 초기화 (다른 테스트가 만든 유저/데이터 제거)
        userRepository.deleteAll();


        User user = userRepository.findByEmail("pagooo@naver.com")
                .orElseGet(() -> userRepository.save(new User("pagooo@naver.com", passwordEncoder.encode("1234"), "test_user","ROLE_USER", true)));

        userId = user.getEmail();
    }

    @Test
    void login_shouldIssueAccessAndRefresh() throws Exception {
        Map<String, String> req = Map.of("email", userId, "password", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", TEST_DEVICE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }


    @Test
    void refresh_rotation_shouldRejectReuse() throws Exception {
        // 1) login
        Map<String, Object> req = Map.of("email", userId, "password", "1234");
        String loginsRes = mockMvc.perform(post("/api/auth/login")
                .header("X-Device-Id", TEST_DEVICE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String r1 = JsonPath.read(loginsRes, "$.data.refreshToken");

        // 2) refresh with r1 (success)
        String refreshRes1 = mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + r1)
                        .header("X-Device-Id", TEST_DEVICE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn().getResponse().getContentAsString();

        String r2 = JsonPath.read(refreshRes1, "$.data.refreshToken");

        // 3) reuse r1 -> 401
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + r1)
                .header("X-Device-Id", TEST_DEVICE_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").value("REFRESH_REVOKED_OR_REUSED"));


        // 4) r2 -> should succeed
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + r2)
                .header("X-Device-Id", TEST_DEVICE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

    }



}
