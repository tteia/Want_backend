package com.example.want.api.block.domain;

import lombok.Getter;


@Getter
public enum Category {
    SPOT("관광지"),
    RESTAURANT("음식점"),
    CAFE("카페"),
    ETC("기타"),;


    private final String value;

    Category(String value) {
        this.value = value;
    }
    }
