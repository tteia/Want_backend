package com.example.want.api.photo.domain;

import com.example.want.api.block.domain.Block;
import com.example.want.api.photo.dto.PhotoListRsDto;
import com.example.want.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    public PhotoListRsDto.PhotoInfoDto FromEntity() {
        return PhotoListRsDto.PhotoInfoDto.builder()
                .id(this.photoId)
                .url(this.photoUrl)
                .build();
    }
}
