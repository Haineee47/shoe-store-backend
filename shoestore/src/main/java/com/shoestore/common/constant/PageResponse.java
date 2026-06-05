package com.shoestore.common.constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private long totalItems;    // Tổng số phần tử trong DB (totalElements)
    private int totalPages;     // Tổng số trang tính được
    private int currentPage;    // Trang hiện tại (0, 1, 2...)
    private int pageSize;       // Kích thước trang
    private List<T> data;       // Mảng danh sách kết quả trả về (Ví dụ: List<CategoryResponse>)
}