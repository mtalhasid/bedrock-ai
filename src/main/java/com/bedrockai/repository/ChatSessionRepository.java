package com.bedrockai.repository;

import com.bedrockai.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<ChatSession> findByIdAndUserId(UUID id, UUID userId);

    @EntityGraph(attributePaths = {"user"})
    Page<ChatSession> findByUserIdAndTitleContainingIgnoreCase(UUID userId, String title, Pageable pageable);
}
