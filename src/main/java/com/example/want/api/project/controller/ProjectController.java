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
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    //    일정 목록
    @GetMapping("/list")
    public ResponseEntity<Object> projectList(@PageableDefault(size = 10, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProjectResDto> projectList = projectService.projectList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "projects are found", projectList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //    일정 생성 창
    @GetMapping("/create")
    public String projectCreateScreen() {
        return "/project/project_register";
    }

    //    일정 생성
    @PostMapping("/create")
    public ResponseEntity<Object> projectCreate(@RequestBody ProjectCreateReqDto dto) {
        Project project = projectService.createProject(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "project is successfully created", project.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    //    일정 상세 보기
    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> projectDetail(@PathVariable Long id) {
        ProjectDetailResDto memberDetailResDto = projectService.projectDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "project is found", memberDetailResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //    일정 수정
    @PutMapping("/update/{id}")
    public String projectUpdate(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        projectService.update(id, dto);
        return "redirect:/project/detail/" + id;
    }

    //    일정 삭제
    @DeleteMapping("/delete/{id}")
    public String projectDelete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list";
    }

    //    프로젝트의 블럭 리스트 보기
    @GetMapping("/blocks/{id}")
    public ResponseEntity<Object> getBlocksByProject(@PathVariable Long id) {
        List<Block> blocks = projectService.getBlocksByProject(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "block list found success", blocks);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}