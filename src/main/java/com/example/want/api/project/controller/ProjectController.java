package com.example.want.api.project.controller;

import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.project.service.ProjectService;
import com.example.want.api.traveluser.dto.LeaderDto;
import com.example.want.common.CommonResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //  출발 일자 수정
    @PutMapping("/update/{id}/startTravel")
    public ResponseEntity<Object> projectStartUpdate(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        projectService.updateStartTravel(id, dto.getStartTravel());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "project is found", "new StartTravel is " + dto.getStartTravel());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //  종료 일자 수정
    @PutMapping("/update/{id}/endTravel")
    public ResponseEntity<Object> projectEndUpdate(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        projectService.updateEndTravel(id, dto.getEndTravel());
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "project is found", "new EndTravel is " + dto.getEndTravel());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    
    // 일정 삭제
    // 삭제도 leader가 탈퇴 or 삭제를 하게 되면 일정이 삭제가 되어야 해서
    // 지금은 testLeaderId를 설정해서 테스트 진행했습니다.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        Long testLeaderId = 1L;
        projectService.deleteProject(id, testLeaderId);
        return new ResponseEntity<>("Project has been logically deleted.", HttpStatus.OK);
    }
}