package com.chatbot.aisupport.controller;

import com.chatbot.aisupport.repository.ChatMessageRepository;
import com.chatbot.aisupport.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin
public class AnalyticsController {

    @Autowired
    private ChatMessageRepository chatRepo;

    @Autowired
    private FaqRepository faqRepo;

    @GetMapping
    public Map<String, Object> getAnalytics() {

        Map<String, Object> data = new HashMap<>();

        // Basic Stats
        data.put("totalMessages", chatRepo.count());
        data.put("totalFaqs", faqRepo.count());
        data.put("totalSessions", chatRepo.countDistinctSessions());
        data.put("totalUsers", chatRepo.countDistinctUsers());

        // ✅ NEW: FAQ vs AI usage
        long faqCount = chatRepo.countByResponseSource("FAQ");
        long aiCount = chatRepo.countByResponseSource("AI");

        data.put("faqCount", faqCount);
        data.put("aiCount", aiCount);

        return data;
    }
}