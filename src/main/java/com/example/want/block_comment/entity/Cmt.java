package com.example.want.block_comment.entity;

import com.example.want.block_comment.dto.CmtListResDto;
import com.example.want.block_comment.dto.CmtUpdateDto;
import com.example.want.common.BaseEntity;
import com.example.want.user.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cmt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private Long blockId;   // 연관된 block의 id
    private Long memberId;    // 작성한 member의 id

    @Column(length = 200)
    private String contents;

//    private Long relatedCommentId;    // 대댓글일 경우, 연관된 댓글의 id

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;

    // 자기 참조
    @ManyToOne(fetch = FetchType.LAZY)
    private Cmt parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Cmt> children;

    // 댓글 삭제 여부
    private String isDeleted;


    public void updateCmt(CmtUpdateDto dto){
        this.contents = dto.getContents();
    }

    public void deleteCmt(String isDeleted){
        this.isDeleted = isDeleted;
    }

    public CmtListResDto listFromEntity(){
        return CmtListResDto.builder()

                .build();
    }
}
