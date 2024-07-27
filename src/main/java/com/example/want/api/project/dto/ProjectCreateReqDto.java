package com.example.want.api.project.dto;

import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import com.example.want.api.traveluser.dto.LeaderDto;
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
    private LeaderDto leaderDto;
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
