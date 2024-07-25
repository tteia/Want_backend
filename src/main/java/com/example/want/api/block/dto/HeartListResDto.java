package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Block;

public class HeartListResDto {
    private String blockName; // 필요한 블록의 정보만 반환
    private int heartCount;

    public HeartListResDto(String blockName, int heartCount) {
        this.blockName = blockName;
        this.heartCount = heartCount;
    }

    public static HeartListResDto fromEntity(Block block) {
        return new HeartListResDto(block.getTitle(), block.getHeart().intValue());
    }

    // Getter 메서드 추가
    public String getBlockName() {
        return blockName;
    }

    public int getHeartCount() {
        return heartCount;
    }
}
