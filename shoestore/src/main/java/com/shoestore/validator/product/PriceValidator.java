package com.shoestore.validator.product;

import com.shoestore.dto.request.productManagementRequest.CreateProductRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<ValidPrice, CreateProductRequest.SkuRequest> {

    @Override
    public boolean isValid(CreateProductRequest.SkuRequest sku, ConstraintValidatorContext context) {
        if (sku == null || sku.getCostPrice() == null || sku.getSellingPrice() == null) {
            return true; // Để các Annotation @NotNull khác trong SkuRequest tự xử lý
        }

        boolean isValid = sku.getSellingPrice().compareTo(sku.getCostPrice()) >= 0;

        if (!isValid) {
            // 🌟 Tắt thông báo lỗi chung chung mặc định của Class Level
            context.disableDefaultConstraintViolation();

            // 🌟 Định tuyến lỗi chỉ đích danh vào thuộc tính 'sellingPrice'
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("sellingPrice")
                    .addConstraintViolation();
        }

        return isValid;
    }
}