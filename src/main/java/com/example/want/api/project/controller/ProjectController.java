package com.example.want.api.project.controller;

import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //    일정 목록
    @GetMapping("/project/list")
    public String projectList(Model model, @PageableDefault(size = 10, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("projectList", projectService.projectList(pageable));
        return "project/project_list";
    }

    //    일정 생성 창
    @GetMapping("/project/create")
    public String projectCreateScreen() {
        return "/project/project_register";
    }

    //    일정 생성
    @PostMapping("/project/create")
    public String projectCreate(ProjectCreateReqDto dto) {
        projectService.createProject(dto);
        return "redirect:/project/list";
    }

    //    일정 상세 보기
    @GetMapping("/project/detail/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.projectDetail(id));
        return "project/project_detail";
    }

    //    일정 수정
    @PostMapping("/project/update/{id}")
    public String projectUpdate(@PathVariable Long id,
                                @ModelAttribute ProjectUpdateDto dto,
                                Model model) {
        projectService.update(id, dto);
        return "redirect:/project/detail/" + id;
    }

    //    일정 삭제
    @GetMapping("/project/delete/{id}")
    public String projectDelete(@PathVariable Long id, Model model) {
        projectService.deleteProject(id);
        return "redirect:/project/list";
    }
}