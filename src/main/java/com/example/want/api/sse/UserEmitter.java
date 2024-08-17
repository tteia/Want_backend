package com.example.want.api.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

public class UserEmitter {

    private final Long memberId;
    private final SseEmitter emitter;

    public UserEmitter(Long memberId, SseEmitter emitter) {
        this.memberId = memberId;
        this.emitter = emitter;
    }

    public Long getMemberId() {
        return memberId;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEmitter that = (UserEmitter) o;
        return Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}

