package com.hyomyang.oauth2login.oauth2.dto.auth;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenPairResponse from(TokenPair pair) {
        return new TokenPairResponse(pair.accessToken(), pair.refreshToken());
    }
}
