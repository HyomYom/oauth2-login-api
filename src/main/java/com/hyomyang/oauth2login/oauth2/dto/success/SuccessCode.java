package com.hyomyang.oauth2login.oauth2.dto.success;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    AUTHORIZED(HttpStatus.OK, "S001", "인증된 토큰입니다.")

    ;
    private final HttpStatus status;
    private final String code;
    private final String message;

}
