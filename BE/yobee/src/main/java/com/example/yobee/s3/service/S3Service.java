package com.example.yobee.s3.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Service
@NoArgsConstructor
public class S3Service {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file, String fileName) throws IOException {

        long size = file.getSize(); // 파일 크기

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        objectMetaData.setContentLength(size);

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), objectMetaData)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String imgurl){
        try {
            s3Client.deleteObject(bucket, imgurl);
            System.out.println(imgurl);
        }
        catch (Exception e) {
            System.out.println("s3 삭제 실패");
        }
    }

}
