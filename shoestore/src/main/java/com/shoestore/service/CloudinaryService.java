package com.shoestore.service;

import com.shoestore.dto.response.productManagementResponse.UploadImageResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CloudinaryService {

    UploadImageResponse uploadImage(MultipartFile file, String folder);

    void deleteImage(String publicId);

    // 🌟 THÊM 2 HÀM MỚI VÀO ĐÂY:
    void deleteImagesBulk(List<String> publicIds);

    List<UploadImageResponse> uploadMultipleImages(MultipartFile[] files, String folderName);
}