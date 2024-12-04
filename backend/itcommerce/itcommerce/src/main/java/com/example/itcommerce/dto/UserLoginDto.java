package com.example.itcommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginDto {
    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String password;

    UserLoginDto(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
}
