package com.example.want.block_comment.controller;

import com.example.want.block_comment.dto.CmtListResDto;
import com.example.want.block_comment.dto.CmtUpdateDto;
import com.example.want.block_comment.dto.CreateCmtRqDto;
import com.example.want.block_comment.service.CmtService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/block_comment")
public class CmtController {

    private final CmtService cmtService;

    // 댓글 생성
    @PostMapping("/create")
    public ResponseEntity<Object> createBlockComment(@RequestBody CreateCmtRqDto dto) {
        cmtService.create(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "comment is successfully created", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 댓글 조회
    @GetMapping("/list")
    public CommonResDto cmtList(){
        List<CmtListResDto> cmtList = cmtService.cmtList();
        return new CommonResDto(HttpStatus.CREATED, "ok", cmtList);
    }


    // 댓글 수정
    @GetMapping("/update")
    public String cmtUpdate(@RequestBody CmtUpdateDto dto){
        cmtService.update(dto);
        return "ok";
    }


    // 댓글 삭제
    @GetMapping("/delete")
    public String cmtDelete(@RequestBody Long id){
        cmtService.delete(id);
        return "ok";
    }
}
