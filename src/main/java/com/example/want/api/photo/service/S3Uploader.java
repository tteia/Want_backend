package com.example.want.api.photo.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.want.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public String uploadFile(MultipartFile multipartFile) throws IOException {

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
        return amazonS3.getUrl(bucket, uuidFileName).toString();
    }
}
