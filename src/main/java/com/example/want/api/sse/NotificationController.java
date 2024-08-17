package com.example.want.api.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // SSE 엔드포인트: 클라이언트가 이 엔드포인트에 연결하여 알림을 수신
    @GetMapping("/api/notifications")
    public SseEmitter streamNotifications() {
        return notificationService.addEmitter();
    }
}
