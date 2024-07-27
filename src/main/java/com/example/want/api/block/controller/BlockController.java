package com.example.want.api.block.controller;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.dto.BlockActiveListRsDto;
import com.example.want.api.block.dto.BlockDetailRsDto;
import com.example.want.api.block.dto.CreateBlockRqDto;
import com.example.want.api.block.dto.SetDateBlockRqDto;
import com.example.want.api.block.service.BlockService;
import com.example.want.api.heart.dto.HeartListResDto;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/block")
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/create")
    public ResponseEntity<Object> createBlock(@RequestBody CreateBlockRqDto request) {
        Block block = blockService.createBlock(request);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", block), HttpStatus.OK);
    }

    @GetMapping("/dis/active/list")
    public ResponseEntity<Object> getNotActiveBlockList(@PageableDefault(size = 10) Pageable pageable) {
        Page<BlockActiveListRsDto> blockList = blockService.getNotActiveBlockList(pageable);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", blockList), HttpStatus.OK);
    }

    @GetMapping("/active/list")
    public ResponseEntity<Object> getBlockList(@PageableDefault(size = 10) Pageable pageable) {
        Page<BlockActiveListRsDto> blockList = blockService.getActiveBlockList(pageable);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", blockList), HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> getBlock(@PathVariable Long id) {
        BlockDetailRsDto block = blockService.getBlockDetail(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", block), HttpStatus.OK);
    }

    // 좋아요수 증가
    @PostMapping("/{blockId}/heart")
    public ResponseEntity<Object> addLikeToPost(@PathVariable Long blockId, @RequestBody String memberEmail) {
        blockService.addLikeToPost(blockId, memberEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{blockId}/hearts")
    public ResponseEntity<Object> getHeartCount(@PathVariable Long blockId) {
        Long heartCount = blockService.getLikesCount(blockId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", heartCount), HttpStatus.OK);
    }

    // 좋아요 수를 내림차순으로 조회 (인기 순)
    @GetMapping("/popular")
    public ResponseEntity<?> popularBlocks(@PageableDefault(size = 10) Pageable pageable) {
        Page<HeartListResDto> heartList = blockService.activeBlocksByPopular(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", heartList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 날짜별 Block 조회
    @GetMapping("/{date}")
    public ResponseEntity<?> getBlocksByDate(@PathVariable String date, @PageableDefault(size = 5) Pageable pageable) {
        LocalDate localDate = LocalDate.parse(date);
        Page<Block> blocks = blockService.getBlocksByDate(localDate, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", blocks);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // Block 일정 등록 -> 끌어다놓기
    @PostMapping("/setDate")
    public ResponseEntity<CommonResDto> setDateBlock(@RequestBody SetDateBlockRqDto setBlockRqDto) {
        Block updatedBlock = blockService.setDateBlock(setBlockRqDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Success", updatedBlock);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


}
