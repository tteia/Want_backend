package com.example.want.api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResDto {
    private Long id;
    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;
    private LocalDateTime createdAt;
    private String travelGroupName;
//    private StateTravel stateTravel;
}