package com.example.want.api.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    // 연결된 클라이언트의 SSE Emitters를 관리
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 새로운 SSE 연결을 추가
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);  // 연결 시간을 무제한으로 설정
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));  // 완료되면 제거
        emitter.onTimeout(() -> emitters.remove(emitter));     // 타임아웃 시 제거
        emitter.onError(e -> emitters.remove(emitter));        // 에러 발생 시 제거
        return emitter;
    }

    // 메시지를 모든 연결된 클라이언트에게 전송
    public void sendNotification(String message) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (Exception e) {
                emitters.remove(emitter);  // 에러 발생 시 해당 Emitter를 제거
            }
        });
    }
}
