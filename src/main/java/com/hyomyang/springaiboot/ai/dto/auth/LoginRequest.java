package com.hyomyang.springaiboot.ai.dto.auth;

import java.util.Set;

public record LoginRequest(
        Long id,
        String username,
        String password,
        Set<String> roles
) {
}
