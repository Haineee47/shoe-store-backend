package com.shoestore.dto.request.categoryManagementRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryStatusRequest {
    @NotNull(message = "Trạng thái kích hoạt không được để trống")
    private Boolean active;
}