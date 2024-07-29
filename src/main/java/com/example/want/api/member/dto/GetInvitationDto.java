package com.example.want.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetInvitationDto {
    private Long projectId;
    private String projectTitle;
    private String invitationAccepted;
}
