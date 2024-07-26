package com.example.want.api.block_comment.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.block_comment.dto.CreateCmtRqDto;
import com.example.want.api.block_comment.dto.CmtResDto;
import com.example.want.api.block_comment.dto.CmtUpdateDto;
import com.example.want.api.block_comment.entity.Cmt;
import com.example.want.api.block_comment.repository.CmtRepository;
import com.example.want.api.user.domain.Member;
import com.example.want.api.user.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    // Create
    @Transactional
    public Cmt create(CreateCmtRqDto dto, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("member is not found"));
        Block block = blockRepository.findById(dto.getBlockId()).orElseThrow(()->new EntityNotFoundException("block is not found"));
        return cmtRepository.save( dto.toEntity(member, block));
    }

    // Read
    public List<CmtResDto> cmtList(){
        List<Cmt> cmtList = cmtRepository.findAll();
		List<CmtResDto> cmtListResDtos = new ArrayList<CmtResDto>();
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
