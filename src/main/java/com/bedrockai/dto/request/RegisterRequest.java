package com.bedrockai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotNull(message = "username is required")
        String username,

        @Email(message = "invalid email")
        @NotNull(message = "email is required")
        String email,

        @NotNull(message = "password is required")
        @Size(min = 6, message = "password must be atleast 6 characters")
        String password
) {
}
