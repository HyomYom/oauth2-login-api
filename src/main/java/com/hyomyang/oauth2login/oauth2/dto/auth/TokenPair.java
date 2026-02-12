package com.hyomyang.oauth2login.oauth2.dto.auth;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
