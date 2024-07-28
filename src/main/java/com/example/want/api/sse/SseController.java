package com.example.want.api.sse;

import com.example.want.api.member.login.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

//    sse를 구독하는 클라이언트에게 sseEmitter를 반환
    @GetMapping("/sse/subsribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserInfo userInfo) {
        SseEmitter sseEmitter = sseService.subscribe(userInfo.getEmail());
        return sseEmitter;
    }


}
