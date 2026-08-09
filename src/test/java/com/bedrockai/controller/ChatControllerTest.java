package com.bedrockai.controller;

import com.bedrockai.dto.request.ChatRequest;
import com.bedrockai.dto.response.ApiResponse;
import com.bedrockai.dto.response.ChatMessageResponse;
import com.bedrockai.dto.response.ChatResponse;
import com.bedrockai.dto.response.ChatSessionResponse;
import com.bedrockai.service.chat.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @Test
    void chat_returns201_whenSuccessful() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChatRequest request = new ChatRequest("Hello, Bedrock AI!", null);
        ChatResponse mockResponse = mock(ChatResponse.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(chatService.chat(userId, request)).thenReturn(mockResponse);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            ResponseEntity<ApiResponse<ChatResponse>> response = chatController.chat(request);

            // Assert
            assertEquals(201, response.getStatusCode().value());
            assertTrue(response.getBody().isSuccess());
            assertEquals(mockResponse, response.getBody().getData());

            verify(chatService).chat(userId, request);
        }
    }

    @Test
    void getAllSessions_returns200_whenSuccessful() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String search = "learning";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatSessionResponse> mockPage = Page.empty();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(chatService.getUserSessions(userId, search, pageable)).thenReturn(mockPage);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            ResponseEntity<ApiResponse<Page<ChatSessionResponse>>> response =
                    chatController.getAllSessions(search, pageable);

            // Assert
            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().isSuccess());
            assertEquals(mockPage, response.getBody().getData());

            verify(chatService).getUserSessions(userId, search, pageable);
        }
    }

    @Test
    void getSessionMessages_returns200_whenSuccessful() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        List<ChatMessageResponse> mockMessages = List.of(mock(ChatMessageResponse.class));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);
        when(chatService.getSessionHistory(userId, sessionId)).thenReturn(mockMessages);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            ResponseEntity<ApiResponse<List<ChatMessageResponse>>> response =
                    chatController.getSessionMessages(sessionId);

            // Assert
            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().isSuccess());
            assertEquals(mockMessages, response.getBody().getData());

            verify(chatService).getSessionHistory(userId, sessionId);
        }
    }

    @Test
    void deleteSession_returns200_whenSuccessful() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            ResponseEntity<ApiResponse<Void>> response = chatController.deleteSession(sessionId);

            // Assert
            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().isSuccess());

            verify(chatService).deleteSession(userId, sessionId);
        }
    }
}
