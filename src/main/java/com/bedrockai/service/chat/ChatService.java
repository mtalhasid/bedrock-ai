package com.bedrockai.service.chat;

import com.bedrockai.dto.request.ChatRequest;
import com.bedrockai.dto.response.ChatMessageResponse;
import com.bedrockai.dto.response.ChatResponse;
import com.bedrockai.dto.response.ChatSessionResponse;
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
