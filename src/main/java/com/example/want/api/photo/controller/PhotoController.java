package com.example.want.api.photo.controller;


import com.example.want.api.photo.dto.PhotoListRsDto;
import com.example.want.api.photo.service.PhotoService;
import com.example.want.common.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/photo")
public class PhotoController {

    private final PhotoService photoService;

    // 사진 S3 업로드 및 DB 저장
    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> photoUpload(@RequestPart Long blockId, @RequestPart(value = "files", required = false) List<MultipartFile> files){
        // 입력된 파일의 개수가 10개 이하인지 판별
        if (files.size() > 10) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.BAD_REQUEST, "There are too many files to upload", null), HttpStatus.BAD_REQUEST);
        }
        try {
            List<PhotoListRsDto.PhotoInfoDto> infoDtos = new ArrayList<>();
            for (MultipartFile file : files){
                PhotoListRsDto.PhotoInfoDto photoInfoDto = photoService.uploadFile(blockId, file);  // 이미지 파일 id, url
                infoDtos.add(photoInfoDto);
            }
            PhotoListRsDto photoListRsDto = PhotoListRsDto.builder()
                    .blockId(blockId)
                    .photoList(infoDtos)
                    .build();
            return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Successfully uploaded " + files.size() + " files.", photoListRsDto), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload files.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // block별 사진 리스트
    @GetMapping("/{blockId}/list")
    public ResponseEntity<?> photoList(@PathVariable Long blockId){
        PhotoListRsDto dto = photoService.photoList(blockId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Files are successfully found.", dto), HttpStatus.OK);
    }

    // 사진 업데이트
    // 1 ) 해당 block에 저장되어 있던 파일명들을 리스트로 받아와 bucket에서 삭제 + photo 데이터 전체 삭제
    // 2 ) newFiles 업로드 및 저장
    @PutMapping("/update")
    public ResponseEntity<?> updatePhotos(@RequestParam Long blockId,
                                          @RequestParam("oldFiles") List<String> oldFileUrls,
                                          @RequestParam("newFiles") List<MultipartFile> newFiles) {

        // 입력된 파일의 개수가 10개 이하인지 판별
        if (newFiles.size() > 10) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.BAD_REQUEST, "There are too many files to upload", null), HttpStatus.BAD_REQUEST);
        }
        try {
            PhotoListRsDto photoListRsDto = photoService.updateFiles(blockId, oldFileUrls, newFiles);
            return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Successfully uploaded " + newFiles.size() + " files.", photoListRsDto), HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload files.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

