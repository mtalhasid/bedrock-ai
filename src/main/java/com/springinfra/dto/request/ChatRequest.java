package com.springinfra.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ChatRequest(
    @NotBlank(message = "Prompt cannot be empty")
    String prompt,
    
    UUID sessionId
) {}
