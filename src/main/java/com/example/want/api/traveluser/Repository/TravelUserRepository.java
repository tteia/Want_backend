package com.example.want.api.traveluser.Repository;

import com.example.want.api.member.domain.Member;
import com.example.want.api.project.domain.Project;
import com.example.want.api.traveluser.domain.TravelUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
    Page<TravelUser> findAll(Pageable pageable);
    Optional<TravelUser> findById(Long id);

    boolean existsByProjectAndMember(Project project, Member member);

    Optional<TravelUser> findByProjectAndMember(Project project, Member member);

    List<TravelUser> findByMemberEmail(String email);
}
