package com.example.want.api.block.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetDateBlockRqDto {
    private Long blockId;
    private LocalDate planDate;
    private LocalTime planStartTime;
    private LocalTime planEndTime;
    private String planDescription;
}
