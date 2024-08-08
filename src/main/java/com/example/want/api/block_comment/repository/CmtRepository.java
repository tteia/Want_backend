package com.example.want.api.block_comment.repository;

import com.example.want.api.block_comment.domain.Cmt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmtRepository extends JpaRepository<Cmt, Long> {
    Page<Cmt> findByBlockIdAndIsDeleted(Pageable pageable, Long blockId, String isDeleted);
}
