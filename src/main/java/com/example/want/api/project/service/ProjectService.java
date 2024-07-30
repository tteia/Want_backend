package com.example.want.api.project.service;

import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.MyProjectListRsDto;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectDetailRsDto;
import com.example.want.api.project.dto.TravelDatesUpdateDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.projectMember.Repository.ProjectMemberRepository;
import com.example.want.api.projectMember.domain.ProjectMember;
import com.example.want.api.state.domain.ProjectState;
import com.example.want.api.state.domain.State;
import com.example.want.api.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final StateRepository stateRepository;


    //    일정 생성, 팀원 목록 생성 (일정 생성자가 리더가 됨)
    @Transactional
    public Project createProject(ProjectCreateReqDto dto, String email) {
        Member member = findMemberByEmail(email);
//        일정 생성
        Project project = dto.toEntity();
        project = projectRepository.save(project);
//        리더 생성
        ProjectMember projectMember = createProjectLeader(member, project);
//        지역 생성
        Project finalProject = project;
        List<ProjectState> projectStates = createProjectStates(dto.getStateList(), finalProject);
        project.getProjectStates().addAll(projectStates);

//        프로젝트의 팀원 목록 리스트에 travelUser(리더)를 add
//        검증 코드 없이는 계속 null 에러 나서 넣었더니 됐습니다
        project.getProjectMembers().add(projectMember);
        project.initializeFields();
        return project;
    }
    private ProjectMember createProjectLeader(Member member, Project project) {
        return ProjectMember.builder()
                .member(member)
                .authority(Authority.LEADER)
                .invitationAccepted("Y")
                .project(project)
                .build();
    }

    private List<ProjectState> createProjectStates(List<ProjectCreateReqDto.StateListDto> stateListDtos, Project project) {
        return stateListDtos.stream()
                .map(stateDto -> {
                    State state = stateRepository.findById(stateDto.getStateId())
                            .orElseThrow(() -> new EntityNotFoundException("State not found"));
                    return ProjectState.builder()
                            .state(state)
                            .project(project)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 일정 수정
    @Transactional
    public void updateTravelDates(Long projectId, TravelDatesUpdateDto travelDatesUpdateDto, String email) {
        Member member = findMemberByEmail(email);
        Project project = findProjectById(projectId);
        if (project.getProjectMembers().stream().noneMatch(projectMember -> projectMember.getMember().equals(member))) {
            throw new IllegalArgumentException("프로젝트에 접근할수있는 유저가 아닙니다.");
        }

        Project updatedProject = project.toBuilder()
                .startTravel(travelDatesUpdateDto.getStartTravel())
                .endTravel(travelDatesUpdateDto.getEndTravel())
                .build();

        projectRepository.save(updatedProject);
    }

    // 제목 수정
    public void updateTitle(Long projectId, String newTitle, String email) {
        Member member = findMemberByEmail(email);
        Project project = findProjectById(projectId);
        if (project.getProjectMembers().stream().noneMatch(projectMember -> projectMember.getMember().equals(member))) {
            throw new IllegalArgumentException("프로젝트에 접근할수있는 유저가 아닙니다.");
        }

        Project updateProject = project.toBuilder()
                .title(newTitle)
                .build();
        projectRepository.save(updateProject);
    }

    // 일정 삭제
    @Transactional
    public void deleteProject(Long projectId, String email) {
        Project project = findProjectById(projectId);
        Member member = findMemberByEmail(email);

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
    public void inviteUser (Long projectId, String email) {
//        초대할 member 객체
        Member member = findMemberByEmail(email);
        Project project = findProjectById(projectId);
        if (project.getProjectMembers().stream().noneMatch(projectMember -> projectMember.getMember().equals(member))) {
            throw new IllegalArgumentException("프로젝트에 접근할수있는 유저가 아닙니다.");
        }

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
        Member member = findMemberByEmail(email);
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

    public ProjectDetailRsDto getProjectDetail(Long projectId, String email) {
        Member member = findMemberByEmail(email);
        Project project =  findProjectById(projectId);
        if (project.getProjectMembers().stream().noneMatch(projectMember -> projectMember.getMember().equals(member))) {
            throw new IllegalArgumentException("프로젝트에 접근할수있는 유저가 아닙니다.");
        }

        return ProjectDetailRsDto.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .startTravel(project.getStartTravel())
                .endTravel(project.getEndTravel())
                .projectMembers(project.getProjectMembers().stream()
                        .map(projectMember -> ProjectDetailRsDto.ProjectMemberList.builder()
                                .userId(projectMember.getMember().getId())
                                .userName(projectMember.getMember().getName())
                                .userProfile(projectMember.getMember().getProfileUrl())
                                .build())
                        .collect(Collectors.toList()))
                .projectStates(project.getProjectStates().stream()
                        .map(projectState -> ProjectDetailRsDto.ProjectStateList.builder()
                                .stateId(projectState.getState().getId())
                                .country(projectState.getState().getCountry())
                                .city(projectState.getState().getCity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }



    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }
    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }


}
