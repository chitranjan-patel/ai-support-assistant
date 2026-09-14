package com.chatbot.aisupport.repository;

import com.chatbot.aisupport.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
public interface FaqRepository extends JpaRepository<Faq, Long> {

    Optional<Faq> findByQuestionIgnoreCase(String question);

    Optional<Faq> findTopByQuestionContainingIgnoreCase(String question);

    @Query("""
SELECT f FROM Faq f
WHERE LOWER(f.question) LIKE LOWER(CONCAT('%', :text, '%'))
   OR LOWER(CONCAT('%', :text, '%')) LIKE LOWER(CONCAT('%', f.question, '%'))
   OR LOWER(f.question) LIKE LOWER(CONCAT('%', :text))
   OR LOWER(f.question) LIKE LOWER(CONCAT(:text, '%'))
""")
List<Faq> findSimilar(@Param("text") String text);
}