package com.example.itcommerce.controller;

import com.example.itcommerce.dto.UserLoginDto;
import com.example.itcommerce.dto.UserRegisterDto;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        User user = this.userService.register(userRegisterDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        String jwtToken = this.userService.login(userLoginDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .body(new HashMap<>());
    }

}
