package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.domain.Category;
import com.example.want.api.project.domain.Project;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateBlockRqDto {
    private Category category;
    private Long projectId;

    public Block toEntity(Category category, Project project) {
        return Block.builder()
                .title("제목을 입력해주세요")
                .content("내용을 입력해주세요")
                .category(category)
                .project(project)
                .heartCount(0L)
                .isActivated("N")
                .build();
    }
}