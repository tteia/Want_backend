package com.example.want.api.photo.service;

import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final PhotoRepository photoRepository;


    public void save(CreatePhotoRqDto createPhotoRqDto) {
        Photo photo = new Photo(createPhotoRqDto.getTitle(), createPhotoRqDto.getUrl());
        photoRepository.save(photo);
    }

    public List<Photo> getFiles() {
        List<Photo> all = photoRepository.findAll();
        return all;
    }
}
