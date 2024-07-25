package com.example.want.placeBlock.controller;

import com.example.want.placeBlock.domain.Block;
import com.example.want.placeBlock.dto.HeartListResDto;
import com.example.want.placeBlock.service.HeartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/heart")
@RestController
public class HeartController {
    @Autowired
    private HeartService heartService;

    // 좋아요 수에 따라 인기 명소를 반환하는 메서드
    @GetMapping("/popular")
    public List<Block> popularBlocks(HeartListResDto heartListResDto) {
        List<Block> blockList = new ArrayList<>();
        for (Block block : blockList) {
            heartListResDto.add(block.listFromEntity());
        }
        return heartService.sortByPopularAttractions();
    }
}