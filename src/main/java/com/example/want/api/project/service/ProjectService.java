package com.example.want.api.project.service;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.MyProjectListRsDto;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.TravelDatesUpdateDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
import com.example.want.api.traveluser.dto.LeaderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TravelUserRepository travelUserRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TravelUserRepository travelUserRepository, MemberRepository memberRepository) {
        this.projectRepository = projectRepository;
        this.travelUserRepository = travelUserRepository;
        this.memberRepository = memberRepository;
    }

    //    일정 생성, 팀원 목록 생성 (일정 생성자가 리더가 됨)
    @Transactional
    public Project createProject(ProjectCreateReqDto dto) {
//        일정 생성
        Project project = dto.toEntity();
        project = projectRepository.save(project);

//        리더 : 프로젝트 생성, 프로젝트 삭제를 할 수 있는 역할 
//        따로 Dto를 만들었는데 혹시 다른 방법 있으면 말씀 해주시면 감사합니다.
//        프로젝트 생성 할 때 로그인 되어 있는 사용자의 아이디를 LeaderDto 객체에 저장
        LeaderDto leaderDto = dto.getLeaderDto();
        Long leaderId = leaderDto.getLeaderId();

//        리더 생성 -> leaderDto에서 받은 id로 member 객체를 찾아서 반환
        Member leader = memberRepository.findById(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("Member not Found"));

//        TravelUser 생성
        TravelUser travelUser = TravelUser.builder()
                .member(leader)
                .authority(Authority.LEADER)
                .invitationAccepted("Y")
                .project(project)
                .build();
        TravelUser savedUser = travelUserRepository.save(travelUser);


//        프로젝트의 팀원 목록 리스트에 travelUser(리더)를 add
//        검증 코드 없이는 계속 null 에러 나서 넣었더니 됐습니다
        if(project.getTravelUsers() == null || project.getTravelUsers().isEmpty()) {
            project.setTravelUsers(new ArrayList<>());
        }
        project.getTravelUsers().add(savedUser);

        project.initializeFields();
        return project;
    }

    // 일정 수정
    @Transactional
    public void updateTravelDates(Long projectId, TravelDatesUpdateDto travelDatesUpdateDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Project updatedProject = project.toBuilder()
                .startTravel(travelDatesUpdateDto.getStartTravel())
                .endTravel(travelDatesUpdateDto.getEndTravel())
                .build();

        projectRepository.save(updatedProject);
    }

    // 제목 수정
    public void updateTitle(Long id, String newTitle) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Project updateProject = project.toBuilder()
                .title(newTitle)
                .build();
        projectRepository.save(updateProject);
    }

    // 일정 삭제
    @Transactional
    public void deleteProject(Long projectId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        TravelUser travelUser = travelUserRepository.findByProjectAndMember(project, member)
                .orElseThrow(() -> new EntityNotFoundException("TravelUser not found"));

        if (travelUser.getAuthority() == Authority.LEADER) {
            project.delete();
            projectRepository.save(project);
        } else {
            travelUserRepository.delete(travelUser);
        }
    }

//    팀원 초대
    public void inviteUser (Long id, String email) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

//        초대할 member 객체
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));

//        멤버가 이미 팀원목록에 속해 있는지 확인하는 검증코드
        boolean existsMember = travelUserRepository.existsByProjectAndMember(project, member);
        if(existsMember) {
            throw new IllegalArgumentException("Member already exists.");
        }
        
        String invitationCode = UUID.randomUUID().toString();

        TravelUser travelUser = TravelUser.builder()
                .project(project)
                .member(member)
                .authority(Authority.MEMBER)
                .invitationAccepted("N") // 초대 수락을 하면 "Y"로 변경
                .invitationCode(invitationCode)
                .build();

        travelUserRepository.save(travelUser);
    }

    public Page<MyProjectListRsDto> getMyProjectList(Pageable pageable, String email) {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Page<MyProjectListRsDto> myProjectListRsDto = travelUserRepository.findActiveProjectByMember(member, pageable)
                .map(project -> MyProjectListRsDto.builder()
                        .projectId(project.getId())
                        .projectTitle(project.getTitle())
                        .startTravel(project.getStartTravel().toString())
                        .endTravel(project.getEndTravel().toString())
                        .travelUsers(project.getTravelUsers().stream()
                                .map(travelUser -> MyProjectListRsDto.MyProjectMember.builder()
                                        .userId(travelUser.getMember().getId())
                                        .userName(travelUser.getMember().getName())
                                        .userProfile(travelUser.getMember().getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
        return myProjectListRsDto;
    }
}
