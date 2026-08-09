package com.bedrockai.dto.response;

import com.bedrockai.entity.Role;
import java.time.LocalDateTime;

public record ChatMessageResponse(
    Role role,
    String content,
    LocalDateTime createdAt
) {}
