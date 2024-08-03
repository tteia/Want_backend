package com.example.want.api.project.dto;

import com.example.want.api.project.domain.Project;
import com.example.want.api.state.domain.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreateResDto {
    private String projectTitle;
    private LocalDate startTravel;
    private LocalDate endTravel;
    private StateResDto state;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StateResDto {
        private String country;
        private String city;

        public static StateResDto fromEntity(State state) {
            return StateResDto.builder()
                    .country(state.getCountry())
                    .city(state.getCity())
                    .build();
        }
    }
    public static ProjectCreateResDto fromEntity(Project project, State state) {
        return ProjectCreateResDto.builder()
                .projectTitle(project.getTitle())
                .startTravel(project.getStartTravel())
                .endTravel(project.getEndTravel())
                .state(StateResDto.fromEntity(state))
                .build();
    }
}
