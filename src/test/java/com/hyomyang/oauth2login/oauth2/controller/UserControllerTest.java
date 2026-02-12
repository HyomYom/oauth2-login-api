package com.hyomyang.oauth2login.oauth2.controller;


import com.hyomyang.oauth2login.oauth2.dto.user.UserResponse;
import com.hyomyang.oauth2login.oauth2.logger.TestLogger;
import com.hyomyang.oauth2login.oauth2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(TestLogger.class)
@ActiveProfiles("test")
@Slf4j
public class UserControllerTest implements TestWatcher {

    MockMvc mockMvc;
    UserService userService;
    UserController userController;

    @BeforeEach
    void setup(){
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
    }


    @Test
    void getUserById_success() throws Exception {
        UserResponse mockUser = new UserResponse(1L, "test@naver.com", "tester", "USER");
        when(userService.getById(1L)).thenReturn(mockUser);

        mockMvc.perform(
                get("/api/users/1").accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.name").value("tester"))
                .andExpect(jsonPath("$.data.role").value("USER"));



    }

}
