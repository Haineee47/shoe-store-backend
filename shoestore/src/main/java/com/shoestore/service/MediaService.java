package com.shoestore.service;

import com.shoestore.common.enums.media.MediaFolder;
import com.shoestore.dto.response.mediaManagementResponse.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    ImageResponse uploadImage(MultipartFile file, MediaFolder folder);
    void deleteImage(String publicId);
}