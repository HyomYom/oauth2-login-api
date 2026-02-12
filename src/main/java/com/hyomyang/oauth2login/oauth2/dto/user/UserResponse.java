package com.hyomyang.oauth2login.oauth2.dto.user;

import com.hyomyang.oauth2login.oauth2.domain.User;

public record UserResponse(
        Long id,
        String email,
        String name,
        String role
    ) {
    private static final String EMPTY_ROLE = "";
    public static UserResponse of(User user){

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                EMPTY_ROLE
        );
    }

    public static UserResponse of(User user, String role){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                role
        );
    }


}
