package com.shoestore.dto.request.categoryManagementRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 100,
            message = "Tên danh mục phải từ 2 đến 100 ký tự")
    private String name;

    @Size(max = 2000,
            message = "Mô tả không vượt quá 2000 ký tự")
    private String description;

    @Size(max = 500,
            message = "Đường dẫn ảnh không vượt quá 500 ký tự")
    private String imageUrl;

    @Min(value = 0,
            message = "Thứ tự hiển thị phải lớn hơn hoặc bằng 0")
    private Integer sortOrder;

    private Long parentId;

    private Boolean isActive;
}