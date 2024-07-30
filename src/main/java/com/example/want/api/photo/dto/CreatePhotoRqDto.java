package com.example.want.api.photo.dto;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block_comment.domain.Cmt;
import com.example.want.api.member.domain.Member;
import com.example.want.api.photo.domain.Photo;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePhotoRqDto {
    private Long blockId;
    private String url;

    public Photo toEntity(Block block){
        return Photo.builder()
                .photoUrl(url)
                .block(block)
                .build();
    }
}
