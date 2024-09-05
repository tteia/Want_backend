package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.domain.Category;
import com.example.want.api.location.domain.Location;
import com.example.want.api.project.domain.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImportBlockRqDto {
    private Long projectId;
    private Location location;

    private Double latitude;
    private Double longitude;
    private String placeName;
    private Category category;

    public Block toImport(Location location, Project project) {
        return Block.builder()
                .category(location.getCategory())
                .project(project)
                .heartCount(0L)
                .isActivated("N")
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .placeName(location.getPlaceName())
                .build();
    }
}