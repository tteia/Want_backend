package com.example.want.api.block_comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmtRsDto {
    private Long commentId;
    private Long blockId;
    private String memberName;
    private String memberProfile;
    private String contents;
    private LocalDateTime createdTime;

}
