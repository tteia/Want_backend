package com.example.want.api.project.service;

import com.example.want.api.project.domain.Authority;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
import com.example.want.api.traveluser.dto.LeaderDto;
import com.example.want.api.user.domain.Member;
import com.example.want.api.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;

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

        return project;
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

    // 출발 일자 수정
    public void updateStartTravel(Long id, LocalDate newStartTravel) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Project updateProject = project.toBuilder()
                .startTravel(newStartTravel)
                .build();
        projectRepository.save(updateProject);
    }

    // 종료 일자 수정
    public void updateEndTravel(Long id, LocalDate newEndTravel) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Project updateProject = project.toBuilder()
                .startTravel(newEndTravel)
                .build();
        projectRepository.save(updateProject);
    }
}
