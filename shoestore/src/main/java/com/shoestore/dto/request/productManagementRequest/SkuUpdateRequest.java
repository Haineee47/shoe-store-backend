package com.shoestore.dto.request.productManagementRequest;

import com.shoestore.validator.product.ValidPrice;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidPrice
public class SkuUpdateRequest {

    /**
     * null = SKU mới
     * khác null = SKU đã tồn tại
     */
    private Long id;

    @NotBlank(message = "Mã SKU không được để trống")
    @Size(max = 50, message = "Mã SKU không vượt quá 50 ký tự")
    private String skuCode;

    @NotBlank(message = "Kích cỡ không được để trống")
    private String size;

    @NotBlank(message = "Màu sắc không được để trống")
    private String color;

    @NotNull(message = "Giá vốn không được để trống")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal costPrice;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal sellingPrice;

    @NotNull(message = "Tồn kho không được để trống")
    @Min(value = 0)
    private Integer stockQuantity;

    @NotNull(message = "Ngưỡng cảnh báo tồn kho không được để trống")
    @Min(value = 1)
    private Integer lowStockThreshold;

    @NotNull(message = "Trọng lượng không được để trống")
    @Min(value = 1)
    private Integer weight;

    private Integer length;

    private Integer width;

    private Integer height;

    @Size(max = 500)
    private String skuImageUrl;
}