package com.bedrockai.service.chat;

import com.bedrockai.dto.request.ChatRequest;
import com.bedrockai.dto.response.ChatMessageResponse;
import com.bedrockai.dto.response.ChatResponse;
import com.bedrockai.dto.response.ChatSessionResponse;
import com.bedrockai.dto.response.LlmResult;
import com.bedrockai.entity.ChatMessage;
import com.bedrockai.entity.ChatSession;
import com.bedrockai.entity.Role;
import com.bedrockai.entity.User;
import com.bedrockai.exception.AppException;
import com.bedrockai.repository.ChatMessageRepository;
import com.bedrockai.repository.ChatSessionRepository;
import com.bedrockai.repository.UserRepository;
import com.bedrockai.service.chat.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final LlmService llmService;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "sessions", key = "#userId"),
        @CacheEvict(value = "history", key = "#request.sessionId", condition = "#request.sessionId != null")
    })
    public ChatResponse chat(UUID userId, ChatRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        ChatSession session;
        List<ChatMessage> history;

        if (request.sessionId() == null) {
            // Start a new session
            LlmResult llmResponse = llmService.generateResponse(List.of(), request.prompt());
            
            session = ChatSession.builder()
                    .user(user)
                    .title(llmResponse.title() != null ? llmResponse.title() : "New Chat")
                    .build();
            session = sessionRepository.save(session);
            
            saveMessage(session, Role.USER, request.prompt());
            saveMessage(session, Role.MODEL, llmResponse.answer());
            
            return new ChatResponse(session.getId(), llmResponse.answer());
        }

        // Continue existing session
        session = sessionRepository.findByIdAndUserId(request.sessionId(), userId)
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));

        history = messageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        LlmResult llmResponse = llmService.generateResponse(history, request.prompt());
        
        saveMessage(session, Role.USER, request.prompt());
        saveMessage(session, Role.MODEL, llmResponse.answer());

        return new ChatResponse(session.getId(), llmResponse.answer());
    }

    private void saveMessage(ChatSession session, Role role, String content) {
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .role(role)
                .content(content)
                .build();
        messageRepository.save(message);
    }

    @Override
    @Cacheable(value = "sessions", key = "#userId + '-' + #search + '-' + #pageable.pageNumber")
    public Page<ChatSessionResponse> getUserSessions(UUID userId, String search, Pageable pageable) {
        Page<ChatSession> sessions;
        if (search != null && !search.isBlank()) {
            sessions = sessionRepository.findByUserIdAndTitleContainingIgnoreCase(userId, search, pageable);
        } else {
            sessions = sessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return sessions.map(s -> new ChatSessionResponse(s.getId(), s.getTitle(), s.getCreatedAt()));
    }

    @Override
    @Cacheable(value = "history", key = "#sessionId")
    public List<ChatMessageResponse> getSessionHistory(UUID userId, UUID sessionId) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId()).stream()
                .map(m -> new ChatMessageResponse(m.getRole(), m.getContent(), m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "sessions", key = "#userId"),
        @CacheEvict(value = "history", key = "#sessionId")
    })
    public void deleteSession(UUID userId, UUID sessionId) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));
        sessionRepository.delete(session);
    }
}
