package com.shoestore.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shoestore.common.enums.ErrorCode;
import com.shoestore.common.enums.media.MediaFolder;
import com.shoestore.dto.response.mediaManagementResponse.ImageResponse;
import com.shoestore.exception.BusinessException;
import com.shoestore.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;

    @Value("${shoestore.media.max-file-size}")
    private long maxFileSize;

    @Value("${shoestore.media.allowed-types}")
    private List<String> allowedTypes;

    @Override
    public ImageResponse uploadImage(MultipartFile file, MediaFolder folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.MEDIA_FILE_EMPTY);
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.MEDIA_FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BusinessException(ErrorCode.MEDIA_FILE_TYPE_NOT_ALLOWED);
        }

        // 🌟 1. Filename validation chặt chẽ chống Path Traversal (../)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.MEDIA_FILENAME_INVALID);
        }
        String cleanedFilename = org.springframework.util.StringUtils.cleanPath(originalFilename);
        if (cleanedFilename.contains("..") || cleanedFilename.startsWith("/")) {
            throw new BusinessException(ErrorCode.MEDIA_FILENAME_INVALID);
        }

        try {
            MediaFolder targetFolder = (folder != null) ? folder : MediaFolder.GENERAL;

            Map<?, ?> uploadParams = ObjectUtils.asMap(
                    "folder", targetFolder.getPath(),
                    "resource_type", "image"
            );

            // 🌟 2. Sử dụng getInputStream() thay vì getBytes() để tối ưu RAM
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getInputStream(), uploadParams);

            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");

            return ImageResponse.builder()
                    .publicId(publicId)
                    .url(url)
                    .originalFilename(cleanedFilename)
                    .size(file.getSize())
                    .contentType(contentType)
                    .build();

        } catch (IOException e) {
            log.error("💥 Lỗi IO khi upload file bằng InputStream: ", e);
            throw new BusinessException(ErrorCode.MEDIA_UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            throw new BusinessException(ErrorCode.MEDIA_PUBLIC_ID_INVALID);
        }

        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String result = (String) deleteResult.get("result");

            if ("not_found".equals(result)) {
                log.warn("⚠️ Remote image not found on Cloudinary with ID: {}", publicId);
            } else {
                log.info("🗑️ Remote image deleted successfully from Cloudinary. ID: {}", publicId);
            }
        } catch (IOException e) {
            log.error("💥 Exception occurred during Cloudinary destroy: ", e);
            throw new BusinessException(ErrorCode.MEDIA_DELETE_FAILED);
        }
    }
}