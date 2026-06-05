package com.shoestore.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 🌟 BẮT BUỘC: Để Spring biết đây là file cấu hình hạ tầng
public class CloudinaryConfig {

    @Value("${shoestore.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${shoestore.cloudinary.api-key}")
    private String apiKey;

    @Value("${shoestore.cloudinary.api-secret}")
    private String apiSecret;

    @Bean // 🌟 BẮT BUỘC: Đăng ký Object Cloudinary vào Spring Context làm Bean toàn cục
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}