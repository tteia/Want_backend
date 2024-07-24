package com.example.want.block_comment.dto;

import com.example.want.block_comment.entity.Cmt;
import com.example.want.user.domain.Member;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCmtRqDto {
    private Long memberId;
    private String contents;

    // Cmt parent

    public Cmt toEntity(){
        return Cmt.builder()
                .contents(this.contents)
                .build();
    }

}
