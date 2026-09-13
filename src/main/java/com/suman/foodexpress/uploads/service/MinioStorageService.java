package com.suman.foodexpress.uploads.service;

import com.suman.foodexpress.auth.exception.ApiException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioStorageService {

  private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
  private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/png", "image/jpeg", "image/webp");
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");

  private final MinioClient minioClient;
  private final String bucket;
  private final String endpoint;

  public MinioStorageService(
      MinioClient minioClient,
      @Value("${app.minio.bucket}") String bucket,
      @Value("${app.minio.endpoint}") String endpoint) {
    this.minioClient = minioClient;
    this.bucket = bucket;
    this.endpoint = endpoint;
  }

  @PostConstruct
  void initializeBucket() {
    try {
      boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
      minioClient.setBucketPolicy(
          SetBucketPolicyArgs.builder().bucket(bucket).config(publicReadPolicy(bucket)).build());
    } catch (Exception ex) {
      log.warn("Failed to initialize MinIO bucket '{}': {}", bucket, ex.getMessage());
    }
  }

  public String uploadImage(MultipartFile file, String folder) {
    validate(file);
    String extension = extensionOf(file.getOriginalFilename());
    String key = folder + "/" + UUID.randomUUID() + "." + extension;
    try (InputStream in = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucket).object(key).stream(in, file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
    } catch (Exception ex) {
      throw new ApiException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image: " + ex.getMessage());
    }
    return endpoint + "/" + bucket + "/" + key;
  }

  private void validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "File is required");
    }
    if (file.getSize() > MAX_IMAGE_SIZE) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Image must not exceed 5MB");
    }
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Only PNG, JPEG and WEBP images are allowed");
    }
    String extension = extensionOf(file.getOriginalFilename());
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid image extension");
    }
  }

  private String extensionOf(String filename) {
    if (filename == null) {
      return "";
    }
    int dot = filename.lastIndexOf('.');
    return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  private String publicReadPolicy(String bucketName) {
    return """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": { "AWS": ["*"] },
              "Action": ["s3:GetObject"],
              "Resource": ["arn:aws:s3:::%s/*"]
            }
          ]
        }
        """
        .formatted(bucketName);
  }
}
