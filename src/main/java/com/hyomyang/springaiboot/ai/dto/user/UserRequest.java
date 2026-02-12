package com.hyomyang.springaiboot.ai.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest (
        @Email(message = "{user.email.invalid}")
        @NotBlank(message = "{user.email.required}")
        String email,
        @NotBlank(message ="{user.password.required}")
        String password,
        @NotBlank(message = "{user.name.required}")
        String name,
        @NotBlank(message = "{user.role.required}")
        String role,
//        @NotBlank(message = "{user.active.required}")
        boolean active
        ){

}

