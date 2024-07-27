package com.example.want.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("admin"),
    MEMBER("user");

    private final String value;
}
