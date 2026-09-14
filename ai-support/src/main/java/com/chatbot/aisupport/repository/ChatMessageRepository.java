package com.chatbot.aisupport.repository;

import com.chatbot.aisupport.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop5ByOrderByIdDesc();

    List<ChatMessage> findBySessionIdOrderByIdAsc(String sessionId);

    @Query("SELECT DISTINCT c.sessionId FROM ChatMessage c ORDER BY c.id DESC")
    List<String> findAllSessions();

    List<ChatMessage> findBySessionIdAndUsernameOrderByIdAsc(String sessionId, String username);
@Query("SELECT COUNT(DISTINCT c.sessionId) FROM ChatMessage c")
long countDistinctSessions();

@Query("SELECT COUNT(DISTINCT c.username) FROM ChatMessage c")
long countDistinctUsers();

@Query("""
SELECT DISTINCT c.sessionId
FROM ChatMessage c
WHERE c.username = :username
ORDER BY c.sessionId DESC
""")
List<String> findSessionsByUsername(@Param("username") String username);
long countByResponseSource(String responseSource);

}