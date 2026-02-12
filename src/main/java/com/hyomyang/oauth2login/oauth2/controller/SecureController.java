package com.hyomyang.oauth2login.oauth2.controller;


import com.hyomyang.oauth2login.oauth2.dto.response.ApiResponse;
import com.hyomyang.oauth2login.oauth2.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping(@AuthenticationPrincipal UserPrincipal userPrincipal){

        String msg = "pong:" + userPrincipal.getUserId();
        return ResponseEntity.ok(ApiResponse.ok(msg));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ping")
    public ResponseEntity<ApiResponse<String>> adminPing(@AuthenticationPrincipal UserPrincipal userPrincipal){
        String msg = "admin-pong:" + userPrincipal.getUserId();
        return ResponseEntity.ok(ApiResponse.ok(msg));
    }
}
