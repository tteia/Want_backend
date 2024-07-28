package com.example.want.api.photo.controller;

import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.service.PhotoService;
import com.example.want.api.photo.service.S3Uploader;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/photo")
public class PhotoController {

    private final S3Uploader s3Uploader;
    private final PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(CreatePhotoRqDto dto) throws IOException {
        String url = s3Uploader.uploadFile(dto.getFile());
        dto.setUrl(url);
        photoService.save(dto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "photo is successfully uploaded", url), HttpStatus.CREATED);
    }
}
