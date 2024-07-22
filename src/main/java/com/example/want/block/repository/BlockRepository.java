package com.example.want.block.repository;

import com.example.want.block.domain.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Page<Block> findAllByIsActivated(String y, Pageable pageable);
}
