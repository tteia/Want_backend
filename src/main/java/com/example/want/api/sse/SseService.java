package com.example.want.api.sse;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>(); // map<userId, SseEmitter>
    private final SseController sseController;
    private final TravelUserRepository travelUserRepository;
    private final MemberRepository memberRepository;

    public SseEmitter subscribe(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // timeout 설정
        emitters.put(member.getId(), emitter); // userId에 해당하는 emitter를 저장

        emitter.onCompletion(() -> emitters.remove(member.getId())); // emitter가 종료되면 emitters에서 제거
        emitter.onTimeout(() -> emitters.remove(member.getId())); // emitter가 timeout되면 emitters에서 제거
        emitter.onError(e -> emitters.remove(member.getId())); // emitter가 에러가 발생하면 emitters에서 제거

        return emitter;
    }

    public void sendMessageToProject(Long projectId, String message) {
        List<TravelUser> travelUsers = travelUserRepository.findByProjectId(projectId);

        for (TravelUser travelUser : travelUsers) {
            Long userId = travelUser.getMember().getId();
            sendMessage(userId, message);
        }
    }

    private void sendMessage(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}
