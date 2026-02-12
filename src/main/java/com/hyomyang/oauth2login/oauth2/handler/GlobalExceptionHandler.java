package com.hyomyang.oauth2login.oauth2.handler;


import com.hyomyang.oauth2login.oauth2.dto.error.ErrorCode;
import com.hyomyang.oauth2login.oauth2.dto.error.ErrorResponse;
import com.hyomyang.oauth2login.oauth2.dto.error.FieldErrorDetail;
import com.hyomyang.oauth2login.oauth2.dto.response.ApiResponse;
import com.hyomyang.oauth2login.oauth2.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1) @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handelValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldErrorDetail::from)
                .toList();

        log.warn("Validation failed: {} {}", req.getRequestURI(), fieldErrors, ex);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDATION_FAILED,
                req.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(ApiResponse.error(
                        response,
                        ErrorCode.VALIDATION_FAILED.getMessage()
                        )
                );
    };

    // 2) JSON 파싱 오류
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMalformedJson(
            Exception ex,
            HttpServletRequest request
    ) {
        log.warn("Malformed JSON: {}", request.getRequestURI(), ex);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.MALFORMED_JSON,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.MALFORMED_JSON.getStatus())
                .body(ApiResponse.error(
                        response,
                        ErrorCode.MALFORMED_JSON.getMessage()
                ));
    }

    // 3) 토큰
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ){
        log.warn("Unauthorized: {}", request.getRequestURI(), ex);
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = ErrorResponse.of(
                errorCode,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(
                        response,
                        response.message()
                ));
    }


    // 4) 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error: {}", request.getRequestURI(), ex);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.BUSINESS_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.BUSINESS_ERROR.getStatus())
                .body(ApiResponse.error(
                        response,
                        ErrorCode.BUSINESS_ERROR.getMessage()
                ));
    }

}
