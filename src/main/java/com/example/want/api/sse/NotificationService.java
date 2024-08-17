package com.example.want.api.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class NotificationService {

    // 프로젝트별로 사용자 정보와 SSE Emitters를 관리 (Set 사용)
    private final Map<Long, Set<UserEmitter>> projectEmitters = new ConcurrentHashMap<>();

    // 프로젝트별 SSE 연결을 추가
    public SseEmitter addEmitter(Long projectId, Long memberId) {
        Set<UserEmitter> emittersForProject = projectEmitters.computeIfAbsent(projectId, k -> new CopyOnWriteArraySet<>());

        // 이미 연결된 사용자가 있는지 확인
        for (UserEmitter userEmitter : emittersForProject) {
            if (userEmitter.getMemberId().equals(memberId)) {
                // 이미 연결된 경우, 새로운 Emitter를 생성하지 않고 null 반환
                return null;
            }
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        UserEmitter userEmitter = new UserEmitter(memberId, emitter);

        // 사용자별 연결 추가
        emittersForProject.add(userEmitter);

        // 등록 시와 연결이 끊어질 때, 및 에러 발생 시의 처리
        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed for project: " + projectId + ", Member: " + memberId);
            emittersForProject.remove(userEmitter);
        });
        emitter.onTimeout(() -> {
            System.out.println("SSE connection timed out for project: " + projectId + ", Member: " + memberId);
            emittersForProject.remove(userEmitter);
        });
        emitter.onError(e -> {
            System.err.println("SSE connection error for project: " + projectId + ", Member: " + memberId);
            e.printStackTrace();
            emittersForProject.remove(userEmitter);
        });

        try {
            emitter.send(SseEmitter.event().name("subscribe").data("Subscribed to project " + projectId));
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 연결 @@@@@@@@@@@@@@@@@@@@@@@ project: " + projectId + ", Member: " + memberId);
        } catch (Exception e) {
            emittersForProject.remove(userEmitter);
        }

        return emitter;
    }

    // 특정 프로젝트의 모든 멤버에게 알림 전송
    public void sendNotificationToProject(Long projectId, String message) {
        Set<UserEmitter> emitters = projectEmitters.get(projectId);
        if (emitters != null) {
            emitters.forEach(userEmitter -> {
                try {
                    userEmitter.getEmitter().send(SseEmitter.event().name("message").data(message));
                } catch (Exception e) {
                    // Remove emitter if an error occurs
                    emitters.remove(userEmitter);
                }
            });
        }
    }
}
