package com.example.itcommerce.service;

import com.example.itcommerce.dto.UserForm;
import com.example.itcommerce.entity.User;
import com.example.itcommerce.exception.UserRegistrationException;
import com.example.itcommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserForm userForm) {
        try {
            String encryptedPassword = this.passwordEncoder.encode(userForm.getPassword());

            User user = new User();
            user.setEmail(userForm.getEmail());
            user.setPassword(encryptedPassword);
            user.setName(userForm.getName());

            return this.userRepository.save(user);
        } catch (Exception e) {
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

}
