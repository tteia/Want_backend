package com.example.want.block.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockDetailRsDto {
    private Long blockId;
    private String title;
    private String content;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String startTime;
    private String endTime;
    private String isActivated;
    private Long Heart;

}
