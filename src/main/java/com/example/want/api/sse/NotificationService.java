package com.example.want.api.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final Map<Long, Map<Long, SseEmitter>> projectMemberEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> inviteEmitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter addEmitter(Long projectId, Long memberId) {
        removeExistingEmitter(projectId, memberId);
        SseEmitter newEmitter = createNewEmitter();
        registerNewEmitter(projectId, memberId, newEmitter);
        setupEmitterCallbacks(projectId, memberId, newEmitter);
        sendConnectionMessage(newEmitter, "연결되었습니다!");
        log.info("SseEmitter connected for project {} and member {}.", projectId, memberId);
        return newEmitter;
    }

    private void removeExistingEmitter(Long projectId, Long memberId) {
        projectMemberEmitters.computeIfPresent(projectId, (k, members) -> {
            members.remove(memberId);
            return members.isEmpty() ? null : members;
        });
    }

    private SseEmitter createNewEmitter() {
        return new SseEmitter(Long.MAX_VALUE);
    }

    private void registerNewEmitter(Long projectId, Long memberId, SseEmitter newEmitter) {
        projectMemberEmitters
                .computeIfAbsent(projectId, k -> new ConcurrentHashMap<>())
                .put(memberId, newEmitter);
    }

    private void setupEmitterCallbacks(Long projectId, Long memberId, SseEmitter emitter) {
        emitter.onCompletion(() -> removeEmitter(projectId, memberId, "completed"));
        emitter.onTimeout(() -> removeEmitter(projectId, memberId, "timed out"));
        emitter.onError(e -> removeEmitter(projectId, memberId, "encountered an error"));
    }

    private void removeEmitter(Long projectId, Long memberId, String reason) {
        Map<Long, SseEmitter> members = projectMemberEmitters.get(projectId);
        if (members != null) {
            members.remove(memberId);
            if (members.isEmpty()) {
                projectMemberEmitters.remove(projectId);  // 명시적으로 projectId 제거
            }
        }
        log.info("SseEmitter for project {} and member {} {}.", projectId, memberId, reason);
    }

    private void sendConnectionMessage(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("connected").data(message));
        } catch (Exception e) {
            log.error("Failed to send connection message.", e);
        }
    }

    public void sendNotificationToProject(Long projectId, String message) {
        Map<Long, SseEmitter> projectEmitters = projectMemberEmitters.get(projectId);
        if (projectEmitters != null) {
            projectEmitters.values().forEach(emitter -> sendMessage(emitter, "message", message));
        }
    }

    public SseEmitter subscribeEmitter(String email) {
        return inviteEmitters.compute(email, (k, existingEmitter) -> {
            if (existingEmitter != null) {
                log.info("Existing emitter found for email {}. Returning it.", email);
                return existingEmitter;
            }

            SseEmitter newEmitter = createNewEmitter();
            setupInviteEmitterCallbacks(email, newEmitter);
            sendConnectionMessage(newEmitter, "구독되었습니다!");
            log.info("New SseEmitter connected for email {}.", email);
            return newEmitter;
        });
    }

    private void setupInviteEmitterCallbacks(String email, SseEmitter emitter) {
        emitter.onCompletion(() -> removeInviteEmitter(email, "completed"));
        emitter.onTimeout(() -> removeInviteEmitter(email, "timed out"));
        emitter.onError(e -> removeInviteEmitter(email, "encountered an error"));
    }

    void removeInviteEmitter(String email, String reason) {
        inviteEmitters.computeIfPresent(email, (k, emitter) -> {
            log.info("SseEmitter for email {} {}.", email, reason);
            return null;  // This removes the entry
        });
    }

    public void sendInvitation(String email, String message) {
        SseEmitter emitter = inviteEmitters.get(email);
        if (emitter != null) {
            sendMessage(emitter, "invite", Map.of("message", message));
        }
    }

    private void sendMessage(SseEmitter emitter, String eventName, Object data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            emitter.send(SseEmitter.event().name(eventName).data(jsonData));
        } catch (Exception e) {
            log.error("Failed to send message for event {}.", eventName, e);
        }
    }
}