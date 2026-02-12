package com.hyomyang.oauth2login.oauth2.controller;


import com.hyomyang.oauth2login.oauth2.dto.auth.LoginRequest;
import com.hyomyang.oauth2login.oauth2.dto.auth.RefreshRequest;
import com.hyomyang.oauth2login.oauth2.dto.auth.TokenPairResponse;
import com.hyomyang.oauth2login.oauth2.dto.response.ApiResponse;
import com.hyomyang.oauth2login.oauth2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPairResponse>> login(@RequestBody LoginRequest req,
                                                                @RequestHeader(value = "X-Device-Id", required = false) String deviceId){
        String resolveDeviceId = resolveDeviceId(deviceId);
        var pair = authService.login(
                req.email(),
                req.password(),
                resolveDeviceId);
        return ResponseEntity.ok()
                .header("X-Device-Id", resolveDeviceId)
                .body(ApiResponse.ok(TokenPairResponse.from(pair)));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPairResponse>> refresh( @RequestHeader("Authorization") String authorization,
                                                                   @RequestHeader(value = "X-Device-Id", required = false) String deviceId){
        String resolveDeviceId = resolveDeviceId(deviceId);
        String refreshToken = authorization.replace("Bearer ", "");
        return ResponseEntity.ok(ApiResponse.ok(TokenPairResponse.from(authService.refresh(refreshToken, resolveDeviceId))));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshRequest req){
        authService.logout(req.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private String resolveDeviceId(String deviceId) {
        if(deviceId != null && !deviceId.isBlank()) {
            return deviceId;
        }
        return UUID.randomUUID().toString();
    }

}
