package com.example.want.api.project.dto;

import com.example.want.api.block.domain.Block;
import com.example.want.api.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDetailResDto {
    private Long id;
    private String title;
    private LocalDate startTravel;
    private LocalDate endTravel;
    private String stateTravel;
    private List<Member> memberList;
    private List<Block> blockList;
}
