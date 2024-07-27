package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Block;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreatBlockRqDto {
    private String title;
    private String content;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String startTime;
    private String endTime;

    public Block toEntity(Double latitude, Double longitude, LocalDateTime startTime, LocalDateTime endTime) {
        return Block.builder()
                .title(this.title)
                .content(this.content)
                .placeName(this.placeName)
                .latitude(latitude)
                .longitude(longitude)
                .startTime(startTime)
                .endTime(endTime)
                .isActivated("N")
                .heartCount(0L)
                .build();
    }
}
