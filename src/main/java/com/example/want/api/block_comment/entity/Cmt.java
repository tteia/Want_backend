package com.example.want.api.block_comment.entity;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block_comment.dto.CmtRsDto;
import com.example.want.api.block_comment.dto.CmtUpdateRqDto;
import com.example.want.api.member.domain.Member;
import com.example.want.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cmt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 200)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

//    // 자기 참조
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Cmt parent;
//
//    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
//    private List<Cmt> children;

    // 댓글 삭제 여부
    @Builder.Default
    private String isDeleted = "N";

//    public Cmt(Block block, Member member, String contents) {
//        block = this.block;
//        member = this.member;
//        contents = this.contents;
//    }

    public void updateCmt(CmtUpdateRqDto dto){
        this.contents = dto.getContents();
    }

    public void deleteCmt(String isDeleted){
        this.isDeleted = isDeleted;
    }

    public CmtRsDto listFromEntity(){
        return CmtRsDto.builder()
                .commentId(this.commentId)
                .blockId(this.block.getId())
                .memberId(this.member.getId())
                .contents(this.contents)
                .createdTime(this.getCreatedTime())
                .build();
    }
}
