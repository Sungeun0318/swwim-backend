package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 파일 스토리지 컨트롤러
 */
@Tag(name = "Storage", description = "파일 업로드 API")
@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final S3Service s3Service;

    public StorageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Operation(summary = "Presigned URL 발급", description = "S3 직접 업로드를 위한 Presigned URL을 발급합니다.")
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUrl(
            @AuthenticationPrincipal String userId,
            @RequestParam String path,
            @RequestParam(defaultValue = "application/octet-stream") String contentType) {
        String presignedUrl = s3Service.generatePresignedUrl(path, contentType);
        String publicUrl = s3Service.getPublicUrl(path);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "uploadUrl", presignedUrl,
                "downloadUrl", publicUrl
        )));
    }

    @Operation(summary = "Presigned URL 발급 (POST)", description = "S3 직접 업로드를 위한 Presigned URL을 발급합니다.")
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUrlPost(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, String> body) {
        String path = body.get("path");
        String contentType = body.getOrDefault("contentType", "application/octet-stream");
        String presignedUrl = s3Service.generatePresignedUrl(path, contentType);
        String publicUrl = s3Service.getPublicUrl(path);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "uploadUrl", presignedUrl,
                "downloadUrl", publicUrl
        )));
    }
}
