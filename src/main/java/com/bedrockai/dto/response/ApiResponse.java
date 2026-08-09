package com.bedrockai.dto.response;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;
}
