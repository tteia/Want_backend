package com.example.want.api.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCreateReqDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
}
