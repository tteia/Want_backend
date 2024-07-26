package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlockRqDto {
    private String title;
    private String content;
    private String placeName;
    private Double latitude;
    private Double longitude;

    // Plan 관련 필드 추가
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;

    public Block toBlockEntity() {
        LocalDateTime startDateTime = LocalDateTime.of(this.date, this.startTime);
        LocalDateTime endDateTime = LocalDateTime.of(this.date, this.endTime);

        return Block.builder()
                .title(this.title)
                .content(this.content)
                .placeName(this.placeName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .startTime(startDateTime)
                .endTime(endDateTime)
                .isActivated("N")
                .heartCount(0L)
                .build();
    }
}
