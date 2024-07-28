package com.example.want.api.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitationDto {
    private Long projectId;
    private String projectTitle;
    private String email;
    private String invitationAccepted;
}
