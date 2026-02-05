package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.pool.PoolReviewRequest;
import com.zalmuk.swwim.api.dto.pool.PoolReviewResponse;
import com.zalmuk.swwim.api.dto.pool.SavePoolRequest;
import com.zalmuk.swwim.api.dto.pool.SwimmingPoolResponse;
import com.zalmuk.swwim.api.entity.pool.PoolReview;
import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import com.zalmuk.swwim.api.service.pool.SwimmingPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 수영장 컨트롤러
 */
@Tag(name = "Swimming Pool", description = "수영장 API")
@RestController
@RequestMapping("/api/v1/pools")
public class SwimmingPoolController {

    private final SwimmingPoolService poolService;

    public SwimmingPoolController(SwimmingPoolService poolService) {
        this.poolService = poolService;
    }

    @Operation(summary = "수영장 목록", description = "수영장 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SwimmingPoolResponse>>> getPools() {
        List<SwimmingPool> pools = poolService.findAllSorted();
        List<SwimmingPoolResponse> responses = pools.stream()
                .map(SwimmingPoolResponse::summary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "주변 수영장", description = "현재 위치 주변의 수영장을 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<SwimmingPoolResponse>>> getNearbyPools(
            @Parameter(description = "위도") @RequestParam BigDecimal latitude,
            @Parameter(description = "경도") @RequestParam BigDecimal longitude,
            @Parameter(description = "반경 (km)", example = "5.0") @RequestParam(defaultValue = "5.0") double radius) {
        List<SwimmingPool> pools = poolService.findNearbyPools(latitude, longitude, radius);
        List<SwimmingPoolResponse> responses = pools.stream()
                .map(SwimmingPoolResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "수영장 검색", description = "이름 또는 주소로 수영장을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SwimmingPoolResponse>>> searchPools(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SwimmingPool> pools = poolService.searchByName(keyword, pageable);
        Page<SwimmingPoolResponse> responses = pools.map(SwimmingPoolResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "수영장 상세", description = "수영장의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SwimmingPoolResponse>> getPool(
            @PathVariable Integer id) {
        return poolService.findById(id)
                .map(pool -> ResponseEntity.ok(ApiResponse.success(SwimmingPoolResponse.from(pool))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "리뷰 목록", description = "수영장의 리뷰 목록을 조회합니다.")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<PoolReviewResponse>>> getReviews(
            @PathVariable Integer id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PoolReview> reviews = poolService.getReviews(id, pageable);
        Page<PoolReviewResponse> responses = reviews.map(PoolReviewResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "리뷰 작성", description = "수영장에 리뷰를 작성합니다.")
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PoolReviewResponse>> createReview(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer id,
            @Valid @RequestBody PoolReviewRequest request) {
        PoolReview review = poolService.createReview(id, userId, request.getRating(), request.getComment());
        return ResponseEntity.ok(ApiResponse.success(PoolReviewResponse.from(review), "리뷰가 등록되었습니다."));
    }

    @Operation(summary = "리뷰 삭제", description = "수영장 리뷰를 삭제합니다.")
    @DeleteMapping("/{id}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer id,
            @PathVariable UUID reviewId) {
        poolService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "리뷰가 삭제되었습니다."));
    }

    @Operation(summary = "즐겨찾기 추가 (ID)", description = "수영장 ID로 즐겨찾기에 추가합니다.")
    @PostMapping("/{id}/save")
    public ResponseEntity<ApiResponse<Void>> savePool(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer id) {
        poolService.savePool(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "즐겨찾기에 추가되었습니다."));
    }

    @Operation(summary = "즐겨찾기 추가 (Google Places)", description = "Google Places 정보로 수영장을 저장합니다. 없으면 새로 생성합니다.")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SwimmingPoolResponse>> savePoolByPlaceId(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SavePoolRequest request) {
        SwimmingPool pool = poolService.savePoolByPlaceId(
                userId,
                request.getPlaceId(),
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRating(),
                request.getUserRatingsTotal(),
                request.getPhotoUrl(),
                request.getPhoneNumber(),
                request.getWebsite()
        );
        return ResponseEntity.ok(ApiResponse.success(SwimmingPoolResponse.from(pool), "즐겨찾기에 추가되었습니다."));
    }

    @Operation(summary = "즐겨찾기 제거 (ID)", description = "수영장 ID로 즐겨찾기에서 제거합니다.")
    @DeleteMapping("/{id}/save")
    public ResponseEntity<ApiResponse<Void>> unsavePool(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer id) {
        poolService.unsavePool(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "즐겨찾기에서 제거되었습니다."));
    }

    @Operation(summary = "즐겨찾기 제거 (placeId)", description = "Google Place ID로 즐겨찾기에서 제거합니다.")
    @DeleteMapping("/save/{placeId}")
    public ResponseEntity<ApiResponse<Void>> unsavePoolByPlaceId(
            @AuthenticationPrincipal String userId,
            @PathVariable String placeId) {
        poolService.unsavePoolByPlaceId(userId, placeId);
        return ResponseEntity.ok(ApiResponse.success(null, "즐겨찾기에서 제거되었습니다."));
    }

    @Operation(summary = "내 즐겨찾기", description = "저장된 수영장 목록을 조회합니다.")
    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<PageResponse<SwimmingPoolResponse>>> getSavedPools(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SwimmingPool> pools = poolService.getSavedPools(userId, pageable);
        Page<SwimmingPoolResponse> responses = pools.map(pool -> {
            SwimmingPoolResponse response = SwimmingPoolResponse.from(pool);
            response.setIsSaved(true);
            return response;
        });
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }
}
