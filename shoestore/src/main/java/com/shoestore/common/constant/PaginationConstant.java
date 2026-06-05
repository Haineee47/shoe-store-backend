package com.shoestore.common.constant;

public final class PaginationConstant {
    private PaginationConstant() {
        // Private constructor to prevent instantiation
    }

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY_ID_DESC = "id,desc";

    // Giới hạn số lượng item tối đa trên 1 trang để tránh bị dập DDOS kéo sập DB
    public static final int MAX_PAGE_SIZE = 100;
}