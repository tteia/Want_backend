package com.example.want.api.heart.repository;

import com.example.want.api.block.domain.Block;
import com.example.want.api.heart.domain.Heart;
import com.example.want.api.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByMemberAndBlock(Member member, Block block);

    boolean existsByMemberAndBlock(Member member, Block block);
}
