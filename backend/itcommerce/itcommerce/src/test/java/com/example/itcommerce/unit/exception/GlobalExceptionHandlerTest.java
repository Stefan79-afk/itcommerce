package com.example.itcommerce.unit.exception;


import com.example.itcommerce.exception.GlobalExceptionHandler;
import com.example.itcommerce.exception.UserLoginException;
import com.example.itcommerce.exception.UserRegistrationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {


    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("should get a bad request for UserRegistrationException")
    void shouldGetABadRequestForUserRegistrationException() {
        DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException("DataIntegrityViolationException");
        UserRegistrationException userRegistrationException = new UserRegistrationException("UserRegistrationException", dataIntegrityViolationException);
        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("error", "User with email already exists");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleUserRegistrationException(userRegistrationException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("should get an internal server error for any other cause")
    void shouldGetAnInternalServerErrorForAnyOtherCause() {
        Exception e = new Exception("Exception");
        UserRegistrationException userRegistrationException = new UserRegistrationException("UserRegistrationException", e);
        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("error", "Exception");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleUserRegistrationException(userRegistrationException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("should get an internal server error for no cause")
    void shouldGetAnInternalServerErrorForNoCause() {
        UserRegistrationException userRegistrationException = new UserRegistrationException("UserRegistrationException", null);
        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("error", "UserRegistrationException");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleUserRegistrationException(userRegistrationException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("should get a bad request for MethodArgumentNotValidException")
    void shouldGetABadRequestForMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = List.of(
                new FieldError("userRegisterDto", "email", "Email must not be empty"),
                new FieldError("userRegisterDto", "password", "Password is too short")
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Map<String, String> expectedBody = Map.of("email", "Email must not be empty", "password", "Password is too short");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("should get an Unauthorized for UserLoginException")
    void shouldGetAnUnauthorizedForUserLoginException() {
        UserLoginException userLoginException = new UserLoginException("UserLoginException", new IllegalArgumentException("IllegalArgumentException"));

        Map<String, String> expectedBody = Map.of("error", "Email or password is incorrect");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleLoginException(userLoginException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("should get an Internal Server Error for UserLoginException")
    void shouldGetAnInternalServerErrorForUserLoginException() {
        UserLoginException userLoginException = new UserLoginException("UserLoginException", new Exception("Exception"));

        Map<String, String> expectedBody = Map.of("error", "Unexpected error: UserLoginException");

        ResponseEntity<Map<String, String>> response = this.globalExceptionHandler.handleLoginException(userLoginException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(expectedBody);
    }

}
