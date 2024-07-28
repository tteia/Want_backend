package com.example.want.api.traveluser.Repository;

import com.example.want.api.member.domain.Member;
import com.example.want.api.project.domain.Project;
import com.example.want.api.traveluser.domain.TravelUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
    Page<TravelUser> findAll(Pageable pageable);
    Optional<TravelUser> findById(Long id);

    boolean existsByProjectAndMember(Project project, Member member);


    Optional<TravelUser> findByProjectAndMember(Project project, Member member);
  
    @Query("SELECT tu.project FROM TravelUser tu WHERE tu.member = :member AND tu.project.isDeleted = 'N'")
    Page<Project> findActiveProjectByMember(@Param("member") Member member, Pageable pageable);

    List<TravelUser> findByProjectId(Long projectId);
}
