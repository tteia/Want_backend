package com.example.want.api.block_comment.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.block_comment.dto.CreateCmtRqDto;
import com.example.want.api.block_comment.dto.CmtRsDto;
import com.example.want.api.block_comment.dto.CmtUpdateRqDto;
import com.example.want.api.block_comment.domain.Cmt;
import com.example.want.api.block_comment.repository.CmtRepository;
import com.example.want.api.member.domain.Member;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.projectMember.Repository.ProjectMemberRepository;
import com.example.want.api.projectMember.domain.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CmtService {

    private final CmtRepository cmtRepository;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    // Create
    @Transactional
    public Cmt create(CreateCmtRqDto dto, String email){
        Member member = findMemberByEmail(email);
        Block block = findBlockById(dto.getBlockId());
        return cmtRepository.save( dto.toEntity(member, block));
    }

    // Read
    public Page<CmtRsDto> cmtList(Pageable pageable, Long blockId, String email){
        Member member = findMemberByEmail(email);
        Block block = findBlockById(blockId);
        ProjectMember projectMember = checkProjectMember(block, member);
        Page<Cmt> cmts = cmtRepository.findByBlockIdAndIsDeleted(pageable, blockId, "N");
        Page<CmtRsDto> dtos = cmts.map(a -> a.listFromEntity());
        return dtos;
    }

    // Update
    @Transactional
    public void update(CmtUpdateRqDto dto, String email){
        Member member = findMemberByEmail(email);
        Cmt cmt = cmtRepository.findById(dto.getCommentId()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 댓글 아이디입니다."));
        Block block = cmt.getBlock();
        ProjectMember projectMember = checkProjectMember(block, member);
        cmt.updateCmt(dto.getContents());
    }

    // Delete
    @Transactional
    public void delete(Long commentId, String email){
        Member member = findMemberByEmail(email);
        Cmt cmt = cmtRepository.findById(commentId).orElseThrow(()->new EntityNotFoundException("존재하지 않는 댓글 아이디입니다."));
        Block block = cmt.getBlock();
        ProjectMember projectMember = checkProjectMember(block, member);
        // 댓글 삭제
        cmt.deleteCmt("Y");
    }

    public ProjectMember checkProjectMember(Block block, Member member){
        return projectMemberRepository.findByProjectAndMember(block.getProject(), member).orElseThrow(()->new EntityNotFoundException("project member is not found"));
    }

    public Member findMemberByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
    }
    public Block findBlockById(Long blockId){
        return blockRepository.findById(blockId).orElseThrow(()->new EntityNotFoundException("block is not found"));
    }

}
