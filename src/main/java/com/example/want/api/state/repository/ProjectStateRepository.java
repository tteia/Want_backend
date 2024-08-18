package com.example.want.api.state.repository;

import com.example.want.api.state.domain.ProjectState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStateRepository extends JpaRepository<ProjectState, Long> {
    // State ID로 프로젝트 수를 세는 메서드
    Long countByStateId(Long stateId);
}