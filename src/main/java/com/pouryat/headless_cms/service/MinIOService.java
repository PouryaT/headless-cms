package com.pouryat.headless_cms.service;

import com.pouryat.headless_cms.model.MinIODownloadResponse;
import com.pouryat.headless_cms.model.MinIOUploadResponse;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MinIOService {

    private final MinioClient minioClient;

    private String bucketName;

    public MinIOService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin123")
                .build();
    }

    public void createBucketAndMakePublic(String bucketName) throws Exception {
        // Check if bucket exists
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!found) {
            // Create bucket
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            this.bucketName = bucketName;
            System.out.println("Bucket created: " + bucketName);
        } else {
            this.bucketName = bucketName;
            System.out.println("Bucket already exists: " + bucketName);
        }
        try {
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

            // Apply the policy
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policyJson)
                            .build()
            );

            System.out.println("Bucket policy updated successfully!");
        } catch (MinioException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public MinIOUploadResponse uploadFile(MultipartFile file) throws Exception {
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

        return new MinIOUploadResponse(minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.GET)
                        .expiry(60 * 60) // 1 hour
                        .build()
        ), objectName);
    }

    public List<MinIODownloadResponse.FileDownloadMeta> getDownloadUrls(List<String> fileNames) {
        return fileNames.stream().map(fileName -> {
            String url;
            try {
                url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(fileName)
                                .expiry(60 * 60) // 1 hour
                                .build()
                );
            } catch (Exception e) {
                throw new RuntimeException("Error generating download URL for object: " + fileName, e);
            }

            MinIODownloadResponse.FileDownloadMeta meta = new MinIODownloadResponse.FileDownloadMeta();
            meta.setFileName(fileName);
            meta.setUrl(url);
            return meta;
        }).collect(Collectors.toList());
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