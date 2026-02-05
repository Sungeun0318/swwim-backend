package com.zalmuk.swwim.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 래퍼
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {

    @Schema(description = "성공 여부")
    private boolean success;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "에러 메시지")
    private String message;

    @Schema(description = "에러 코드")
    private String errorCode;

    @Schema(description = "응답 시간")
    private LocalDateTime timestamp;

    // Constructors
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Static factory methods
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        return response;
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        response.setErrorCode(errorCode);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
