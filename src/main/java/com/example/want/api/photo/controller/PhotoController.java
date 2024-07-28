package com.example.want.api.photo.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PhotoController {
    @PostMapping(value="/diary/new",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long saveDiary(HttpServletRequest request, @RequestParam(value="image") MultipartFile image){
//        Long diaryId = diaryService.keepDiary(image, diary);
//        return diaryId;
    }
}
