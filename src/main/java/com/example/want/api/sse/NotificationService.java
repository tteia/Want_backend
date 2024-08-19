package com.example.want.api.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
public class NotificationService {

    // 프로젝트별로 SSE Emitters를 관리 (Set 사용)
    private final Map<Long, Set<SseEmitter>> projectEmitters = new ConcurrentHashMap<>();
    // 멤버별로 프로젝트에 대한 구독을 관리 (Map 사용)
    private final Map<Long, Map<Long, SseEmitter>> memberSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> inviteEmitters = new ConcurrentHashMap<>();


    // 프로젝트별 SSE 연결을 추가
    public SseEmitter addEmitter(Long projectId, Long memberId) {
        // 기존의 emitter가 있으면 제거
        SseEmitter existingEmitter = memberSubscriptions
                .computeIfAbsent(projectId, k -> new ConcurrentHashMap<>())
                .remove(memberId);

        if (existingEmitter != null) {
            // 기존 emitter를 projectEmitters에서 제거
            Set<SseEmitter> emitters = projectEmitters.get(projectId);
            if (emitters != null) {
                emitters.remove(existingEmitter);
            }
        }

        // 새 emitter 생성
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);

        // 새 emitter를 projectEmitters에 추가
        projectEmitters.computeIfAbsent(projectId, k -> new CopyOnWriteArraySet<>()).add(newEmitter);

        // 새 emitter를 memberSubscriptions에 추가
        memberSubscriptions.computeIfAbsent(projectId, k -> new ConcurrentHashMap<>()).put(memberId, newEmitter);

        newEmitter.onCompletion(() -> {
            // 완료 시 리소스 정리
            Set<SseEmitter> emitters = projectEmitters.get(projectId);
            if (emitters != null) {
                emitters.remove(newEmitter);
            }
            Map<Long, SseEmitter> subscriptions = memberSubscriptions.get(projectId);
            if (subscriptions != null) {
                subscriptions.remove(memberId);
            }
            log.info("SseEmitter for project {} and member {} completed.", projectId, memberId);
        });

        newEmitter.onTimeout(() -> {
            // 타임아웃 시 리소스 정리
            Set<SseEmitter> emitters = projectEmitters.get(projectId);
            if (emitters != null) {
                emitters.remove(newEmitter);
            }
            Map<Long, SseEmitter> subscriptions = memberSubscriptions.get(projectId);
            if (subscriptions != null) {
                subscriptions.remove(memberId);
            }
            log.info("SseEmitter for project {} and member {} timed out.", projectId, memberId);
        });

        newEmitter.onError(e -> {
            // 에러 발생 시 리소스 정리
            Set<SseEmitter> emitters = projectEmitters.get(projectId);
            if (emitters != null) {
                emitters.remove(newEmitter);
            }
            Map<Long, SseEmitter> subscriptions = memberSubscriptions.get(projectId);
            if (subscriptions != null) {
                subscriptions.remove(memberId);
            }
            log.error("SseEmitter for project {} and member {} encountered an error.", projectId, memberId, e);
        });

        // 첫 연결 시 클라이언트에게 연결 메시지 전송
        try {
            newEmitter.send(SseEmitter.event().name("connected").data("연결되었습니다!"));
        } catch (Exception e) {
            log.error("Failed to send connection message to member {} of project {}.", memberId, projectId, e);
        }

        log.info("SseEmitter connected for project {} and member {}.", projectId, memberId);

        return newEmitter;
    }

    // 특정 프로젝트의 모든 멤버에게 알림 전송
    public void sendNotificationToProject(Long projectId, String message) {
        Set<SseEmitter> emitters = projectEmitters.get(projectId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (Exception e) {
                    // 에러 발생 시 emitter 제거
                    emitters.remove(emitter);
                    log.error("Failed to send notification to project {}.", projectId, e);
                }
            }
        }
    }

    public SseEmitter subscribeEmitter(String email) {
        SseEmitter existingEmitter = inviteEmitters.remove(email);
        if (existingEmitter != null) {
            return existingEmitter;
        }

        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        inviteEmitters.put(email, newEmitter);

        newEmitter.onCompletion(() -> {
            inviteEmitters.remove(email);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
            log.info("SseEmitter for email {} completed.", email);
        });

        newEmitter.onTimeout(() -> {
            inviteEmitters.remove(email);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!2");
            log.info("SseEmitter for email {} timed out.", email);
        });

        newEmitter.onError(e -> {
            inviteEmitters.remove(email);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!3");
            log.error("SseEmitter for email {} encountered an error.", email, e);
        });

        try {
            newEmitter.send(SseEmitter.event().name("subscribed").data("구독되었습니다!"));
        } catch (Exception e) {
            log.error("Failed to send connection message to email {}.", email, e);
        }

        log.info("SseEmitter connected for email {}.", email);

        return newEmitter;
    }

    public void sendInvitation(String email, String message) {
        SseEmitter emitter = inviteEmitters.get(email);
        if (emitter != null) {
            try {
                String jsonData = new ObjectMapper().writeValueAsString(Collections.singletonMap("message", message));
                emitter.send(SseEmitter.event().name("invite").data(jsonData));            } catch (Exception e) {
                inviteEmitters.remove(email);
                log.error("Failed to send invitation to email {}.", email, e);
            }
        }
    }

}
