package com.example.want.block.service;

import com.example.want.block.domain.Block;
import com.example.want.block.dto.BlockActiveListRsDto;
import com.example.want.block.dto.BlockDetailRsDto;
import com.example.want.block.dto.CreatBlockRqDto;
import com.example.want.block.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;

    @Transactional
    public Block createBlock(CreatBlockRqDto request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);
        return blockRepository.save(request.toEntity(request.getLatitude(), request.getLongitude(), startTime, endTime));
    }

    public Page<BlockActiveListRsDto> getNotActiveBlockList(Pageable pageable) {
        Page<Block> block = blockRepository.findAllByIsActivated("N", pageable);
        return block.map(BlockActiveListRsDto::new);
    }

    public Page<BlockActiveListRsDto> getActiveBlockList(Pageable pageable) {
        Page<Block> block = blockRepository.findAllByIsActivated("Y", pageable);
        return block.map(BlockActiveListRsDto::new);
    }

    public BlockDetailRsDto getBlockDetail(Long id) {
        Block block = blockRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        return block.toDetailDto();
    }
}
