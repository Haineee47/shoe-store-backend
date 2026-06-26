package com.shoestore.dto.response.categoryManagementResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySummaryResponse {
    private Long id;
    private String name;
    private String slug;
    private boolean active;
    private Integer sortOrder;
    private String parentName;
}