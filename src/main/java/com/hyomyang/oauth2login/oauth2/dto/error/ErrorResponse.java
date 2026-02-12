package com.hyomyang.oauth2login.oauth2.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse( // record 클래스는 순서대로 들어가야 한다.
        String code,
        String message,
        int status,
        String path,
        LocalDateTime timestamp,
        List<FieldErrorDetail> errors
) {
    public static ErrorResponse of(ErrorCode errorCode, String path){
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                path,
                LocalDateTime.now(),
                List.of()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                path,
                LocalDateTime.now(),
                fieldErrors
        );
    }
}
