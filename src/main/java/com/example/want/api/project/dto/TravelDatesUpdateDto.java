package com.example.want.api.project.dto;

import com.example.want.api.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelDatesUpdateDto {
    private LocalDate startTravel;
    private LocalDate endTravel;
}
