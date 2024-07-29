package com.example.want.api.photo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRsDto {
    private Long photoId;
    private String photoUrl;
    private Long blockId;
}
