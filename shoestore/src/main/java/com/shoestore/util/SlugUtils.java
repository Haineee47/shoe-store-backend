package com.shoestore.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s+]");

    private SlugUtils() {
        // Khóa Constructor tránh việc khởi tạo đối tượng tiện ích thừa
    }

    public static String makeSlug(String input) {
        if (input == null || input.isBlank()) return "";

        // 1. Chuyển chữ hoa thành chữ thường thô
        String noWhitespace = WHITESPACE.matcher(input.trim().toLowerCase(Locale.ROOT)).replaceAll("-");

        // 2. Loại bỏ dấu tiếng Việt đặc thù và chuẩn hóa Unicode
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "d");

        // 3. Xóa các ký tự đặc biệt không hợp lệ trong URL
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // 4. Thu gọn nhiều dấu gạch ngang liên tiếp thành 1 dấu single
        return slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
