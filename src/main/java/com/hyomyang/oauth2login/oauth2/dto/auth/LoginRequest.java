package com.hyomyang.oauth2login.oauth2.dto.auth;

import java.util.Set;

public record LoginRequest(
        String email,
        String password,
        Set<String> roles
) {
}
