package com.example.itcommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {
    @Size(max = 255, message = "Name must not be longer than 255 characters")
    @NotBlank(message = "Name must not be empty")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password must not be empty")
    private String password;

    @Size(max = 255, message = "Email must not be longer than 255 characters")
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email must be a valid format")
    private String email;
}
