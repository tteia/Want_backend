package com.example.want.api.project.controller;

import com.example.want.api.block.domain.Block;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectDetailResDto;
import com.example.want.api.project.dto.ProjectResDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.project.service.ProjectService;
import com.example.want.common.CommonResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //    일정 생성
    @PostMapping("/create")
    public ResponseEntity<Object> projectCreate(@RequestBody ProjectCreateReqDto dto) {
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
}