package com.shoestore.controller;

import com.shoestore.common.constant.UploadConstant;
import com.shoestore.common.response.ApiResponse;
import com.shoestore.dto.request.productManagementRequest.DeleteImagesRequest;
import com.shoestore.dto.response.productManagementResponse.UploadImageResponse;
import com.shoestore.service.CloudinaryService;
import com.shoestore.validator.product.ImageValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;
    private final ImageValidator imageValidator;

    /**
     * NÂNG CẤP TÍNH NĂNG 1: Tải lên MỘT hình ảnh theo FOLDER linh hoạt
     */
    @PostMapping("/admin/uploads/images/{folderType}")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<UploadImageResponse>> uploadProductImage(
            @PathVariable String folderType,
            @RequestParam("file") MultipartFile file
    ) {
        // 1. Bảo mật: Check xem folderType truyền lên có hợp lệ không
        validateFolderType(folderType);

        // 2. Validate file (Size, Type)
        imageValidator.validate(file);

        // 3. Đẩy lên Cloudinary theo đúng phân vùng folderType
        UploadImageResponse response = cloudinaryService.uploadImage(file, folderType);

        return ResponseEntity.ok(
                ApiResponse.success("Tải lên hình ảnh vào thư mục '" + folderType + "' thành công", response)
        );
    }

    /**
     * NÂNG CẤP TÍNH NĂNG 2: Tải lên HÀNG LOẠT hình ảnh song song theo FOLDER linh hoạt
     */
    @PostMapping("/admin/uploads/images/{folderType}/bulk")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<List<UploadImageResponse>>> uploadMultipleImages(
            @PathVariable String folderType,
            @RequestParam("files") MultipartFile[] files
    ) {
        // 1. Bảo mật: Check folderType
        validateFolderType(folderType);

        // 2. Quét mảng validate định dạng toàn bộ file
        for (MultipartFile file : files) {
            imageValidator.validate(file);
        }

        // 3. Thực thi upload song song đa luồng vào đúng folderType
        List<UploadImageResponse> responses = cloudinaryService.uploadMultipleImages(files, folderType);

        return ResponseEntity.ok(
                ApiResponse.success("Tải lên danh sách hình ảnh vào thư mục '" + folderType + "' thành công", responses)
        );
    }

    /**
     * TÍNH NĂNG 3: Xóa một hình ảnh đơn lẻ khỏi Cloudinary (Giữ nguyên)
     */
    @DeleteMapping("/admin/uploads/images")
    @PreAuthorize("@ss.hasPermission('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @RequestParam("publicId") String publicId
    ) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponse.success("Xóa hình ảnh trên đám mây thành công", null));
    }

    /**
     * TÍNH NĂNG 4: Xóa hình ảnh hàng loạt (Bulk Delete) (Giữ nguyên)
     */
    @DeleteMapping("/admin/uploads/images/bulk")
    @PreAuthorize("@ss.hasPermission('PRODUCT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteImagesBulk(
            @Valid @RequestBody DeleteImagesRequest request
    ) {
        cloudinaryService.deleteImagesBulk(request.getPublicIds());
        return ResponseEntity.ok(ApiResponse.success("Xóa loạt hình ảnh trên đám mây thành công", null));
    }

    /**
     * Hàm helper kiểm tra tính hợp lệ của thư mục truyền lên
     */
    private void validateFolderType(String folderType) {
        if (!UploadConstant.ALLOWED_FOLDERS.contains(folderType.toLowerCase())) {
            // Bạn có thể đổi sang dùng BusinessException(ErrorCode.INVALID_FOLDER) chuẩn của bạn nhé
            throw new IllegalArgumentException("Thư mục lưu trữ '" + folderType + "' không được hệ thống hỗ trợ!");
        }
    }
}