package com.shoestore.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===================================================================
    // 🔒 AUTHENTICATION & USER MODULE (Mã lỗi Xác thực & Người dùng)
    // ===================================================================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Người dùng không tồn tại trên hệ thống"),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Thông tin tài khoản hoặc mật khẩu không chính xác"),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "Tài khoản đã bị khóa do nhập sai quá nhiều lần"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Tài khoản hiện đang bị vô hiệu hóa"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Vai trò người dùng không tồn tại"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào chức năng này"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Yêu cầu không hợp lệ, vui lòng đăng nhập lại"),

    // Email Verification & Password Reset
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email này đã được sử dụng bởi tài khoản khác"),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Email tài khoản chưa được xác thực"),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "Email này đã được xác thực trước đó"),
    PLEASE_WAIT_BEFORE_RESENDING(HttpStatus.TOO_MANY_REQUESTS, "Vui lòng đợi 1 phút trước khi yêu cầu gửi lại mã"),
    INCORRECT_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không chính xác"),
    NEW_PASSWORD_MUST_BE_DIFFERENT(HttpStatus.BAD_REQUEST, "Mật khẩu mới không được trùng với mật khẩu hiện tại"),
    SOCIAL_ACCOUNT_CANNOT_CHANGE_PASSWORD(HttpStatus.BAD_REQUEST, "Tài khoản đăng nhập bằng mạng xã hội không thể đổi mật khẩu trực tiếp"),
    INVALID_RESET_TOKEN(HttpStatus.BAD_REQUEST, "Mã khôi phục mật khẩu không hợp lệ"),
    RESET_TOKEN_EXPIRED(HttpStatus.GONE, "Mã khôi phục mật khẩu đã hết hạn"),

    // JWT Tokens
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Mã token không hợp lệ hoặc đã bị thay đổi"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Mã token đã hết hạn, vui lòng đăng nhập lại"),
    TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, "Mã token này đã được sử dụng"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy Refresh Token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token đã hết hạn, vui lòng đăng nhập lại"),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "Refresh Token đã bị thu hồi hoặc đăng xuất"),

    // Google OAuth2
    GOOGLE_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Mã Google ID Token không hợp lệ"),
    GOOGLE_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Xác thực qua Google không thành công"),
    GOOGLE_EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Tài khoản Google này chưa được xác thực email"),

    // ===================================================================
    // 🏷️ CATEGORY MODULE (Mã lỗi Danh mục sản phẩm)
    // ===================================================================
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Danh mục sản phẩm không tồn tại"),
    CATEGORY_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên danh mục này đã tồn tại"),
    CATEGORY_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Đường dẫn (Slug) danh mục này đã tồn tại"),
    PARENT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục cha"),
    CATEGORY_CANNOT_BE_ITS_OWN_PARENT(HttpStatus.BAD_REQUEST, "Một danh mục không thể chọn chính nó làm danh mục cha"),
    CATEGORY_HAS_CHILDREN_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "Danh mục này đang chứa danh mục con, không thể xóa"),
    CATEGORY_CIRCULAR_REFERENCE(HttpStatus.BAD_REQUEST, "Danh mục cha không hợp lệ, phát hiện vòng lặp danh mục"),

    // ===================================================================
    // 👟 BRAND MODULE (Mã lỗi Thương hiệu)
    // ===================================================================
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "Thương hiệu không tồn tại"),
    BRAND_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên thương hiệu này đã tồn tại"),
    BRAND_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Đường dẫn (Slug) thương hiệu này đã tồn tại"),

    // ===================================================================
    // 📁 MEDIA & CLOUDINARY MODULE (Mã lỗi File & Lưu trữ hình ảnh)
    // ===================================================================
    MEDIA_FILENAME_INVALID(HttpStatus.BAD_REQUEST, "Tên tệp tin không hợp lệ hoặc chứa mã độc Path Traversal"),
    MEDIA_FILE_EMPTY(HttpStatus.BAD_REQUEST, "Tệp tin tải lên trống rỗng, vui lòng chọn lại file"),
    MEDIA_FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "Kích thước tệp vượt quá giới hạn cho phép (Tối đa 2MB)"),
    MEDIA_FILE_TYPE_NOT_ALLOWED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Định dạng tệp không được hỗ trợ (Chỉ chấp nhận JPEG, PNG, WEBP)"),
    MEDIA_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Quá trình tải tệp tin lên hệ thống Cloudinary thất bại"),
    MEDIA_PUBLIC_ID_INVALID(HttpStatus.BAD_REQUEST, "Mã định danh file (Public ID) trên hệ thống Cloud không hợp lệ"),
    MEDIA_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa tệp tin cũ khỏi hệ thống lưu trữ đám mây"),

    // ===================================================================
    // 👟 PRODUCT & SKU MODULE (Mã lỗi Sản phẩm & Biến thể)
    // ===================================================================
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"),
    PRODUCT_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên sản phẩm đã tồn tại"),
    PRODUCT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "Sản phẩm đã bị xóa"),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Hình ảnh sản phẩm không tồn tại"),
    PRODUCT_VARIANT_DUPLICATE(HttpStatus.CONFLICT, "Biến thể sản phẩm bị trùng màu sắc và kích cỡ"),
    PRODUCT_HAS_NO_ACTIVE_SKU(HttpStatus.BAD_REQUEST, "Sản phẩm phải có ít nhất một SKU"),
    PRODUCT_SKU_BELONGS_TO_OTHER_PRODUCT(HttpStatus.BAD_REQUEST, "SKU không thuộc sản phẩm hiện tại"),
    PRODUCT_IMAGE_NOT_BELONG_TO_PRODUCT(HttpStatus.BAD_REQUEST, "Ảnh không thuộc sản phẩm hiện tại"),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "Tên sản phẩm không hợp lệ"),
    PRODUCT_MISSING_THUMBNAIL(HttpStatus.BAD_REQUEST, "Sản phẩm thiếu ảnh đại diện (Thumbnail)"),

    // Luồng concurrency chống ghi đè dữ liệu (Optimistic Locking)
    PRODUCT_CONCURRENT_MODIFICATION(HttpStatus.CONFLICT, "Dữ liệu sản phẩm đã được chỉnh sửa bởi người dùng khác, vui lòng tải lại trang"),
    PRODUCT_SKU_CONCURRENT_MODIFICATION(HttpStatus.CONFLICT, "SKU đã được chỉnh sửa bởi người dùng khác"),
    SKU_CONCURRENT_MODIFICATION(HttpStatus.CONFLICT, "Phiên bản sản phẩm đã bị thay đổi bởi giao dịch khác, vui lòng thử lại."),

    // Các mã lỗi liên quan đến SKU vật lý & Kho hàng
    PRODUCT_SKU_NOT_FOUND(HttpStatus.NOT_FOUND, "SKU không tồn tại trong hệ thống"),
    PRODUCT_SKU_DUPLICATE_IN_REQUEST(HttpStatus.BAD_REQUEST, "Danh sách SKU gửi lên bị trùng mã SKU"),
    PRODUCT_SKU_ALREADY_EXISTS(HttpStatus.CONFLICT, "Mã SKU đã tồn tại trong hệ thống"),
    SKU_STOCK_INSUFFICIENT(HttpStatus.BAD_REQUEST, "Số lượng tồn kho không đủ"),
    SKU_DISCONTINUED(HttpStatus.BAD_REQUEST, "SKU này hiện đã ngừng kinh doanh"),

    // Product Sku Invariants (Quy tắc toàn vẹn nghiệp vụ sản phẩm)
    SKU_COST_PRICE_INVALID(HttpStatus.BAD_REQUEST, "Giá vốn SKU phải lớn hơn hoặc bằng 0"),
    SKU_SELLING_PRICE_INVALID(HttpStatus.BAD_REQUEST, "Giá bán SKU phải lớn hơn 0"),
    SKU_PRICE_CONFLICT(HttpStatus.BAD_REQUEST, "Giá bán lẻ không được phép nhỏ hơn giá vốn"),
    SKU_WEIGHT_INVALID(HttpStatus.BAD_REQUEST, "Trọng lượng sản phẩm phải lớn hơn 0"),
    SKU_DIMENSION_INVALID(HttpStatus.BAD_REQUEST, "Kích thước hình học (Dài, rộng, cao) phải lớn hơn 0"),
    SKU_LOW_STOCK_THRESHOLD_INVALID(HttpStatus.BAD_REQUEST, "Ngưỡng báo động sắp hết hàng phải lớn hơn hoặc bằng 0"),
    SKU_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "Mã định danh SKU không được bỏ trống"),
    SKU_METADATA_REQUIRED(HttpStatus.BAD_REQUEST, "Thông tin cấu hình kích cỡ và màu sắc biến thể không được trống"),

    // ===================================================================
    // 💻 SYSTEM GLOBAL MODULE (Mã lỗi Hệ thống tổng quát)
    // ===================================================================
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Hệ thống gặp sự cố không mong muốn, vui lòng thử lại sau"),
    BUSINESS_ERROR(HttpStatus.BAD_REQUEST, "Lỗi nghiệp vụ hệ thống xảy ra");

    private final HttpStatus httpStatus;
    private final String message;

    // Constructor bắt buộc viết thủ công cho Enum chứa nhiều biến trường dữ liệu
    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}