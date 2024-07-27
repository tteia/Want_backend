package com.example.want.api.project.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.dto.ProjectCreateReqDto;
import com.example.want.api.project.dto.ProjectResDto;
import com.example.want.api.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

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
