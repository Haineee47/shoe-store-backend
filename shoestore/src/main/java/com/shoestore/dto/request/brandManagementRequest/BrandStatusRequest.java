package com.shoestore.dto.request.brandManagementRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandStatusRequest {
    @NotNull(message = "Trạng thái kích hoạt không được để trống")
    private Boolean isActive;
}