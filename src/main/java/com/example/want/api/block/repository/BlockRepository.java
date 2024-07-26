package com.example.want.api.block.repository;
import com.example.want.api.block.domain.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Page<Block> findAllByIsActivatedOrderByStartTimeAsc(@Param("isActivated") String Y, Pageable pageable);
    Page<Block> findAllByIsActivatedOrderByHeartCountDesc (@Param("isActivated") String N, Pageable pageable);

}
