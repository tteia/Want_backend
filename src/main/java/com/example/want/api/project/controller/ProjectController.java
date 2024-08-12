package com.example.want.api.project.controller;

import com.example.want.api.member.login.UserInfo;
import com.example.want.api.project.dto.*;
import com.example.want.api.project.service.ProjectService;
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
    public ResponseEntity<Object> projectCreate(@RequestBody ProjectCreateReqDto dto, @AuthenticationPrincipal UserInfo userInfo ) {
        ProjectCreateResDto projectCreateResDto = projectService.createProject(dto, userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "project is successfully created.", projectCreateResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    //    제목 수정
    @PatchMapping("/{projectId}/update/title")
    public ResponseEntity<Object> projectTitleUpdate(@PathVariable Long projectId, @RequestBody ProjectTitleUpdateRqDto dto, @AuthenticationPrincipal UserInfo userInfo) {
        ProjectTitleUpdateRsDto response = projectService.updateTitle(projectId, dto.getTitle(), userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Project title is successfully updated.", response);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 기간 수정
    @PatchMapping("/{projectId}/update/travel-dates")
    public ResponseEntity<Object> updateTravelDates(
            @PathVariable Long projectId,
            @RequestBody ProjectDatesUpdateRqDto dto,
            @AuthenticationPrincipal UserInfo userInfo) {
        ProjectDatesUpdateRsDto response = projectService.updateTravelDates(projectId, dto, userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Project travel dates are successfully updated.", response);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

//    Leader : 탈퇴 시 동시에 프로젝트 삭제 및 팀원들도 삭제
//    Member : 탈퇴 시 Member 자신만 팀원 목록에서 삭제
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId, @AuthenticationPrincipal UserInfo userInfo) {
        projectService.deleteProject(projectId, userInfo.getEmail());
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

//    팀원 초대
    @PostMapping("/invite")
    public ResponseEntity<Object> inviteUser(@RequestBody InvitationDto dto, @AuthenticationPrincipal UserInfo userInfo) {
        projectService.inviteUser(dto.getProjectId(), dto.getEmail() , userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Member invited successfully.", "Member Email : " + dto.getEmail());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getProjectList(@PageableDefault Pageable pageable, @AuthenticationPrincipal UserInfo userInfo) {
        Page<MyProjectListRsDto> myProjectListRsDto = projectService.getMyProjectList(pageable , userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", myProjectListRsDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/{projectId}/detail")
    public ResponseEntity<?> getProjectDetail(@PathVariable Long projectId, @AuthenticationPrincipal UserInfo userInfo) {
        ProjectDetailRsDto projectDetailRsDto = projectService.getProjectDetail(projectId, userInfo.getEmail());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", projectDetailRsDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}