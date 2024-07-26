package com.example.want.api.block_comment.controller;

import com.example.want.api.block_comment.dto.CreateCmtRqDto;
import com.example.want.api.block_comment.dto.CmtResDto;
import com.example.want.api.block_comment.dto.CmtUpdateDto;
import com.example.want.api.block_comment.entity.Cmt;
import com.example.want.api.block_comment.service.CmtService;
import com.example.want.api.user.domain.Member;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Object> createBlockComment(@RequestBody CreateCmtRqDto dto, String email) {
        Cmt cmt = cmtService.create(dto, email);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "comment is successfully created", cmt), HttpStatus.CREATED);
    }

    // 댓글 조회
    @GetMapping("/list")
    public ResponseEntity<Object> cmtList() {
        List<CmtResDto> cmtList = cmtService.cmtList();
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", cmtList), HttpStatus.OK);
    }

    // 댓글 수정
    @GetMapping("/update")
    public ResponseEntity<Object> createBlocmtUpdateckComment(@RequestBody CmtUpdateDto dto) {
        cmtService.update(dto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully updated", null), HttpStatus.OK);
    }

    // 댓글 삭제
    // 화면 생성 후 리다이렉트
    @GetMapping("/delete")
    public ResponseEntity<Object> cmtDelete(@RequestBody Long id) {
        cmtService.delete(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully deleted", null), HttpStatus.OK);
    }
}
