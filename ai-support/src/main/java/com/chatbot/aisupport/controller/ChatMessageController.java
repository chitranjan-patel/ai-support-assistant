package com.chatbot.aisupport.controller;

import com.chatbot.aisupport.entity.ChatMessage;
import com.chatbot.aisupport.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatMessageController {

    @Autowired
    private ChatMessageService service;

    // ✅ SEND MESSAGE TO AI
    @PostMapping
    public ChatMessage saveMessage(@RequestBody ChatMessage message) {
        return service.saveMessage(message);
    }

    // ✅ LOAD CHAT BY SESSION + USERNAME
    @GetMapping("/{sessionId}/{username}")
    public List<ChatMessage> getChatBySession(
            @PathVariable String sessionId,
            @PathVariable String username) {

        return service.getChatBySession(sessionId, username);
    }

    // ✅ CLEAR ALL CHAT (optional)
    @DeleteMapping("/clear")
    public String clearChat() {
        service.clearAll();
        return "Chat cleared";
    }

    // ✅ GET ALL SESSIONS OF LOGGED-IN USER
    @GetMapping("/sessions/{username}")
    public List<String> getAllSessions(@PathVariable String username) {
        return service.getAllSessions(username);
    }
}