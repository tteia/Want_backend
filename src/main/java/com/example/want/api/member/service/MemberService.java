package com.example.want.api.member.service;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.dto.GetInvitationDto;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//    초대 요청 목록 조회
    public Page<GetInvitationDto> getMyInvitations(String email, Pageable pageable) {
//        사용자의 email을 이용하여 traveluser에서 List를 가져옴
        Page<TravelUser> travelUsers = travelUserRepository.findByMemberEmail(email, pageable);
        List<GetInvitationDto> getInvitationDtos = new ArrayList<>();

//        Page 객체는 페이징 정보를 포함하고 있으므로, 실제 순수 데이터를 리스트로 가져오려면
//        getContent() 메서드를 사용해야함
        for (TravelUser travelUser : travelUsers.getContent()) {
            Project project = projectRepository.findById(travelUser.getProject().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Project Not found"));

            GetInvitationDto getInvitationDto = GetInvitationDto.builder()
                    .projectId(project.getId())
                    .projectTitle(project.getTitle())
                    .invitationAccepted(travelUser.getInvitationAccepted())
                    .build();
            getInvitationDtos.add(getInvitationDto);
        }
//        PageImpl(List<T> content, Pageable pageable, long total)
        return new PageImpl<>(getInvitationDtos, pageable, travelUsers.getTotalElements());
    }

//    초대 요청 수락
    @Transactional
    public void acceptInvitation(String email, Long projectId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member Not found"));

//        member 객체를 먼저 찾은 후 member 객체와 projectId를 통해 TravelUser 리턴
        TravelUser travelUser = travelUserRepository.findByMemberAndProjectId(member, projectId)
                .orElseThrow(() -> new IllegalArgumentException("TravelUser Not found"));

//        이미 초대 수락을 했으면
        if (travelUser.getInvitationAccepted().equals("Y")) {
            throw new IllegalArgumentException("Invitation already accepted");
        }

        travelUser.setInvitationAccepted("Y");
        travelUserRepository.save(travelUser);
    }
}
