package com.example.want.api.photo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PhotoService {
//    todo : 용량제한 설정하기 추가

    private final PhotoRepository photoRepository;

    public void save(CreatePhotoRqDto createPhotoRqDto) {
        Photo photo = new Photo(createPhotoRqDto.getTitle(), createPhotoRqDto.getUrl());
        photoRepository.save(photo);
    }

    public List<Photo> getFiles() {
        List<Photo> all = photoRepository.findAll();
        return all;
    }

//    private final AmazonS3 amazonS3;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    private AmazonS3Client s3Client;
//
//    //게시글 등록
//    @Transactional
//    public Photo createPhoto(CreatePhotoRqDto dto) {
//
//        MultipartFile imageUrl = dto.getUrl();
//
//        //s3 관련
//        String fileName = createFileName(imageUrl.getOriginalFilename());
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(imageUrl.getSize());
//        objectMetadata.setContentType(imageUrl.getContentType());
//
//        System.out.println(bucket);
//
//        try(InputStream inputStream = imageUrl.getInputStream()) {
//            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch(IOException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
//        }
//
//        String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
//
////        PostRequestDto postRequestDto = new PostRequestDto(size, type, style, area, imgUrl, content);
////        Post post = new Post(user, postRequestDto);
////        postRepository.save(post);
//
//        return null;
//    }
//////////////////////////------------S3관련---------------//////////////////////////////
//
//
//    public void deleteImage(String fileName) {
//        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
//    }
//
//    private String createFileName(String fileName) {
//        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
//    }
//
//    private String getFileExtension(String fileName) {
//        try {
//            return fileName.substring(fileName.lastIndexOf("."));
//        } catch (StringIndexOutOfBoundsException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
//        }
//    }


}
