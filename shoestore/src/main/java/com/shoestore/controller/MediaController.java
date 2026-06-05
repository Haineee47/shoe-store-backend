package com.shoestore.controller;

import com.shoestore.common.enums.media.MediaFolder;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.response.mediaManagementResponse.ImageResponse;
import com.shoestore.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
public class MediaController { // 🌟 Đã sửa lỗi naming sao chép

    private final MediaService mediaService;

    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermission('MEDIA_UPLOAD')")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) MediaFolder folder) { // 🌟 Nhận trực tiếp Enum tự động Mapping từ RequestParam

        ImageResponse response = mediaService.uploadImage(file, folder);
        return ResponseEntity.ok(ApiResponse.success("Tải lên hình ảnh thành công", response));
    }

    // 🌟 Chuyển đổi sang RESTful Path Variable phong cách Production
    // Front-end gọi: DELETE /api/v1/admin/media/shoestore/products/sample_id
    @DeleteMapping("/{*publicId}")
    @PreAuthorize("@ss.hasPermission('MEDIA_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String publicId) {
        // Spring sẽ tự bóc tách dấu gạch chéo đầu tiên nếu có, ví dụ: "shoestore/products/abc"
        if (publicId != null && publicId.startsWith("/")) {
            publicId = publicId.substring(1);
        }
        mediaService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponse.success("Xóa hình ảnh thành công", null));
    }
}