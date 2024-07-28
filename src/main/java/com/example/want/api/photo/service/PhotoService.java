package com.example.want.api.photo.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PhotoService {
    @Autowired
    private S3Uploader s3Uploader;
    private final PhotoRepository photoRepository;
    private final BlockRepository blockRepository;

    public void save(CreatePhotoRqDto createPhotoRqDto) {
        Photo photo = new Photo(createPhotoRqDto.getTitle(), createPhotoRqDto.getUrl());
        photoRepository.save(photo);
    }

    public List<Photo> getFiles() {
        List<Photo> all = photoRepository.findAll();
        return all;
    }




}
