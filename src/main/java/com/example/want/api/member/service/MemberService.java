package com.example.want.api.member.service;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.InvitationDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TravelUserRepository travelUserRepository;
    private final ProjectRepository projectRepository;

    public Member getMyProfile(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

    }

    public List<InvitationDto> getMyInvitations(String email) {
//        사용자의 email을 이용하여 traveluser에서 List를 가져옴
        List<TravelUser> travelUsers = travelUserRepository.findByMemberEmail(email);
        List<InvitationDto> invitationDtos = new ArrayList<>();

        for (TravelUser travelUser : travelUsers) {
            Project project = projectRepository.findById(travelUser.getProject().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Project Not found"));
            InvitationDto invitationDto = InvitationDto.builder()
                    .projectId(project.getId())
                    .projectTitle(project.getTitle())
                    .invitationAccepted(travelUser.getInvitationAccepted())
                    .build();
            invitationDtos.add(invitationDto);
        }
        return invitationDtos;
    }
}
