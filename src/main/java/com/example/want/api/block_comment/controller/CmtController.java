package com.example.want.api.block_comment.controller;

import com.example.want.api.block_comment.dto.CreateCmtRqDto;
import com.example.want.api.block_comment.dto.CmtRsDto;
import com.example.want.api.block_comment.dto.CmtUpdateRqDto;
import com.example.want.api.block_comment.entity.Cmt;
import com.example.want.api.block_comment.service.CmtService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping("/list/{blockId}")
    public ResponseEntity<Object> cmtList(@PathVariable Long blockId, @PageableDefault(size=10, sort="createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CmtRsDto> comments = cmtService.cmtList(pageable, blockId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Success", comments), HttpStatus.OK);
    }

    // 댓글 수정
    @PatchMapping("/update")
    public ResponseEntity<Object> UpdateComment(@RequestBody CmtUpdateRqDto dto) {
        cmtService.update(dto);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully updated", "comment id: " + dto.getCommentId()), HttpStatus.OK);
    }

    // 댓글 삭제
    // 화면 생성 후 리다이렉트
    @GetMapping("/delete/{id}")
    public ResponseEntity<Object> cmtDelete(@PathVariable Long id) {
        cmtService.delete(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "comment is successfully deleted", "comment id: " + id), HttpStatus.OK);
    }
}
