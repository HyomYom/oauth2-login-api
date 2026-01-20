package com.hyomyang.springaiboot.ai.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyomyang.springaiboot.ai.domain.User;
import com.hyomyang.springaiboot.ai.dto.auth.RefreshRequest;
import com.hyomyang.springaiboot.ai.logger.TestLogger;
import com.hyomyang.springaiboot.ai.repository.UserRepository;
import com.hyomyang.springaiboot.ai.security.jwt.JwtTokenProvider;
import com.hyomyang.springaiboot.ai.security.refresh.RefreshTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(TestLogger.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    RefreshTokenStore refreshTokenStore;
    @Autowired
    UserRepository userRepository;


    private Long userId;

    @BeforeEach
    void setUp() {

        Optional<User> byId = userRepository.findById(1L);
        byId.orElseGet(() -> userRepository.save(new User("pagooo@naver.com", "test", "ROLE_USER")));

        User user = byId.orElseGet(()-> userRepository.save(new User("pagoooo@naver.com", "test", "ROLE_USER")));

        userId = user.getId();
    }

    /**
     * 테스트용: "유효한 refreshToken"을 발급하고 store에 저장까지 해둔다.
     * (실제 login 엔드포인트가 없어도 Day16 테스트 가능)
     */
    private String issueAndStoreRefreshToken(Long userId) {
        String refresh = tokenProvider.createRefreshToken(userId);

        Jws<Claims> jws = tokenProvider.parseToken(refresh);
        String jti = tokenProvider.getJti(jws);
        Instant expAt = jws.getPayload().getExpiration().toInstant();

        refreshTokenStore.save(userId, jti, expAt);
        return refresh;
    }


    @Test
    void refresh_shouldRotate_andReturnNewTokenPair() throws Exception {
        String oldRefresh = issueAndStoreRefreshToken(userId);

        RefreshRequest refreshRequest = new RefreshRequest(oldRefresh);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.data.refreshToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.data.refreshToken", not(oldRefresh)));
    }

    @Test
    void refresh_withReusedOldRefresh_shouldReturn401() throws Exception {
        String oldRefresh = issueAndStoreRefreshToken(userId);
        RefreshRequest refreshRequest = new RefreshRequest(oldRefresh);

        String rotatedRefresh = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.refreshToken", not(isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();



    }
}
