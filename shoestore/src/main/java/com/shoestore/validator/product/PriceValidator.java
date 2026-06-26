package com.shoestore.validator.product;

// 🌟 Thay đổi dòng import này hướng tới file DTO độc lập mới
import com.shoestore.dto.request.productManagementRequest.CreateSkuRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// 🌟 Đổi Target Type ở đây thành CreateSkuRequest
public class PriceValidator implements ConstraintValidator<ValidPrice, CreateSkuRequest> {

    @Override
    public boolean isValid(CreateSkuRequest sku, ConstraintValidatorContext context) {
        if (sku == null || sku.getCostPrice() == null || sku.getSellingPrice() == null) {
            return true;
        }

        boolean isValid = sku.getSellingPrice().compareTo(sku.getCostPrice()) >= 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("sellingPrice")
                    .addConstraintViolation();
        }

        return isValid;
    }
}