package com.shoestore.dto.request.productManagementRequest;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DeleteImagesRequest {
    @NotEmpty(message = "Danh sách publicId không được để trống")
    private List<String> publicIds;
}