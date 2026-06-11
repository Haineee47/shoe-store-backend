package com.shoestore.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===================================================================
    // 🔒 AUTHENTICATION & USER MODULE (Mã lỗi Xác thực & Người dùng)
    // ===================================================================
    USER_NOT_FOUND("Người dùng không tồn tại trên hệ thống"),
    INVALID_CREDENTIALS("Thông tin tài khoản hoặc mật khẩu không chính xác"),
    ACCOUNT_LOCKED("Tài khoản đã bị khóa do nhập sai quá nhiều lần"),
    ACCOUNT_DISABLED("Tài khoản hiện đang bị vô hiệu hóa"),
    ROLE_NOT_FOUND("Vai trò người dùng không tồn tại"),
    ACCESS_DENIED("Bạn không có quyền truy cập vào chức năng này"),
    UNAUTHORIZED("Yêu cầu không hợp lệ, vui lòng đăng nhập lại"),

    // Email Verification & Password Reset
    EMAIL_ALREADY_EXISTS("Email này đã được sử dụng bởi tài khoản khác"),
    EMAIL_NOT_VERIFIED("Email tài khoản chưa được xác thực"),
    EMAIL_ALREADY_VERIFIED("Email này đã được xác thực trước đó"),
    PLEASE_WAIT_BEFORE_RESENDING("Vui lòng đợi 1 phút trước khi yêu cầu gửi lại mã"),
    INCORRECT_OLD_PASSWORD("Mật khẩu cũ không chính xác"),
    NEW_PASSWORD_MUST_BE_DIFFERENT("Mật khẩu mới không được trùng với mật khẩu hiện tại"),
    SOCIAL_ACCOUNT_CANNOT_CHANGE_PASSWORD("Tài khoản đăng nhập bằng mạng xã hội không thể đổi mật khẩu trực tiếp"),
    INVALID_RESET_TOKEN("Mã khôi phục mật khẩu không hợp lệ"),
    RESET_TOKEN_EXPIRED("Mã khôi phục mật khẩu đã hết hạn"),

    // JWT Tokens
    INVALID_TOKEN("Mã token không hợp lệ hoặc đã bị thay đổi"),
    TOKEN_EXPIRED("Mã token đã hết hạn, vui lòng đăng nhập lại"),
    TOKEN_ALREADY_USED("Mã token này đã được sử dụng"),
    REFRESH_TOKEN_NOT_FOUND("Không tìm thấy Refresh Token"),
    REFRESH_TOKEN_EXPIRED("Refresh Token đã hết hạn, vui lòng đăng nhập lại"),
    REFRESH_TOKEN_REVOKED("Refresh Token đã bị thu hồi hoặc đăng xuất"),

    // Google OAuth2
    GOOGLE_TOKEN_INVALID("Mã Google ID Token không hợp lệ"),
    GOOGLE_AUTHENTICATION_FAILED("Xác thực qua Google không thành công"),
    GOOGLE_EMAIL_NOT_VERIFIED("Tài khoản Google này chưa được xác thực email"),

    // ===================================================================
    // 🏷️ CATEGORY MODULE (Mã lỗi Danh mục sản phẩm)
    // ===================================================================
    CATEGORY_NOT_FOUND("Danh mục sản phẩm không tồn tại"),
    CATEGORY_NAME_ALREADY_EXISTS("Tên danh mục này đã tồn tại"),
    CATEGORY_SLUG_ALREADY_EXISTS("Đường dẫn (Slug) danh mục này đã tồn tại"),
    PARENT_CATEGORY_NOT_FOUND("Không tìm thấy danh mục cha"),
    CATEGORY_CANNOT_BE_ITS_OWN_PARENT("Một danh mục không thể chọn chính nó làm danh mục cha"),
    CATEGORY_HAS_CHILDREN_CANNOT_DELETE("Danh mục này đang chứa danh mục con, không thể xóa"),
    CATEGORY_CIRCULAR_REFERENCE("Danh mục cha không hợp lệ, phát hiện vòng lặp danh mục"),

    // ===================================================================
    // 👟 BRAND MODULE (Mã lỗi Thương hiệu)
    // ===================================================================
    BRAND_NOT_FOUND("Thương hiệu không tồn tại"),
    BRAND_NAME_ALREADY_EXISTS("Tên thương hiệu này đã tồn tại"),
    BRAND_SLUG_ALREADY_EXISTS("Đường dẫn (Slug) thương hiệu này đã tồn tại"),

    // ===================================================================
    // 📁 MEDIA & CLOUDINARY MODULE (Mã lỗi File & Lưu trữ hình ảnh)
    // ===================================================================
    MEDIA_FILENAME_INVALID("Tên tệp tin không hợp lệ hoặc chứa mã độc Path Traversal"),
    MEDIA_FILE_EMPTY("Tệp tin tải lên trống rỗng, vui lòng chọn lại file"),
    MEDIA_FILE_TOO_LARGE("Kích thước tệp vượt quá giới hạn cho phép (Tối đa 2MB)"),
    MEDIA_FILE_TYPE_NOT_ALLOWED("Định dạng tệp không được hỗ trợ (Chỉ chấp nhận JPEG, PNG, WEBP)"),
    MEDIA_UPLOAD_FAILED("Quá trình tải tệp tin lên hệ thống Cloudinary thất bại"),
    MEDIA_PUBLIC_ID_INVALID("Mã định danh file (Public ID) trên hệ thống Cloud không hợp lệ"),
    MEDIA_DELETE_FAILED("Không thể xóa tệp tin cũ khỏi hệ thống lưu trữ đám mây"),

    SKU_STOCK_INSUFFICIENT(
            "Số lượng tồn kho không đủ"
    ),

    PRODUCT_NAME_ALREADY_EXISTS(
            "Tên sản phẩm đã tồn tại"
    ),

    PRODUCT_SKU_DUPLICATE_IN_REQUEST(
            "Danh sách SKU gửi lên bị trùng mã SKU"
    ),

    PRODUCT_SKU_ALREADY_EXISTS(
            "Mã SKU đã tồn tại trong hệ thống"
    ),

    PRODUCT_NOT_FOUND(
            "Sản phẩm không tồn tại"
    ),

    PRODUCT_SKU_NOT_FOUND("SKU không tồn tại"),

    PRODUCT_ALREADY_DELETED(
            "Sản phẩm đã bị xóa"
    ),

    PRODUCT_IMAGE_NOT_FOUND(
            "Hình ảnh sản phẩm không tồn tại"
    ),

    PRODUCT_VARIANT_DUPLICATE(
            "Biến thể sản phẩm bị trùng màu sắc và kích cỡ"
    ),

    PRODUCT_HAS_NO_ACTIVE_SKU(
            "Sản phẩm phải có ít nhất một SKU"
    ),

    PRODUCT_SKU_BELONGS_TO_OTHER_PRODUCT(
            "SKU không thuộc sản phẩm hiện tại"
    ),

    PRODUCT_IMAGE_NOT_BELONG_TO_PRODUCT(
            "Ảnh không thuộc sản phẩm hiện tại"
    ),

    PRODUCT_CONCURRENT_MODIFICATION(
            "Dữ liệu sản phẩm đã được chỉnh sửa bởi người dùng khác, vui lòng tải lại trang"
    ),

    PRODUCT_SKU_CONCURRENT_MODIFICATION(
            "SKU đã được chỉnh sửa bởi người dùng khác"
    ),

    UPLOAD_FAILED(""),

    DELETE_IMAGE_FAILED(""),

    FILE_EMPTY(""),

    FILE_TOO_LARGE(""),

    INVALID_IMAGE_TYPE(""),

    // ===================================================================
    // 💻 SYSTEM GLOBAL MODULE (Mã lỗi Hệ thống tổng quát)
    // ===================================================================
    INTERNAL_SERVER_ERROR("Hệ thống gặp sự cố không mong muốn, vui lòng thử lại sau");

    private final String message;
}