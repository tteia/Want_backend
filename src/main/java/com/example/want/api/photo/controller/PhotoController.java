package com.example.want.api.photo.controller;

import com.example.want.api.photo.dto.CreatePhotoRqDto;

import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
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

    @ResponseBody
    @PostMapping(value="/{blockId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> fileUpload(@PathVariable Long blockId, @RequestParam(value = "files", required = false) List<MultipartFile> files){
        Map<String, String> response = new HashMap<>();
        if (files.size() > 10) {
            response.put("message", "There are too many files to upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
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

}
