package com.hyomyang.oauth2login.oauth2.dto.response;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(T data, String msg){
        return new ApiResponse<>(false, data, msg);
    }
    public static <T> ApiResponse<T> error(T data){
        return new ApiResponse<>(false, data, null);
    }
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(false, null, msg);
    }
}
