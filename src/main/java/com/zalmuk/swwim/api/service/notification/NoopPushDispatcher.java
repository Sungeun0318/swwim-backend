package com.zalmuk.swwim.api.service.notification;

import com.zalmuk.swwim.api.entity.notification.Notification;
import org.springframework.stereotype.Component;

@Component
public class NoopPushDispatcher implements PushDispatcher {

    @Override
    public void dispatch(Notification notification) {
        // TODO Phase 2: APNs/FCM 외부 푸시 발송 구현.
    }
}
