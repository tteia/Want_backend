package com.example.want.api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateDto {
    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;
}

