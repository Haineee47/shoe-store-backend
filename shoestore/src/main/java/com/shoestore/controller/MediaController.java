package com.shoestore.controller;

import com.shoestore.common.constant.UploadConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.productManagementRequest.DeleteImagesRequest;
import com.shoestore.dto.response.productManagementResponse.UploadImageResponse;
import com.shoestore.service.CloudinaryService;
import com.shoestore.validator.product.ImageValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MediaController {

    private final CloudinaryService cloudinaryService;
    private final ImageValidator imageValidator;

    // =========================================================================
    // 📤 1. UPLOAD OPERATIONS - LUỒNG TẢI LÊN PHƯƠNG TIỆN
    // =========================================================================

    /**
     * API tải lên MỘT hình ảnh đơn lẻ vào phân vùng thư mục động.
     */
    @PostMapping("/upload/{folderType}")
    @PreAuthorize("@ss.hasPermission('MEDIA_UPLOAD') or @ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<UploadImageResponse>> uploadImage(
            @PathVariable String folderType,
            @RequestParam("file") MultipartFile file
    ) {
        validateFolderType(folderType);
        imageValidator.validate(file);

        UploadImageResponse response = cloudinaryService.uploadImage(file, folderType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Upload image successful", response));
    }

    /**
     * API tải lên HÀNG LOẠT hình ảnh song song (Bulk Upload) vào phân vùng thư mục động.
     */
    @PostMapping("/upload/{folderType}/bulk")
    @PreAuthorize("@ss.hasPermission('MEDIA_UPLOAD') or @ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<List<UploadImageResponse>>> uploadMultipleImages(
            @PathVariable String folderType,
            @RequestParam("files") MultipartFile[] files
    ) {
        validateFolderType(folderType);
        for (MultipartFile file : files) {
            imageValidator.validate(file);
        }

        List<UploadImageResponse> responses = cloudinaryService.uploadMultipleImages(files, folderType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk upload images successful", responses));
    }

    // =========================================================================
    // 🗑️ 2. DELETE OPERATIONS - LUỒNG XÓA TÀI NGUYÊN PHƯƠNG TIỆN
    // =========================================================================

    /**
     * API xóa MỘT hình ảnh dựa vào Public ID truyền qua Request Param.
     */
    @DeleteMapping
    @PreAuthorize("@ss.hasPermission('MEDIA_DELETE') or @ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @RequestParam("publicId") String publicId
    ) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponse.success("Delete image successful", null));
    }

    /**
     * API xóa HÀNG LOẠT hình ảnh (Bulk Delete) thông qua Request Body.
     */
    @DeleteMapping("/bulk")
    @PreAuthorize("@ss.hasPermission('MEDIA_DELETE') or @ss.hasPermission('PRODUCT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteImagesBulk(
            @Valid @RequestBody DeleteImagesRequest request
    ) {
        cloudinaryService.deleteImagesBulk(request.getPublicIds());
        return ResponseEntity.ok(ApiResponse.success("Bulk delete images successful", null));
    }

    /**
     * API mở rộng: Xóa hình ảnh dựa theo cấu trúc toàn bộ đường dẫn publicId.
     */
    @DeleteMapping("/path/{*publicId}")
    @PreAuthorize("@ss.hasPermission('MEDIA_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteImageByPath(@PathVariable String publicId) {
        if (publicId != null && publicId.startsWith("/")) {
            publicId = publicId.substring(1);
        }
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponse.success("Delete image by path successful", null));
    }

    // =========================================================================
    // ⚙️ PRIVATE HELPERS
    // =========================================================================

    private void validateFolderType(String folderType) {
        if (!UploadConstant.ALLOWED_FOLDERS.contains(folderType.toLowerCase())) {
            throw new IllegalArgumentException("Storage folder '" + folderType + "' is not supported by the system!");
        }
    }
}