package com.example.want.api.block.domain;

import com.example.want.api.block.dto.BlockDetailRsDto;
import com.example.want.api.block.dto.SetDateBlockRqDto;
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
    @Enumerated(EnumType.STRING)
    private Category category;

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

    public BlockDetailRsDto toDetailDto() {
        return BlockDetailRsDto.builder()
                .blockId(this.id)
                .title(this.title)
                .content(this.content)
                .placeName(this.placeName)
                .category(this.category)
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

    // 등록된 일정을 블럭에 삽입
    public void updatePlan(SetDateBlockRqDto dto) {
        this.planDate = dto.getPlanDate();
        this.planStartTime = dto.getPlanStartTime();
        this.planEndTime = dto.getPlanEndTime();
    }
}
