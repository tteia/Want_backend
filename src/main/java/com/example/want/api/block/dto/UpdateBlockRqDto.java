package com.example.want.api.block.dto;

import com.example.want.api.block.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBlockRqDto {

    private String title;

    private String content;

    private String placeName;
    private Double latitude;
    private Double longitude;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(value = EnumType.STRING)
    private Category category;

}
