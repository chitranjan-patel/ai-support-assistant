package com.chatbot.aisupport.controller;

import com.chatbot.aisupport.entity.Faq;
import com.chatbot.aisupport.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private FaqRepository faqRepository;

    // ✅ GET ALL FAQ
    @GetMapping("/faqs")
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    // ✅ ADD FAQ
    @PostMapping("/faqs")
    public Faq addFaq(@RequestBody Faq faq) {
        return faqRepository.save(faq);
    }

    // ✅ UPDATE FAQ
    @PutMapping("/faqs/{id}")
    public Faq updateFaq(@PathVariable Long id,
                         @RequestBody Faq updatedFaq) {

        Faq faq = faqRepository.findById(id)
                .orElseThrow();

        faq.setQuestion(updatedFaq.getQuestion());
        faq.setAnswer(updatedFaq.getAnswer());

        return faqRepository.save(faq);
    }

    // ✅ DELETE FAQ
    @DeleteMapping("/faqs/{id}")
    public void deleteFaq(@PathVariable Long id) {
        faqRepository.deleteById(id);
    }
}