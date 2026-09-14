package com.chatbot.aisupport.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();

    // 🔥 FIX 1: Allow UI/static files
    if (!path.startsWith("/api/")) {
        filterChain.doFilter(request, response);
        return;
    }

    // 🔥 FIX 2: Allow auth APIs
    if (path.startsWith("/api/auth") 
    || path.contains("login.html") 
    || path.contains("register.html")
    || path.contains("/api/analytics")) {

    filterChain.doFilter(request, response);
    return;
}

    String authHeader = request.getHeader("Authorization");

    // ❌ No token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Missing");
        return;
    }

    String token = authHeader.substring(7);

    System.out.println("Incoming Token: " + token);

    if (!jwtUtil.validateToken(token)) {
        System.out.println("Token INVALID");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
        return;
    }

    System.out.println("Token VALID");

    filterChain.doFilter(request, response);
}
}