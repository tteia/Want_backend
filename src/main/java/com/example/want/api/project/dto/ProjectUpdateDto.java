package com.example.want.api.project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectUpdateDto {
    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;
//    private StateTravel stateTravel;
}

