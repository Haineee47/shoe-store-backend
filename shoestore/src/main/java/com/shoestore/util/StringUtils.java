package com.shoestore.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) return "";

        // 1. Chuyển chữ hoa thành chữ thường và thay khoảng trắng bằng dấu gạch ngang
        String nowhitespace = WHITESPACE.matcher(input.trim().toLowerCase(Locale.ROOT)).replaceAll("-");

        // 2. Loại bỏ dấu tiếng Việt (Ví dụ: "á" -> "a")
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");

        // 3. Thay chữ đ/Đ thành d
        slug = slug.replaceAll("đ", "d");

        // 4. Loại bỏ các ký tự đặc biệt rác
        slug = NONLATIN.matcher(slug).replaceAll("");

        // 5. Rút gọn nhiều dấu gạch ngang liên tiếp "---" thành một dấu "-"
        return slug.replaceAll("-+", "-");
    }
}