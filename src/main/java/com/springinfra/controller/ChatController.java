package com.springinfra.controller;


import com.springinfra.dto.request.ChatRequest;
import com.springinfra.dto.response.*;
import com.springinfra.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        UUID userId = getAuthenticatedUserId();
        log.info("Chat request from user {}: {}", userId, request);
        ChatResponse data = chatService.chat(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(HttpStatus.CREATED, data, "Chat Processed"));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<ChatSessionResponse>>> getAllSessions(
            @RequestParam(required = false) String search, 
            Pageable pageable) {
        UUID userId = getAuthenticatedUserId();
        log.info("Fetching all sessions for user: {}", userId);
        Page<ChatSessionResponse> data = chatService.getUserSessions(userId, search, pageable);
        return ResponseEntity.ok(success(HttpStatus.OK, data, "Chat Sessions fetched"));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getSessionMessages(@PathVariable UUID sessionId) {
        UUID userId = getAuthenticatedUserId();
        log.info("Fetching messages for session: {}, user: {}", sessionId, userId);
        List<ChatMessageResponse> data = chatService.getSessionHistory(userId, sessionId);
        return ResponseEntity.ok(success(HttpStatus.OK, data, "Chat messages fetched"));
    }

    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable UUID sessionId) {
        UUID userId = getAuthenticatedUserId();
        log.info("Deleting session: {}, user: {}", sessionId, userId);
        chatService.deleteSession(userId, sessionId);
        return ResponseEntity.ok(success(HttpStatus.OK, null, "Session Deleted"));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }

    private static <T> ApiResponse<T> success(HttpStatus status, T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }
}