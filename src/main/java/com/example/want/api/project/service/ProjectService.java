package com.example.want.api.project.service;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.MyProjectListRsDto;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.TravelDatesUpdateDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.projectMember.Repository.ProjectMemberRepository;
import com.example.want.api.projectMember.domain.ProjectMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, MemberRepository memberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.memberRepository = memberRepository;
    }

    //    일정 생성, 팀원 목록 생성 (일정 생성자가 리더가 됨)
    @Transactional
    public Project createProject(ProjectCreateReqDto dto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not Found"));
//        일정 생성
        Project project = dto.toEntity();
        project = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder()
                .member(member)
                .authority(Authority.LEADER)
                .invitationAccepted("Y")
                .project(project)
                .build();

//        프로젝트의 팀원 목록 리스트에 travelUser(리더)를 add
//        검증 코드 없이는 계속 null 에러 나서 넣었더니 됐습니다
        project.getProjectMembers().add(projectMember);
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

        ProjectMember projectMember = projectMemberRepository.findByProjectAndMember(project, member)
                .orElseThrow(() -> new EntityNotFoundException("TravelUser not found"));

        if (projectMember.getAuthority() == Authority.LEADER) {
            project.delete();

            List<ProjectMember> projectMembers = projectMemberRepository.findByProject(project);
            for (ProjectMember pm : projectMembers) {
                pm.updateIsExist("N");
            }
            projectRepository.save(project);

        } else {
            projectMember.updateIsExist("N");
            projectMemberRepository.save(projectMember);
        }
    }

//    팀원 초대
    public void inviteUser (Long id, String email) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

//        초대할 member 객체
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));

//        멤버가 이미 팀원목록에 속해 있는지 확인하는 검증코드
        boolean existsMember = projectMemberRepository.existsByProjectAndMember(project, member);
        if(existsMember) {
            throw new IllegalArgumentException("Member already exists.");
        }
        
        String invitationCode = UUID.randomUUID().toString();

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(member)
                .authority(Authority.MEMBER)
                .invitationAccepted("N") // 초대 수락을 하면 "Y"로 변경
                .invitationCode(invitationCode)
                .build();

        projectMemberRepository.save(projectMember);
    }

    public Page<MyProjectListRsDto> getMyProjectList(Pageable pageable, String email) {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Page<MyProjectListRsDto> myProjectListRsDto = projectMemberRepository.findActiveProjectByMember(member, pageable)
                .map(project -> MyProjectListRsDto.builder()
                        .projectId(project.getId())
                        .projectTitle(project.getTitle())
                        .startTravel(project.getStartTravel().toString())
                        .endTravel(project.getEndTravel().toString())
                        .travelUsers(project.getProjectMembers().stream()
                                .map(projectMember -> MyProjectListRsDto.MyProjectMember.builder()
                                        .userId(projectMember.getMember().getId())
                                        .userName(projectMember.getMember().getName())
                                        .userProfile(projectMember.getMember().getProfileUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
        return myProjectListRsDto;
    }
}
