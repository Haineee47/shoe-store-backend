package com.shoestore.validator.product;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Áp dụng ở cấp độ Class Request
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PriceValidator.class, PriceUpdateValidator.class})
@Documented
public @interface ValidPrice {
    String message() default "Giá bán lẻ (sellingPrice) phải lớn hơn hoặc bằng giá vốn (costPrice)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}