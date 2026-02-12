package com.hyomyang.oauth2login.oauth2.dto.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "C001","입력값이 잘못되었습니다."),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "C002", "요청 JSON 형식이 올바르지 않습니다."),
    BUSINESS_ERROR(HttpStatus.CONFLICT, "C003", "비즈니스 로직 오류가 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C004", "유요한 인증정보가 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C005", "접근 권한이 없습니다."),


    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, "TOKEN_TYPE_MISMATCH", "토큰 타입이 올바르지 않습니다."),
    REFRESH_REVOKED_OR_REUSED(HttpStatus.UNAUTHORIZED, "REFRESH_REVOKED_OR_REUSED", "Refresh 토큰이 폐기되었거나 재사용되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "토큰이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "TOKEN_UNSUPPORTED", "지원하지 않는 토큰 형식입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "TOKEN_MALFORMED", "토큰 형식이 올바르지 않습니다."),
    TOKEN_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID_SIGNATURE", "토큰 서명이 올바르지 않습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "로그인 실패."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "USER_INACTIVE", "비활성화된 계정입니다. 관리자에게 문의하세요."),
    DEVICE_ID_REQUIRED(HttpStatus.BAD_REQUEST, "DEVICE_ID_REQUIRED", "디바이스 ID가 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
