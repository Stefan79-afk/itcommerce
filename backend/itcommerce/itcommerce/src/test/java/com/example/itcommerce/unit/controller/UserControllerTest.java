package com.example.itcommerce.unit.controller;

import com.example.itcommerce.controller.UserController;
import com.example.itcommerce.dto.UserLoginDto;
import com.example.itcommerce.dto.UserRegisterDto;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("should return the registered user")
    void shouldReturnTheRegisteredUser() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setPassword("password");
        userRegisterDto.setName("Test User");

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName("Test User");
        expectedUser.setEmail("test@example.com");
        expectedUser.setPassword("encrypted_password");

        when(this.userService.register(any(UserRegisterDto.class))).thenReturn(expectedUser);

        ResponseEntity<User> response = this.userController.register(userRegisterDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("should return the jwt for the login")
    void shouldReturnTheJWTForTheLogin() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@example.com");
        userLoginDto.setPassword("password");

        String expectedToken = "token";

        when(this.userService.login(any(UserLoginDto.class))).thenReturn(expectedToken);

        ResponseEntity<Map<String, String>> response = this.userController.login(userLoginDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        assertThat(response.getHeaders()).containsKey("Authorization");
        assertThat(response.getHeaders()).containsEntry("Authorization", Collections.singletonList("Bearer " + expectedToken));
    }
}
