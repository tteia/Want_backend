package com.example.want.api.photo.domain;

import com.example.want.api.block.domain.Block;
import com.example.want.api.photo.dto.PhotoRsDto;
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

    public PhotoRsDto listFromEntity(Long blockId) {
        return PhotoRsDto.builder()
                .photoId(this.photoId)
                .photoUrl(this.photoUrl)
                .blockId(blockId)
                .build();
    }
}
