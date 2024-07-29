package com.example.want.api.heart.dto;

import com.example.want.api.block.domain.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartListResDto {
    private Long blockId;
    private String blockName;
    private int heartCount;

    public static HeartListResDto fromEntity(Block block) {
        return HeartListResDto.builder()
                .blockId(block.getId())
                .blockName(block.getTitle())
                .heartCount(block.getHeartCount().intValue())
                .build();
    }
}
