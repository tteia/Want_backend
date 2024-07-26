package com.example.want.api.block.domain;

import com.example.want.api.block.dto.BlockDetailRsDto;
import com.example.want.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Block extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String placeName;

    private Double latitude;
    private Double longitude;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String isActivated;
    private Long heartCount;

    // 선택한 날짜의 일정만 조회할 수 있는 plan 관련 데이터 추가.
    private LocalDate planDate;
    private LocalTime planStartTime;
    private LocalTime planEndTime;
    private String planDescription;

    public BlockDetailRsDto toDetailDto() {
        return BlockDetailRsDto.builder()
                .blockId(this.id)
                .title(this.title)
                .content(this.content)
                .placeName(this.placeName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .startTime(this.startTime.toString())
                .endTime(this.endTime.toString())
                .isActivated(this.isActivated)
                .heartCount(this.heartCount)
                .build();
    }

    public void incrementHearts() {
        this.heartCount++;
    }

    public void decrementHearts() {
        this.heartCount--;
    }
}
