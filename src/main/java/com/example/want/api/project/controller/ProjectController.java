package com.example.want.api.project.controller;

import com.example.want.api.member.login.UserInfo;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.*;
import com.example.want.api.project.service.ProjectService;
import com.example.want.api.traveluser.dto.LeaderDto;
import com.example.want.common.CommonResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //    일정 생성
    //    로그인 되어 있는 사용자의 id를 받아서 일정을 생성
    //    이 부분 로그인 기능 이용해서 해야 할거같은데 접근을 어떻게 해야 할지 모르겠어서 일단 이렇게 작성했습니다.
    @PostMapping("/create")
    public ResponseEntity<Object> projectCreate(@RequestBody ProjectCreateReqDto dto) {
        Long testLeaderId = 1L; // 실제로는 로그인된 사용자 ID를 사용해야 함
        LeaderDto leaderDto = LeaderDto.builder()
                .leaderId(testLeaderId)
                .build();
        dto.setLeaderDto(leaderDto);
        Project project = projectService.createProject(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "project is successfully created.", "project id is : " +  project.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    //    제목 수정
    @PutMapping("/update/{id}/title")
    public ResponseEntity<Object> projectTitleUpdate(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        projectService.updateTitle(id, dto.getTitle());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "project is found", "new Title is " + dto.getTitle());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 기간 수정
    @PutMapping("/update/{id}/travel-dates")
    public ResponseEntity<Object> updateTravelDates(
            @PathVariable Long id,
            @RequestBody TravelDatesUpdateDto dto) {
        projectService.updateTravelDates(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "project is found", dto.getStartTravel() + " - " + dto.getEndTravel());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

//    Leader : 탈퇴 시 동시에 프로젝트 삭제
//    Member : 탈퇴 시 Member 자신만 팀원 목록에서 삭제
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId, @AuthenticationPrincipal UserInfo userInfo) {
        projectService.deleteProject(projectId, userInfo.getEmail());
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

//    팀원 초대
    @PostMapping("/invite/{projectId}")
    public ResponseEntity<Object> inviteUser(@PathVariable Long projectId, @RequestBody InvitationDto dto) {
        projectService.inviteUser(projectId, dto.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Member invited successfully.", "Member Email : " + dto.getEmail());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getProjectList(@PageableDefault Pageable pageable, @AuthenticationPrincipal UserInfo userInfo) {
        Page<MyProjectListRsDto> myProjectListRsDto = projectService.getMyProjectList(pageable , userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", myProjectListRsDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }
}