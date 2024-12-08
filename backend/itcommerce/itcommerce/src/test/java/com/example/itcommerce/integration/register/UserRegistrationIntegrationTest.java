package com.example.itcommerce.integration.register;

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

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserRegistrationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static UserRegisterDto buildRegisterDto() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();

        userRegisterDto.setName("Test User");
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setPassword("password");

        return userRegisterDto;
    }

    @Test
    @DisplayName("should register the user successfully")
    void shouldRegisterUserSuccessfully() {
        UserRegisterDto userRegisterDto = buildRegisterDto();

        try {
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRegisterDto)))
                    .andExpect(status().isCreated());
        } catch(Exception e) {
            fail("Test failed: " + e.getMessage());
        }

        assertThat(this.userRepository.findByEmail("test@example.com")).isNotNull();
    }

//    @Test
//    @DisplayName("should not register the user if email already exists")
//    void shouldNotRegisterTheUserIfEmailAlreadyExists() {
//        UserRegisterDto userRegisterDto = buildRegisterDto();
//
//        try {
//            this.mockMvc.perform(post("/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(this.objectMapper.writeValueAsString(userRegisterDto)))
//                    .andExpect(status().isCreated());
//
//            assertThat(this.userRepository.findByEmail("test@example.com")).isNotNull();
//
//            this.mockMvc.perform(post("/register")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(this.objectMapper.writeValueAsString(userRegisterDto)))
//                    .andExpect(status().isConflict());
//        } catch (Exception e) {
//            fail("Test failed: " + e.getMessage());
//        }
//    }

    @Test
    @DisplayName("should not register the user if the request body is not valid")
    void shouldNotRegisterTheUserIfTheRequestBodyIsNotValid() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("bad email");
        userRegisterDto.setPassword("123");

        final Map<String, String > expectedBody = new HashMap<>();
        expectedBody.put("password", "Password must be at least 8 characters long");
        expectedBody.put("name", "Name must not be empty");
        expectedBody.put("email", "Email must be a valid format");

        try {
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRegisterDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(this.objectMapper.writeValueAsString(expectedBody)));
        } catch(Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

}
