package com.example.want.api.block.controller;

import com.example.want.api.block.dto.HeartListResDto;
import com.example.want.api.block.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/heart")
public class HeartController {
    private final HeartService heartService;

    // 좋아요 수에 따라 인기 명소를 반환하는 메서드
    @GetMapping("/popular")
    public ResponseEntity<List<HeartListResDto>> popularBlocks(@PageableDefault(size = 10) Pageable pageable) {
        List<HeartListResDto> heartList = heartService.sortByPopularAttractions(pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity<>(heartList, HttpStatus.OK);
    }
}
