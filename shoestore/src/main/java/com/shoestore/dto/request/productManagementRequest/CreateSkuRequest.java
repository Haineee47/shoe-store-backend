package com.shoestore.dto.request.productManagementRequest;

import com.shoestore.validator.product.ValidPrice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidPrice
public class CreateSkuRequest {

    @NotBlank(message = "Mã SKU không được để trống")
    @Size(max = 50, message = "Mã SKU không vượt quá 50 ký tự")
    private String skuCode;

    @NotBlank(message = "Kích cỡ không được để trống")
    private String size;

    @NotBlank(message = "Màu sắc không được để trống")
    private String color;

    @NotNull(message = "Giá vốn không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá vốn không được là số âm")
    private java.math.BigDecimal costPrice;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá bán không được là số âm")
    private java.math.BigDecimal sellingPrice;

    @NotNull(message = "Số lượng tồn kho ban đầu không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity;

    @NotNull(message = "Ngưỡng cảnh báo tồn kho không được để trống")
    @Min(value = 1, message = "Ngưỡng cảnh báo tồn kho tối thiểu phải bằng 1")
    private Integer lowStockThreshold;

    @NotNull(message = "Trọng lượng không được để trống")
    @Min(value = 1, message = "Trọng lượng vận chuyển tối thiểu là 1 gram")
    private Integer weight;

    private Integer length;
    private Integer width;
    private Integer height;

    @Size(max = 500)
    private String skuImageUrl;

}

