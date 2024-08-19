package com.example.want.api.project.repository;

import com.example.want.api.project.domain.Project;
import com.example.want.api.state.domain.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findAll(Pageable pageable);

    Optional<Project> findById(Long id);

    Optional<Project> findByTitle(String title);

    List<Project> findByIsDeleted(String n);

    List<Project> findByProjectStatesState(State state);
}