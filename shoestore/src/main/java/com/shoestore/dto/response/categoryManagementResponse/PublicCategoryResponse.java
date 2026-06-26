package com.shoestore.dto.response.categoryManagementResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;

    // 🔥 SEO Meta Extensions
    private String metaTitle;
    private String metaDescription;
    private String canonicalUrl;
}