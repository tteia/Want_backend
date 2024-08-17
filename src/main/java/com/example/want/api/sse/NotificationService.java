package com.example.want.api.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class NotificationService {

    // 프로젝트별로 SSE Emitters를 관리 (Set 사용)
    private final Map<Long, Set<SseEmitter>> projectEmitters = new ConcurrentHashMap<>();

    // 프로젝트별 SSE 연결을 추가
    public SseEmitter addEmitter(Long projectId, Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        projectEmitters.computeIfAbsent(projectId, k -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> {
            projectEmitters.get(projectId).remove(emitter);
        });

        emitter.onTimeout(() -> {
            projectEmitters.get(projectId).remove(emitter);
        });

        emitter.onError(e -> {
            projectEmitters.get(projectId).remove(emitter);
        });

        return emitter;
    }

    // 특정 프로젝트의 모든 멤버에게 알림 전송
    public void sendNotificationToProject(Long projectId, String message) {
        Set<SseEmitter> emitters = projectEmitters.get(projectId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (Exception e) {
                    emitters.remove(emitter);
                }
            }
        }
    }
}

