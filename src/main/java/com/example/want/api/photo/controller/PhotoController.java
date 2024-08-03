package com.example.want.api.photo.controller;

import com.example.want.api.photo.dto.CreatePhotoRqDto;

import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.service.PhotoService;
import com.example.want.common.CommonResDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.geolatte.geom.M;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            response.put("message", "There are too many files to upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            List<PhotoListRsDto.PhotoInfoDto> infoDtos = new ArrayList<>();
            for (MultipartFile file : files){
                 photoService.uploadFile(file);
            }
            response.put("message", "Successfully uploaded " + files.size() + " files.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Failed to upload files.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // block별 사진 리스트
    @GetMapping("/{blockId}/list")
    public ResponseEntity<?> photoList(@PathVariable Long blockId){
        PhotoListRsDto dto = photoService.photoList(blockId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Files are successfully found.", dto), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePhotos(@RequestParam Long blockId,
                                          @RequestParam("oldFiles") List<String> oldFileNames,
                                          @RequestParam("newFiles") List<MultipartFile> newFiles) {

        // 입력된 파일의 개수가 10개 이하인지 판별
        if (newFiles.size() > 10) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.BAD_REQUEST, "There are too many files to upload", null), HttpStatus.BAD_REQUEST);
        }
        try {
            System.out.println(oldFileNames);
            PhotoListRsDto photoListRsDto = photoService.updateFiles(blockId, oldFileNames, newFiles);
            return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "Successfully uploaded " + newFiles.size() + " files.", photoListRsDto), HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload files.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

