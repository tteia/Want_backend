package com.example.want.api.projectMember.Repository;

import com.example.want.api.member.domain.Member;
import com.example.want.api.project.domain.Project;
import com.example.want.api.projectMember.domain.ProjectMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Page<ProjectMember> findAll(Pageable pageable);
    Optional<ProjectMember> findById(Long id);
    Optional<ProjectMember> findByMemberEmail(String email);

    boolean existsByProjectAndMember(Project project, Member member);


    Optional<ProjectMember> findByProjectAndMember(Project project, Member member);
  
    @Query("SELECT tu.project FROM ProjectMember tu WHERE tu.member = :member AND tu.project.isDeleted = 'N' AND tu.invitationAccepted='Y'")
    Page<Project> findActiveProjectByMember(@Param("member") Member member, Pageable pageable);

    Page<ProjectMember> findByMemberEmail(String email, Pageable pageable);

    Optional<ProjectMember> findByMemberAndProjectId(Member member, Long projectId);

    List<ProjectMember> findByProject(Project project);
}
