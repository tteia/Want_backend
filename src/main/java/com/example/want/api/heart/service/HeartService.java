package com.example.want.api.heart.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.heart.dto.HeartListResDto;
import com.example.want.api.block.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeartService {
    @Autowired
    private BlockRepository blockRepository;

    // 좋아요 수에 따라 블록을 정렬하여 반환하는 메서드
    public List<HeartListResDto> blocksByPopular(Pageable pageable) {
        Page<Block> blocks = blockRepository.findByIsActivatedOrderByHeartCountDesc("Y", pageable);
        List<HeartListResDto> result = new ArrayList<>();
        blocks.getContent().forEach(block -> result.add(HeartListResDto.fromEntity(block)));
        return result;
    }

}
