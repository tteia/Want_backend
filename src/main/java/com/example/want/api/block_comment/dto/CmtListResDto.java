package com.example.want.api.block_comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmtListResDto {
    private Long blockId;
    private Long commentId;
    private Long userID;

    // List<Cmt> children
}
