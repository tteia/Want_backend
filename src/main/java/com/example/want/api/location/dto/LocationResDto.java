package com.example.want.api.location.dto;

import com.example.want.api.block.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResDto {
    private Double latitude;
    private Double longitude;
    private Long popularCount;
    private String placeName;
    private Category category;
}
