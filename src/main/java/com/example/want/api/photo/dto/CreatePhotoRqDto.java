package com.example.want.api.photo.dto;

import com.example.want.api.block.domain.Block;
import com.example.want.api.photo.domain.Photo;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePhotoRqDto {
    private Long blockId;
    private String title;
    private String url;
    private MultipartFile file;

}
