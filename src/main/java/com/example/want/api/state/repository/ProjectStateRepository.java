package com.example.want.api.state.repository;

import com.example.want.api.project.domain.Project;
import com.example.want.api.state.domain.ProjectState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStateRepository extends JpaRepository<ProjectState, Long> {
    ProjectState findByProject(Project project);
    Long countByStateId(Long stateId);
}