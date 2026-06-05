package com.shoestore.dto.response.categoryManagementResponse;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl; // 🌟 Bổ sung link ảnh phục vụ làm Icon Menu ngoài trang chủ
    private List<CategoryTreeResponse> children;
}