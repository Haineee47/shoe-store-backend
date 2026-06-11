package com.shoestore.validator.product;

import com.shoestore.dto.request.productManagementRequest.SkuUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceUpdateValidator implements ConstraintValidator<ValidPrice, SkuUpdateRequest> {

    @Override
    public boolean isValid(SkuUpdateRequest sku, ConstraintValidatorContext context) {
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