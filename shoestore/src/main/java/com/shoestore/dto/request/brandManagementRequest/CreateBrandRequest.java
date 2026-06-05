package com.shoestore.dto.request.brandManagementRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandRequest {

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 100, message = "Tên thương hiệu không vượt quá 100 ký tự")
    private String name;

    private String description;

    @NotBlank(message = "Logo thương hiệu không được để trống") // E-commerce cần logo để làm bộ lọc hiển thị
    @URL(message = "Định dạng đường dẫn logo không hợp lệ")
    @Size(max = 500, message = "Link logo không vượt quá 500 ký tự")
    private String logoUrl;

    @Min(value = 0, message = "Thứ tự sắp xếp không được là số âm")
    private Integer sortOrder;

    private Boolean isActive;
}