package com.example.want.api.heart.heartController;


import com.example.want.api.heart.dto.HeartListResDto;
import com.example.want.api.heart.service.HeartService;
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

    // 좋아요 수를 내림차순으로 조회 (인기 순)
    @GetMapping("/popular")
    public ResponseEntity<List<HeartListResDto>> popularBlocks(@PageableDefault(size = 10) Pageable pageable) {
        List<HeartListResDto> heartList = heartService.blocksByPopular(pageable);
        return new ResponseEntity<>(heartList, HttpStatus.OK);
    }
}

