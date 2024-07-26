package com.example.want.api.block.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockByDateResDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
}
