package com.example.want.block_comment.service;

import com.example.want.block_comment.dto.CmtListResDto;
import com.example.want.block_comment.dto.CmtUpdateDto;
import com.example.want.block_comment.dto.CreateCmtRqDto;
import com.example.want.block_comment.entity.Cmt;
import com.example.want.block_comment.repository.CmtRepository;
import com.example.want.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CmtService {

    private final CmtRepository cmtRepository;

    // Create
    @Transactional
    public Cmt create(CreateCmtRqDto dto){
        return cmtRepository.save(dto.toEntity());
    }

    // Read
    public List<CmtListResDto> cmtList(){
        List<Cmt> cmtList = cmtRepository.findAll();
		List<CmtListResDto> cmtListResDtos = new ArrayList<CmtListResDto>();
		for (Cmt c : cmtList) {
            cmtListResDtos.add(c.listFromEntity());
		}
        return cmtListResDtos;
    }


    // Update
    @Transactional
    public void update(CmtUpdateDto dto){
        Cmt cmt = cmtRepository.findById(dto.getCommentId()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 댓글 아이디입니다."));
        cmt.updateCmt(dto);
    }

    // Delete
    @Transactional
    public void delete(Long id){
        // isDeleted = 1로 변경
        Cmt cmt = cmtRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 아이디입니다."));
        cmt.deleteCmt("Y");
    }

}
