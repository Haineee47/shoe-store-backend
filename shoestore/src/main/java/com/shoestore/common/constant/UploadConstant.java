package com.shoestore.common.constant;

import java.util.Set;

public class UploadConstant {
    // Chỉ chấp nhận 4 thư mục này, truyền ngoài vùng này sẽ bị chặn đứng
    public static final Set<String> ALLOWED_FOLDERS = Set.of("products", "avatars", "banners", "blogs");
}