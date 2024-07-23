package com.example.want.block_comment.repository;

import com.example.want.block_comment.entity.Cmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CmtRepository extends JpaRepository<Cmt, Long> {

}
