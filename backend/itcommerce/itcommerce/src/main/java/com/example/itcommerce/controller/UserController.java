package com.example.itcommerce.controller;

import com.example.itcommerce.dto.UserForm;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<User> register(@Valid @RequestBody UserForm userForm) {
        User user = this.userService.register(userForm);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

}
