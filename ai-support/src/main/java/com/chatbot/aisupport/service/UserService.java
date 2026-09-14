package com.chatbot.aisupport.service;

import com.chatbot.aisupport.entity.User;
import com.chatbot.aisupport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ REGISTER
    public String register(User user) {

        Optional<User> existingUser =
                userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            return "Username already exists";
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    // ✅ LOGIN
    public String login(User user) {

        Optional<User> existingUser =
                userRepository.findByUsername(user.getUsername());

        if (existingUser.isEmpty()) {
            return "User not found";
        }

        if (!existingUser.get().getPassword().equals(user.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }
}