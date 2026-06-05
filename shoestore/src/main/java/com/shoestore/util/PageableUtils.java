package com.shoestore.util;

import com.shoestore.common.constant.PaginationConstant;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PageableUtils {

    private PageableUtils() {
        // Utility class
    }

    /**
     * Tạo đối tượng Pageable chuẩn từ các tham số request.
     * Hỗ trợ sort đơn lẻ (ví dụ: "name,asc") hoặc đa điều kiện (ví dụ: "sortOrder,asc&id,desc")
     */
    public static Pageable createPageable(int page, int size, String sort) {
        // 1. Validate page và size chống các giá trị âm hoặc quá lớn
        if (page < 0) page = 0;
        if (size <= 0) size = Integer.parseInt(PaginationConstant.DEFAULT_PAGE_SIZE);
        if (size > PaginationConstant.MAX_PAGE_SIZE) size = PaginationConstant.MAX_PAGE_SIZE;

        // 2. Xử lý Sort logic
        Sort sortOrder = Sort.unsorted();

        if (StringUtils.hasText(sort)) {
            List<Sort.Order> orders = new ArrayList<>();

            // Hỗ trợ trường hợp client truyền nhiều điều kiện sort cách nhau bằng dấu và (&) hoặc dấu phẩy (,)
            String[] sortParams = sort.contains("&") ? sort.split("&") : new String[]{sort};

            for (String sortParam : sortParams) {
                if (sortParam.contains(",")) {
                    String[] parts = sortParam.split(",");
                    String property = parts[0].trim();
                    String directionStr = parts.length > 1 ? parts[1].trim() : "asc";

                    Sort.Direction direction = directionStr.equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

                    orders.add(new Sort.Order(direction, property));
                }
            }

            if (!orders.isEmpty()) {
                sortOrder = Sort.by(orders);
            }
        }

        return PageRequest.of(page, size, sortOrder);
    }
}