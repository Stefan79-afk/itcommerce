package com.example.itcommerce.unit.service;

import com.example.itcommerce.dto.UserLoginDto;
import com.example.itcommerce.dto.UserRegisterDto;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.exception.UserLoginException;
import com.example.itcommerce.exception.UserRegistrationException;
import com.example.itcommerce.repository.UserRepository;
import com.example.itcommerce.service.UserService;
import com.example.itcommerce.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private static UserLoginDto getUserLoginDto() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@example.com");
        userLoginDto.setPassword("password");
        return userLoginDto;
    }

    private static User getExpectedUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encrypted_password");
        user.setName("Test User");
        return user;
    }

    private static UserRegisterDto getUserRegisterDto() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setName("Test User");
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setPassword("password");
        return userRegisterDto;
    }


    @Test
    @DisplayName("should register the user successfully")
    void shouldRegisterTheUserSuccessfully() {
        UserRegisterDto userRegisterDto = getUserRegisterDto();

        when(this.passwordEncoder.encode(any(String.class))).thenReturn("encrypted_password");

        User expectedUser = getExpectedUser();

        when(this.userRepository.save(any(User.class))).thenReturn(expectedUser);

        User user = this.userService.register(userRegisterDto);

        assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("should return the jwt token successfully")
    void shouldReturnTheJWTTokenSuccessfully() {
        UserLoginDto userLoginDto = getUserLoginDto();

        User user = getExpectedUser();

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(user);
        when(this.passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        when(this.jwtUtil.generateToken(any(String.class))).thenReturn("token");

        String token = this.userService.login(userLoginDto);
        assertThat(token).isEqualTo("token");

    }

    @Test
    @DisplayName("should throw UserRegistrationException when registration fails")
    void shouldThrowUserRegistrationExceptionsWhenRegistrationFails() {
        UserRegisterDto userRegisterDto = getUserRegisterDto();

        DataIntegrityViolationException dte = new DataIntegrityViolationException("Failed to save user to database");

        when(this.userRepository.save(any(User.class))).thenThrow(dte);

        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userService.register(userRegisterDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to register user");
        assertThat(exception.getCause()).isEqualTo(dte);

    }

    @Test
    @DisplayName("should throw UserLoginException when user doesn't exist")
    void shouldThrowUserLoginExceptionWhenUserDoesntExist() {
        UserLoginDto userLoginDto = getUserLoginDto();

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(null);

        UserLoginException userLoginException = assertThrows(UserLoginException.class, () -> {
            this.userService.login(userLoginDto);
        });

        assertThat(userLoginException.getMessage()).isEqualTo("No user with email test@example.com found");
        assertThat(userLoginException.getCause().getMessage()).isEqualTo("Invalid email");
        assertThat(userLoginException.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should throw UserLoginException when password is invalid")
    void shouldThrowUserLoginExceptionWhenPasswordIsInvalid() {
        UserLoginDto userLoginDto = getUserLoginDto();

        when(this.passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);
        when(this.userRepository.findByEmail(any(String.class))).thenReturn(getExpectedUser());

        UserLoginException userLoginException = assertThrows(UserLoginException.class, () -> {
            this.userService.login(userLoginDto);
        });

        assertThat(userLoginException.getMessage()).isEqualTo("Invalid password");
        assertThat(userLoginException.getCause().getMessage()).isEqualTo("Invalid password");
        assertThat(userLoginException.getCause()).isInstanceOf(IllegalArgumentException.class);
    }
}
