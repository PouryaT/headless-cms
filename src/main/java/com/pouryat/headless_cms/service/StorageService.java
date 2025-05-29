package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.entity.Media;
import com.pouryat.headless_cms.entity.User;
import com.pouryat.headless_cms.mapper.MediaMapper;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class StorageService {

    private final MinioClient minioClient;

    private String bucketName;

    public StorageService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin123")
                .build();
    }

    public void createBucketAndMakePublic(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            this.bucketName = bucketName;
            log.debug("Bucket created: {}", bucketName);
        } else {
            this.bucketName = bucketName;
            log.debug("Bucket already exists: {}", bucketName);
        }
/*        try {
            String policyJson = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policyJson)
                            .build()
            );

            log.debug("Bucket policy updated successfully!");
        } catch (MinioException | IllegalArgumentException e) {
            log.debug("Error: {}", e.getMessage());
        }*/
    }

    public List<MinIOUploadResponse> uploadFile(User user, MultipartFile[] multipartFiles) throws Exception {
        List<MinIOUploadResponse> minIOUploadResponseList = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            try {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            } catch (Exception e) {
                throw new Exception(e);
            }

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(60 * 60)
                            .build());
            Media media = new Media();
            media.setUrl(getDownloadUrls(objectName));
            media.setFileName(objectName);
            media.setUploadedAt(LocalDateTime.now());
            media.setUploadedBy(user);
            media.setType(file.getContentType());
            MinIOUploadResponse minIOUploadResponse = new MinIOUploadResponse(media.getId(), url, objectName, media.getType(), MediaMapper.toDto(media));
            minIOUploadResponseList.add(minIOUploadResponse);
        }
        return minIOUploadResponseList;
    }

    public String getDownloadUrls(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating download URL for object: " + fileName, e);
        }
    }

    public void deleteFiles(List<String> objectNames) {
        try {
            List<DeleteObject> objects = objectNames.stream()
                    .map(DeleteObject::new)
                    .toList();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build()
            );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.err.println("Error deleting " + error.objectName() + ": " + error.message());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete files", e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            createBucketAndMakePublic("media");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }
}