package com.example.want.api.photo.dto;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoListRsDto {
    private Long blockId;
    private List<PhotoInfoDto> photoList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhotoInfoDto{
        private Long id;
        private String url;
    }
}
