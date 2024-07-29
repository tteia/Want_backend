package com.example.want.api.photo.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.dto.CreatePhotoRqDto;
import com.example.want.api.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class PhotoService {

    private final AmazonS3 amazonS3;
    private final PhotoRepository photoRepository;
    private final BlockRepository blockRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

//    1. 용량제한, -> 백엔드 코드에서 용량 resize, S3자체에서 용량 resize
//    4-1.블록이랑 연결.

    // S3 Uploader
    @Transactional
    public void uploadFile(Long blockId, MultipartFile multipartFile) throws IOException {

        String inputFileName = multipartFile.getOriginalFilename();

        //파일 형식 구하기
        String ext = inputFileName.substring(inputFileName.lastIndexOf(".") + 1).toLowerCase();
        String contentType;

        //content type을 지정해서 올려주지 않으면 자동으로 "application/octet-stream"으로 고정이 되서 링크 클릭시 웹에서 열리는게 아니라 자동 다운이 시작됨.
        switch (ext) {
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "jpg":
                contentType = "image/jpg";
                break;
            default:
                throw new IllegalArgumentException("Only image files (jpeg, png, jpg) are allowed.");   // 안뜸
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        System.out.println("metadata:" + metadata.getContentType());

        String uuidFileName = UUID.randomUUID().toString() + "." + ext;

        try {
            amazonS3.putObject(new PutObjectRequest(bucket, uuidFileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        //object 정보 가져오기
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucket);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

        for (S3ObjectSummary object: objectSummaries) {
            System.out.println("object = " + object.toString());
        }

        String url = amazonS3.getUrl(bucket, uuidFileName).toString();
        Block block = blockRepository.findById(blockId).orElseThrow(()->new EntityNotFoundException("block id is not found"));
        Photo photo = Photo.builder()
                .photoUrl(url)
                .block(block)
                .build();
        photoRepository.save(photo);
    }

    public List<Photo> getFiles() {
        List<Photo> all = photoRepository.findAll();
        return all;
    }


}
