package com.hyomyang.oauth2login.oauth2.dto.error;

import org.springframework.validation.FieldError;

public record FieldErrorDetail(
        String field,
        Object rejectedValue,
        String message
) {
    public static FieldErrorDetail from(FieldError error) {
        String rejected = error.getRejectedValue() == null ? "" : error.getRejectedValue().toString();
        return new FieldErrorDetail(error.getField(), rejected, error.getDefaultMessage());
    }
}
