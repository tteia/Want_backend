package com.example.want.api.block_comment.controller;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.dto.CreatBlockRqDto;
import com.example.want.api.block_comment.dto.CreateCmtRqDto;
import com.example.want.api.block_comment.dto.CmtListResDto;
import com.example.want.api.block_comment.dto.CmtUpdateDto;
import com.example.want.api.block_comment.entity.Cmt;
import com.example.want.api.block_comment.service.CmtService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/block_comment")
public class CmtController {

    private final CmtService cmtService;

    // 댓글 생성
    @PostMapping("/create")
    public ResponseEntity<Object> createBlockComment(@RequestBody CreateCmtRqDto dto, String email, Long blockId) {
        Cmt cmt = cmtService.create(dto, email, blockId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully created", cmt), HttpStatus.OK);
    }


    // 댓글 조회
    @GetMapping("/list")
    public ResponseEntity<Object> cmtList() {
        List<CmtListResDto> cmtList = cmtService.cmtList();
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", cmtList), HttpStatus.OK);
    }


    // 댓글 수정
    @GetMapping("/update")
    public ResponseEntity<Object> createBlocmtUpdateckComment(@RequestBody CmtUpdateDto dto) {
        cmtService.update(dto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully updated", null), HttpStatus.OK);
    }


    // 댓글 삭제
    @GetMapping("/delete")
    public ResponseEntity<Object> cmtDelete(@RequestBody Long id) {
        cmtService.delete(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully deleted", null), HttpStatus.OK);
    }
}
