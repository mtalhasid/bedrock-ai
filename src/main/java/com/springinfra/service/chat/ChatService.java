package com.springinfra.service.chat;

import com.springinfra.dto.request.ChatRequest;
import com.springinfra.dto.response.ChatMessageResponse;
import com.springinfra.dto.response.ChatResponse;
import com.springinfra.dto.response.ChatSessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    ChatResponse chat(UUID userId, ChatRequest request);
    Page<ChatSessionResponse> getUserSessions(UUID userId, String search, Pageable pageable);
    List<ChatMessageResponse> getSessionHistory(UUID userId, UUID sessionId);
    void deleteSession(UUID userId, UUID sessionId);
}
