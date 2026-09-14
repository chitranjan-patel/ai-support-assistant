package com.chatbot.aisupport.controller;

import com.chatbot.aisupport.entity.User;
import com.chatbot.aisupport.security.JwtUtil;
import com.chatbot.aisupport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTER API
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    // ✅ LOGIN API WITH JWT
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {

        String result = userService.login(user);

        Map<String, Object> response = new HashMap<>();

        if (result.equals("Login successful")) {

            String token = jwtUtil.generateToken(user.getUsername());

            response.put("token", token);
            response.put("username", user.getUsername());
        } else {
            response.put("error", result);
        }

        return response;
    }
}