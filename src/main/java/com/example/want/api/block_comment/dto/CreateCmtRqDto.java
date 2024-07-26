package com.example.want.api.block_comment.dto;

import com.example.want.api.block_comment.entity.Cmt;
import com.example.want.api.user.domain.Member;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCmtRqDto {
//    private Long memberId;
    private Long blockId;
    private String contents;


    public Cmt toEntity(Member member){
        return Cmt.builder()
                .member(member)
                .contents(this.contents)
                .build();
    }

}
