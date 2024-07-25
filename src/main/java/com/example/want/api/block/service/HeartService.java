package com.example.want.api.block.service;

import com.example.want.api.block.dto.HeartListResDto;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.block.domain.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeartService {
    @Autowired
    private BlockRepository blockRepository;

    // 좋아요 수에 따라 블록을 정렬하여 반환하는 메서드
    public List<HeartListResDto> sortByPopularAttractions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Block> blocks = blockRepository.findAllByHeartDesc("Y", pageable);

        return blocks.getContent().stream()
                .map(HeartListResDto::fromEntity)
                .collect(Collectors.toList());
    }
}
