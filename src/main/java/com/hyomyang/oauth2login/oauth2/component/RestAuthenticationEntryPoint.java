package com.hyomyang.oauth2login.oauth2.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyomyang.oauth2login.oauth2.dto.error.ErrorCode;
import com.hyomyang.oauth2login.oauth2.dto.error.ErrorResponse;
import com.hyomyang.oauth2login.oauth2.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;



    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String authError = (String) request.getAttribute("auth_error");
        // null이면 "null"이 되니, 아래처럼 처리할 수도
        if (request.getAttribute("auth_error") == null) authError = "";


        ErrorCode errorCode = switch (authError) {
            case "TOKEN_EXPIRED" -> ErrorCode.TOKEN_EXPIRED;
            case "TOKEN_INVALID_SIGNATURE" -> ErrorCode.TOKEN_EXPIRED;
            case "TOKEN_MALFORMED" -> ErrorCode.TOKEN_EXPIRED;
            case "TOKEN_UNSUPPORTED" -> ErrorCode.TOKEN_EXPIRED;
            case "TOKEN_INVALID" -> ErrorCode.TOKEN_EXPIRED;
            default -> ErrorCode.UNAUTHORIZED;
        };

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse err = ErrorResponse.of(
                errorCode,
                request.getRequestURI()
        );

        ApiResponse<ErrorResponse> body = ApiResponse.error(
                err,
                errorCode.getMessage()
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}
