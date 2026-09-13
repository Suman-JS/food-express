package com.suman.foodexpress.uploads;

import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.uploads.dto.UploadResponse;
import com.suman.foodexpress.uploads.service.MinioStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Upload Routes")
@RequestMapping("/api/v1/uploads")
public class UploadsController {

  private final MinioStorageService minioStorageService;

  public UploadsController(MinioStorageService minioStorageService) {
    this.minioStorageService = minioStorageService;
  }

  @PostMapping("/images")
  @Operation(summary = "Upload an image", description = "PNG, JPEG or WEBP up to 5MB.")
  public ResponseEntity<ApiResponse<UploadResponse>> uploadImage(
      @RequestPart("file") MultipartFile file) {
    String url = minioStorageService.uploadImage(file, "images");
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success(new UploadResponse(url), "Image uploaded successfully."));
  }
}
