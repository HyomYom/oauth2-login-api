package com.hyomyang.springaiboot.ai.controller;


import com.hyomyang.springaiboot.ai.component.TokenExtractor;
import com.hyomyang.springaiboot.ai.dto.auth.LoginRequest;
import com.hyomyang.springaiboot.ai.dto.auth.RefreshRequest;
import com.hyomyang.springaiboot.ai.dto.auth.TokenPair;
import com.hyomyang.springaiboot.ai.dto.auth.TokenPairResponse;
import com.hyomyang.springaiboot.ai.dto.response.ApiResponse;
import com.hyomyang.springaiboot.ai.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPairResponse>> login(@RequestBody LoginRequest req){
        var pair = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok(TokenPairResponse.from(pair)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPairResponse>> refresh( @RequestHeader("Authorization") String authorization){
        String refreshToken = authorization.replace("Bearer ", "");
        return ResponseEntity.ok(ApiResponse.ok(TokenPairResponse.from(authService.refresh(refreshToken))));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshRequest req){
        authService.logout(req.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

}
