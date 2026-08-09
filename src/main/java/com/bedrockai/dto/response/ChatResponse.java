package com.bedrockai.dto.response;

import java.util.UUID;

public record ChatResponse(
    UUID sessionId,
    String reply
) {}
