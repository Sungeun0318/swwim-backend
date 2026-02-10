package com.zalmuk.swwim.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 에러 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * 에러 발생 시각
     */
    private LocalDateTime timestamp;

    /**
     * HTTP 상태 코드
     */
    private Integer status;

    /**
     * 에러 타입
     */
    private String error;

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * 상세 에러 정보 (Validation 등)
     */
    private Map<String, String> details;
}
