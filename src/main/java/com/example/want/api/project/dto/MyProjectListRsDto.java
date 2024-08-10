package com.example.want.api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyProjectListRsDto {
    private Long projectId;
    private String projectTitle;
    private String startTravel;
    private String endTravel;
    private String createdTime;
    private List<MyProjectMember> travelUsers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MyProjectMember {
        private Long userId;
        private String userName;
        private String userProfile;
    }
}

