package com.example.itcommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
public class UserLoginDto {
    @Size(max = 255, message = "Email must not be longer than 255 characters")
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email must be a valid format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password must not be empty")
    private String password;

}
