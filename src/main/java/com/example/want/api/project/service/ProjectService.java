package com.example.want.api.project.service;

import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectResDto;
import com.example.want.api.project.dto.ProjectUpdateDto;
import com.example.want.api.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    //    일정 생성
    @Transactional
    public Project createProject(ProjectCreateReqDto dto) {
        Project project = dto.toEntity();
        return projectRepository.save(project);
    }

    //    일정 상세 보기
    public ProjectResDto projectDetail(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 일정이 없습니다."));
        return project.detFromEntity();
    }

    //    일정 삭제
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    //    일정 수정
    @Transactional
    public void update(Long id, ProjectUpdateDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 일정이 없습니다."));
        project.updateProject(dto);
    }

    //    일정 목록
    public Page<ProjectResDto> projectList(Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(pageable);
        Page<ProjectResDto> projectResDtos = projects.map(a->a.listFromEntity());
        return projectResDtos;
    }
}
