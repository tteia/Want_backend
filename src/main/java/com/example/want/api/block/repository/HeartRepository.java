package com.example.want.placeBlock.Repository;

import com.example.want.placeBlock.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    // 특정 블록의 좋아요 수를 카운트하는 메서드
    long countByBlock_BlockId(Long blockId);
}
