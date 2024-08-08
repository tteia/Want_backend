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
public class ProjectCreateReqDto {
    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;
    private StateReqDto state;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StateReqDto {
        private String country;
        private String city;
    }
//    여행지
//    private StateTravel stateTravel;

    public Project toEntity() {
        return Project.builder()
                .title(title)
                .startTravel(startTravel)
                .endTravel(endTravel)
                .build();
    }
}
