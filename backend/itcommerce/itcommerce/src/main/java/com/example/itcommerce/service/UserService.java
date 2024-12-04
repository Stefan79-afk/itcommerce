package com.example.itcommerce.service;

import com.example.itcommerce.dto.UserLoginDto;
import com.example.itcommerce.dto.UserRegisterDto;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.exception.UserLoginException;
import com.example.itcommerce.exception.UserRegistrationException;
import com.example.itcommerce.repository.UserRepository;
import com.example.itcommerce.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(UserRegisterDto userRegisterDto) {
        try {
            String encryptedPassword = this.passwordEncoder.encode(userRegisterDto.getPassword());

            User user = new User();
            user.setEmail(userRegisterDto.getEmail());
            user.setPassword(encryptedPassword);
            user.setName(userRegisterDto.getName());

            return this.userRepository.save(user);
        } catch (Exception e) {
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

    public String login(UserLoginDto userLoginDto) {
        String email = userLoginDto.getEmail();

       User user = this.userRepository.findByEmail(email);

       if (user == null) {
           throw new UserLoginException(String.format("No user with email %s found", email), new IllegalArgumentException("Invalid email"));
       }
       if (this.passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
           return jwtUtil.generateToken(email);
       } else {
           throw new UserLoginException("Invalid password", new IllegalArgumentException("Invalid password"));
       }

    }

}
