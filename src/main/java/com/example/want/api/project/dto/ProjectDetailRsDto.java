package com.example.want.api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDetailRsDto {
    private Long projectId;
    private String projectTitle;
    private LocalDate startTravel;
    private LocalDate endTravel;
    private List<ProjectMemberList> projectMembers = new ArrayList<>();
    private List<ProjectStateList> projectStates = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectMemberList {
        private Long userId;
        private String userName;
        private String userProfile;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectStateList {
        private Long stateId;
        private String country;
        private String city;
    }

}
