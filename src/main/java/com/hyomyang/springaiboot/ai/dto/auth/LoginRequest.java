package com.hyomyang.springaiboot.ai.dto.auth;

import java.util.Set;

public record LoginRequest(
        String email,
        String password,
        Set<String> roles
) {
}
