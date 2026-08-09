package com.bedrockai.service.chat;

import com.bedrockai.dto.response.LlmResult;
import com.bedrockai.entity.ChatMessage;
import java.util.List;

public interface LlmService {
    LlmResult generateResponse(List<ChatMessage> history, String prompt);
}
