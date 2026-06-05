package com.shoestore.dto.request.brandManagementRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBrandRequest {

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 100, message = "Tên thương hiệu không vượt quá 100 ký tự")
    private String name;

    private String description;

    @Size(max = 500, message = "Link logo không vượt quá 500 ký tự")
    private String logoUrl;

    private Integer sortOrder;

    private Boolean isActive;
}