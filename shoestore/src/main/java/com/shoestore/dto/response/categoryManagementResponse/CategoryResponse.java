package com.shoestore.dto.response.categoryManagementResponse;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl; // 🌟 Bổ sung trả về link ảnh
    private Integer sortOrder; // 🌟 Bổ sung trả về thứ tự sắp xếp
    private Boolean isActive;
    private Long parentId;
    private String parentName;
    private LocalDateTime createdAt; // 🌟 Bổ sung thông tin quản trị hệ thống
    private LocalDateTime updatedAt; // 🌟 Bổ sung thông tin quản trị hệ thống
}