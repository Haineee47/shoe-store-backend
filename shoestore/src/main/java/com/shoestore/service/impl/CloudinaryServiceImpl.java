package com.shoestore.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shoestore.common.enums.ErrorCode;
import com.shoestore.dto.response.productManagementResponse.UploadImageResponse;
import com.shoestore.exception.BusinessException;
import com.shoestore.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    // 🌟 Tạo Thread Pool gồm 5 luồng xử lý chuyên dụng cho việc upload ảnh phụ song song
    private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(5);

    @Override
    public UploadImageResponse uploadImage(MultipartFile file, String folder) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "shoestore/" + folder)
            );

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return new UploadImageResponse(imageUrl, publicId);

        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh lên Cloudinary: ", e);
            throw new BusinessException(ErrorCode.MEDIA_UPLOAD_FAILED); // Sử dụng lỗi chuẩn của bạn
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if ("not_found".equals(result.get("result"))) {
                log.warn("Không tìm thấy ảnh cần xóa trên Cloudinary với publicId: {}", publicId);
            }
        } catch (IOException e) {
            log.error("Lỗi khi xóa ảnh trên Cloudinary: ", e);
            throw new BusinessException(ErrorCode.MEDIA_DELETE_FAILED); // Đổi sang ErrorCode của bạn nếu có
        }
    }

    /**
     * 🌟 TÍNH NĂNG MỚI 1: Xóa hàng loạt ảnh (Bulk Delete)
     */
    @Override
    public void deleteImagesBulk(List<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) return;
        try {
            // Tận dụng API xóa gộp tối đa 100 ảnh/lần của Cloudinary SDK
            cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());
            log.info("Đã thực hiện xóa hàng loạt {} hình ảnh thành công.", publicIds.size());
        } catch (Exception e) {
            log.error("Lỗi khi xóa hàng loạt ảnh trên Cloudinary: ", e);
            throw new BusinessException(ErrorCode.MEDIA_DELETE_FAILED);
        }
    }

    /**
     * 🌟 TÍNH NĂNG MỚI 2: Upload danh sách ảnh song song không nghẽn luồng
     */
    @Override
    public List<UploadImageResponse> uploadMultipleImages(MultipartFile[] files, String folderName) {
        List<CompletableFuture<UploadImageResponse>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            // Đẩy các tác vụ upload vào Thread Pool xử lý bất đồng bộ
            CompletableFuture<UploadImageResponse> future = CompletableFuture.supplyAsync(() ->
                    this.uploadImage(file, folderName), uploadExecutor
            );
            futures.add(future);
        }

        // Chờ tất cả các ảnh upload xong đồng thời rồi thu hoạch kết quả
        try {
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .toList())
                    .join();
        } catch (Exception e) {
            log.error("Lỗi khi upload hàng loạt ảnh bất đồng bộ: ", e);
            throw new BusinessException(ErrorCode.MEDIA_UPLOAD_FAILED);
        }
    }
}