package com.shoestore.dto.request.categoryManagementRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Long parentId;
    private Integer sortOrder;
}