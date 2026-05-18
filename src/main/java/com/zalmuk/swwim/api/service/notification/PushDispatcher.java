package com.zalmuk.swwim.api.service.notification;

import com.zalmuk.swwim.api.entity.notification.Notification;

/**
 * 외부 푸시 발송 인터페이스.
 *
 * Phase 2에서 APNs/FCM 구현체를 연결한다.
 */
public interface PushDispatcher {

    void dispatch(Notification notification);
}
