package com.springinfra.dto.response;

import com.springinfra.entity.Role;
import java.time.LocalDateTime;

public record ChatMessageResponse(
    Role role,
    String content,
    LocalDateTime createdAt
) {}
