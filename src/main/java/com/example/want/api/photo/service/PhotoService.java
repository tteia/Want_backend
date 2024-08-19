package com.example.want.api.photo.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.want.api.block.domain.Block;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.photo.domain.Photo;
import com.example.want.api.photo.dto.PhotoListRsDto;
import com.example.want.api.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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


    // ----- file  bucket에 업로드 + S3url db에 저장 -----
    @Transactional
    public PhotoListRsDto.PhotoInfoDto uploadFile(Long blockId, MultipartFile multipartFile) throws IOException {
        Block block = blockRepository.findById(blockId).orElseThrow(() -> new EntityNotFoundException("block id is not found"));

        String inputFileName = multipartFile.getOriginalFilename();
        String ext = getFileExtension(inputFileName);
        String contentType = getContentType(ext);
        ObjectMetadata metadata = createMetadata(contentType);
        String uuidFileName = generateUUIDFileName(ext);

        uploadToS3(multipartFile, uuidFileName, metadata);

        String url = getS3FileUrl(uuidFileName);
        Photo photo = savePhotoToDatabase(block, url);

        return createPhotoInfoDto(photo, url);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getContentType(String ext) {
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
        return contentType;
    }

    // metadata 입력
    private ObjectMetadata createMetadata(String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        return metadata;
    }

    private String generateUUIDFileName(String ext) {
        return UUID.randomUUID().toString() + "." + ext;
    }

    private void uploadToS3(MultipartFile multipartFile, String uuidFileName, ObjectMetadata metadata) throws IOException {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, uuidFileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException("Error uploading file to S3", e);
        } catch (SdkClientException e) {
            throw new SdkClientException("Error with S3 SDK client", e);
        }
    }

    private String getS3FileUrl(String uuidFileName) {
        return amazonS3.getUrl(bucket, uuidFileName).toString();
    }

    private Photo savePhotoToDatabase(Block block, String url) {
        Photo photo = Photo.builder()
                .photoUrl(url)
                .block(block)
                .build();
        return photoRepository.save(photo);
    }

    private PhotoListRsDto.PhotoInfoDto createPhotoInfoDto(Photo photo, String url) {
        return PhotoListRsDto.PhotoInfoDto.builder()
                .id(photo.getPhotoId())
                .url(url)
                .build();
    }


    // 파일 리스트
    public PhotoListRsDto photoList(Long blockId) {
        List<Photo> photos = photoRepository.findByBlockId(blockId);
        List<PhotoListRsDto.PhotoInfoDto> infoDtos = new ArrayList<>();
        for (Photo p : photos){
            infoDtos.add(p.FromEntity());
        }
        PhotoListRsDto photoListRsDto = PhotoListRsDto.builder()
                .blockId(blockId)
                .photoList(infoDtos)
                .build();
        return photoListRsDto;
    }


    // 파일 삭제
    @Transactional
    public void deleteFiles(Long blockId,  List<String> delFiles){
        for (String photoId : delFiles){
            Long longPhotoId = Long.parseLong(photoId);
            Photo photo = photoRepository.findById(longPhotoId).orElseThrow(()->new EntityNotFoundException("존재하지 않는 Id입니다."));
            String filename = extractFilename(photo.getPhotoUrl());
            amazonS3.deleteObject(bucket, filename);
            photoRepository.deleteByPhotoId(longPhotoId);
        }

    }

    // 파일 업데이트
    @Transactional
    public PhotoListRsDto updateFiles(Long blockId,
                            List<String> delFiles,
                            List<MultipartFile> newFiles) throws IOException {

//        // url에서 file 이름 추출
//        List<String> oldFileNames = new ArrayList<>();
//        for (String oldFileUrl : oldFiles){
//            oldFileNames.add(extractFilename(oldFileUrl));
//        }
//
//        List<String> updatedFileUrls = new ArrayList<>();
//
//        // 기존 파일 삭제
//        for (String oldFileName : oldFileNames) {
//            deleteFile(blockId, oldFileName);
//        }
        // delFiles 삭제
        deleteFiles(blockId, delFiles);

        // 새 파일 업로드
        List<PhotoListRsDto.PhotoInfoDto> infoDtos = new ArrayList<>();
        for (MultipartFile newFile : newFiles){
            PhotoListRsDto.PhotoInfoDto photoInfoDto = uploadFile(blockId, newFile);
            infoDtos.add(photoInfoDto);
        }

        PhotoListRsDto photoListRsDto = PhotoListRsDto.builder()
                .blockId(blockId)
                .photoList(infoDtos)
                .build();

        return photoListRsDto;
    }

    // url에서 file 이름 추출
    public String extractFilename(String url) {
        // URL에서 마지막 '/' 이후의 문자열을 추출
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return url.substring(lastSlashIndex + 1);
        }
        System.out.println("url :" + url);
        // '/'가 없으면 URL이 잘못된 경우로 처리
        return null;
    }
}
