package com.zalmuk.swwim.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 헬스 체크 컨트롤러
 */
@Tag(name = "Health Check", description = "서버 상태 확인 API")
@RestController
public class HealthCheckController {

    @Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 실행 중인지 확인합니다.")
    @GetMapping({"/", "/health"})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SWWIM API Server");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
