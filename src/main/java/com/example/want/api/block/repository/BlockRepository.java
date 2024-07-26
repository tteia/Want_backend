package com.example.want.api.block.repository;

import com.example.want.api.block.domain.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Page<Block> findAllByIsActivated(String y, Pageable pageable);
    Page<Block> findByIsActivatedOrderByHeartCountDesc(String isActivated, Pageable pageable);
    // Plan 관련 메서드
    Page<Block> findByDateOrderByStartTimeAsc(LocalDate date);
}
