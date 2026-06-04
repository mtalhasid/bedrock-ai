package com.springinfra.service.chat;

import com.springinfra.dto.response.LlmResult;
import com.springinfra.entity.ChatMessage;
import java.util.List;

public interface LlmService {
    LlmResult generateResponse(List<ChatMessage> history, String prompt);
}
