package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.service.user.EntitlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * RevenueCat Webhook 수신 컨트롤러
 * https://www.revenuecat.com/docs/integrations/webhooks
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final EntitlementService entitlementService;

    @Value("${revenuecat.webhook.auth-key:}")
    private String webhookAuthKey;

    /**
     * RevenueCat Webhook 수신
     * 이벤트 타입:
     * - INITIAL_PURCHASE: 최초 구매
     * - RENEWAL: 자동 갱신
     * - PRODUCT_CHANGE: 상품 변경
     * - CANCELLATION: 구독 취소 (만료 시점에 해지)
     * - UNCANCELLATION: 취소 철회
     * - EXPIRATION: 구독 만료
     * - BILLING_ISSUE: 결제 실패
     * - SUBSCRIBER_ALIAS: 사용자 alias 변경
     */
    @PostMapping("/revenuecat")
    public ResponseEntity<Map<String, Object>> handleRevenueCatWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> payload) {

        // Authorization 헤더 검증 (설정된 경우)
        if (webhookAuthKey != null && !webhookAuthKey.isEmpty()) {
            if (authorization == null || !authorization.equals("Bearer " + webhookAuthKey)) {
                log.warn("[Webhook] 인증 실패: Authorization 헤더 불일치");
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
            }
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = (Map<String, Object>) payload.get("event");
            if (event == null) {
                log.warn("[Webhook] event 필드 없음. payload={}", payload);
                return ResponseEntity.ok(Map.of("success", true));
            }

            String eventType = (String) event.get("type");
            String appUserId = (String) event.get("app_user_id");
            String environment = (String) event.get("environment"); // SANDBOX or PRODUCTION
            String store = (String) event.get("store");              // APP_STORE / PLAY_STORE
            String productId = (String) event.get("product_id");
            String eventId = (String) event.get("id");

            log.info("[Webhook] RevenueCat 이벤트 수신: type={}, env={}, store={}, " +
                            "productId={}, userId={}, eventId={}",
                    eventType, environment, store, productId, appUserId, eventId);

            // SANDBOX 환경 이벤트는 명확히 표시
            if ("SANDBOX".equals(environment)) {
                log.info("[Webhook] 🧪 SANDBOX 환경 이벤트 처리 중");
            }

            if (appUserId == null || appUserId.startsWith("$RCAnonymousID:")) {
                log.warn("[Webhook] 유효하지 않은 userId (익명): {}", appUserId);
                return ResponseEntity.ok(Map.of("success", true));
            }

            switch (eventType) {
                case "INITIAL_PURCHASE":
                case "RENEWAL":
                case "UNCANCELLATION":
                    handlePurchaseOrRenewal(appUserId, event);
                    break;

                case "EXPIRATION":
                case "BILLING_ISSUE":
                    handleExpiration(appUserId, event);
                    break;

                case "CANCELLATION":
                    // 취소는 만료 시점까지는 프리미엄 유지 (즉시 해지 아님)
                    log.info("[Webhook] 구독 취소 (만료까지 유지): userId={}", appUserId);
                    break;

                case "PRODUCT_CHANGE":
                    handlePurchaseOrRenewal(appUserId, event);
                    break;

                case "TEST":
                    // RevenueCat 대시보드의 "Send Test Event" 버튼으로 발생
                    log.info("[Webhook] ✅ TEST 이벤트 수신 — webhook 연결 정상");
                    break;

                default:
                    log.info("[Webhook] 처리하지 않는 이벤트: {} (userId={})", eventType, appUserId);
                    break;
            }

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("[Webhook] 처리 실패: {}", e.getMessage(), e);
            // RevenueCat은 200이 아니면 재시도하므로, 파싱 에러는 200 반환
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 구매/갱신 처리 - premium entitlement 부여
     */
    private void handlePurchaseOrRenewal(String userId, Map<String, Object> event) {
        try {
            LocalDateTime expiresAt = parseExpirationDate(event);
            String productId = (String) event.get("product_id");
            String environment = (String) event.get("environment");

            // entitlement 목록에서 premium 확인
            @SuppressWarnings("unchecked")
            List<String> entitlementIds = (List<String>) event.get("entitlement_ids");

            entitlementService.grantFromRevenueCat(userId, EntitlementService.PREMIUM, expiresAt);
            log.info("[Webhook] ✅ premium 부여: userId={}, productId={}, expiresAt={}, " +
                            "env={}, entitlement_ids={}",
                    userId, productId, expiresAt, environment, entitlementIds);
        } catch (IllegalArgumentException e) {
            log.warn("[Webhook] ❌ 사용자 없음: userId={}", userId);
        } catch (Exception e) {
            log.error("[Webhook] ❌ premium 부여 실패: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 만료/결제실패 처리 - premium entitlement 취소
     */
    private void handleExpiration(String userId, Map<String, Object> event) {
        try {
            String productId = (String) event.get("product_id");
            String environment = (String) event.get("environment");
            entitlementService.revokeEntitlement(userId, EntitlementService.PREMIUM);
            log.info("[Webhook] 🚫 premium 취소: userId={}, productId={}, env={}",
                    userId, productId, environment);
        } catch (Exception e) {
            log.warn("[Webhook] premium 취소 실패: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 만료일 파싱 (밀리초 타임스탬프)
     */
    private LocalDateTime parseExpirationDate(Map<String, Object> event) {
        Object expirationAtMs = event.get("expiration_at_ms");
        if (expirationAtMs != null) {
            long ms;
            if (expirationAtMs instanceof Number) {
                ms = ((Number) expirationAtMs).longValue();
            } else {
                ms = Long.parseLong(expirationAtMs.toString());
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of("Asia/Seoul"));
        }
        // 만료일 없으면 null (무기한)
        return null;
    }
}
