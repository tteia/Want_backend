package com.example.want.placeBlock.service;

import com.example.want.placeBlock.Repository.BlockRepository;
import com.example.want.placeBlock.domain.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartService {
    @Autowired
    private BlockRepository blockRepository;

    // 좋아요 수에 따라 블록을 정렬하여 반환하는 메서드
    public List<Block> sortByPopularAttractions() {
        return blockRepository.findAllByOrderByHeartDesc();
    }
}
