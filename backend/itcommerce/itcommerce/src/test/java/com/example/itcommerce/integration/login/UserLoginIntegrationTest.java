package com.example.itcommerce.integration.login;

import com.example.itcommerce.dto.UserLoginDto;
import com.example.itcommerce.dto.UserRegisterDto;
import com.example.itcommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static UserLoginDto buildUserLoginDto() {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("test@example.com");
        userLoginDto.setPassword("password");

        return userLoginDto;
    }

    private static UserRegisterDto buildUserRegisterDto() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setPassword("password");
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setName("Test User");

        return userRegisterDto;
    }

    @Test
    @DisplayName("should login the user successfully")
    void shouldLoginTheUserSuccessfully() {
        UserRegisterDto userRegisterDto = buildUserRegisterDto();
        UserLoginDto userLoginDto = buildUserLoginDto();

        try {
            this.mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(userRegisterDto)))
                    .andExpect(status().isCreated());
            assertThat(this.userRepository.findByEmail("test@example.com")).isNotNull();

            this.mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(userLoginDto)))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Authorization"))
                    .andExpect(header().string("Authorization", startsWith("Bearer ")));
        } catch (Exception e) {
            fail("Test failed " + e.getMessage());
        }
    }

    @Test
    @DisplayName("should not login the user if password is incorrect")
    void shouldNotLoginTheUserIfPasswordIsIncorrect() {
        UserRegisterDto userRegisterDto = buildUserRegisterDto();
        UserLoginDto userLoginDto = buildUserLoginDto();
        userLoginDto.setPassword("wrong password");

        try {
            this.mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(userRegisterDto)))
                    .andExpect(status().isCreated());
            assertThat(this.userRepository.findByEmail("test@example.com")).isNotNull();

            this.mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(userLoginDto)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed " + e.getMessage());
        }
    }

    @Test
    @DisplayName("should not login the user if the request body is invalid")
    void shouldNotLoginTheUserIfTheRequestBodyIsInvalid() {
        UserRegisterDto userRegisterDto = buildUserRegisterDto();
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setPassword("123");
        userLoginDto.setEmail("bad email");

        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("password", "Password must be at least 8 characters long");
        expectedBody.put("email", "Email must be a valid format");

        try {
            this.mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(userRegisterDto)))
                    .andExpect(status().isCreated());
            assertThat(this.userRepository.findByEmail("test@example.com")).isNotNull();

            this.mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(userLoginDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(this.objectMapper.writeValueAsString(expectedBody)));
        } catch (Exception e) {
            fail("Test failed " + e.getMessage());
        }
    }
}
