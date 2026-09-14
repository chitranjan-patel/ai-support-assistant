package com.chatbot.aisupport.service;

import com.chatbot.aisupport.entity.ChatMessage;
import com.chatbot.aisupport.entity.Faq;
import com.chatbot.aisupport.repository.ChatMessageRepository;
import com.chatbot.aisupport.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private FaqRepository faqRepository;

    public ChatMessage saveMessage(ChatMessage message) {

        String username = message.getUsername();

        // ===============================
        // ✅ NULL / EMPTY CHECK
        // ===============================
        if (message.getUserMessage() == null || message.getUserMessage().isBlank()) {
            message.setBotReply("Please enter a valid message.");
            message.setResponseSource("SYSTEM");
            return repository.save(message);
        }

        // ===============================
        // ✅ NORMALIZE USER INPUT
        // ===============================
        String cleanUser = message.getUserMessage()
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();

        // ===============================
        // ✅ GREETING CHECK
        // ===============================
        List<String> greetings = List.of(
                "hi", "hello", "hey",
                "good morning", "good evening", "good afternoon"
        );

        if (greetings.stream().anyMatch(cleanUser::contains)) {
            message.setBotReply("Hello! 👋 How can I assist you today?");
            message.setResponseSource("SYSTEM");
            return repository.save(message);
        }

        // ===============================
        // ✅ FAQ MATCH (FINAL PRODUCTION)
        // ===============================
        List<Faq> allFaqs = faqRepository.findAll();

        for (Faq faq : allFaqs) {

            String cleanDb = faq.getQuestion()
                    .toLowerCase()
                    .replaceAll("[^a-zA-Z0-9 ]", "")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (cleanDb.equals(cleanUser) ||
                cleanDb.contains(cleanUser) ||
                cleanUser.contains(cleanDb)) {

                System.out.println("🔥 FAQ MATCH FOUND: " + faq.getQuestion());

                message.setBotReply(faq.getAnswer());
                message.setResponseSource("FAQ");

                return repository.save(message);
            }
        }

        // ===============================
        // ✅ LOAD LAST 6 CHAT HISTORY
        // ===============================
        List<ChatMessage> history =
                repository.findBySessionIdAndUsernameOrderByIdAsc(
                        message.getSessionId(),
                        username
                );

        StringBuilder context = new StringBuilder();

        if (history != null && !history.isEmpty()) {

            int start = Math.max(0, history.size() - 6);
            List<ChatMessage> recentHistory = history.subList(start, history.size());

            for (ChatMessage chat : recentHistory) {

                if (chat.getUserMessage() != null) {
                    context.append("User: ")
                           .append(chat.getUserMessage())
                           .append("\n");
                }

                if (chat.getBotReply() != null) {
                    context.append("Bot: ")
                           .append(chat.getBotReply())
                           .append("\n");
                }
            }
        }

        // ===============================
        // ✅ AI PROMPT (CONTROLLED)
        // ===============================
        context.append("\nYou are a helpful AI assistant.\n");
        context.append("Answer directly in 2-3 short lines.\n");
        context.append("Do not give examples or conversations.\n\n");
        context.append("User: ").append(message.getUserMessage());

        String aiReply = ollamaService.getAIResponse(context.toString());

        message.setBotReply(aiReply);
        message.setResponseSource("AI");

        return repository.save(message);
    }

    public List<ChatMessage> getChatBySession(String sessionId, String username) {
        return repository.findBySessionIdAndUsernameOrderByIdAsc(sessionId, username);
    }

    public void clearAll() {
        repository.deleteAll();
    }

    public List<String> getAllSessions(String username) {
        return repository.findSessionsByUsername(username);
    }

    public List<ChatMessage> getAllMessages() {
        return repository.findAll();
    }
}