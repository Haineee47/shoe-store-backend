package com.shoestore.config;

import com.shoestore.common.enums.user.PermissionName;
import com.shoestore.entity.Permission;
import com.shoestore.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionSeeder implements ApplicationRunner {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("🚀 [PermissionSeeder] Bắt đầu kiểm tra và tự động đồng bộ quyền hệ thống...");

        // Tối ưu hiệu năng bằng cách dùng EnumMap (Dành riêng cho Key là Enum trong Java)
        Map<PermissionName, String> permissionsToSeed = new EnumMap<>(PermissionName.class);

        // 🌟 CATEGORY MANAGEMENT PERMISSIONS (Phase 2.1)
        // Lưu ý: Đảm bảo các giá trị CATEGORY_CREATE, CATEGORY_VIEW... đã được khai báo trong Enum PermissionName của bạn
        permissionsToSeed.put(PermissionName.CATEGORY_CREATE, "Quyền tạo mới danh mục sản phẩm");
        permissionsToSeed.put(PermissionName.CATEGORY_VIEW, "Quyền xem danh sách và chi tiết danh mục quản trị");
        permissionsToSeed.put(PermissionName.CATEGORY_UPDATE, "Quyền cập nhật thông tin và trạng thái danh mục");
        permissionsToSeed.put(PermissionName.CATEGORY_DELETE, "Quyền xóa danh mục khỏi hệ thống");

        // 🌟 BRAND MANAGEMENT PERMISSIONS (Phase 2.2)
        permissionsToSeed.put(PermissionName.BRAND_CREATE, "Quyền tạo mới thương hiệu");
        permissionsToSeed.put(PermissionName.BRAND_VIEW, "Quyền xem danh sách và chi tiết thương hiệu quản trị");
        permissionsToSeed.put(PermissionName.BRAND_UPDATE, "Quyền cập nhật thông tin và trạng thái thương hiệu");
        permissionsToSeed.put(PermissionName.BRAND_DELETE, "Quyền xóa thương hiệu khỏi hệ thống");

        // 🌟 MEDIA MANAGEMENT PERMISSIONS (Sẵn sàng đón đầu Phase 2.25)
        permissionsToSeed.put(PermissionName.MEDIA_UPLOAD, "Quyền tải lên hình ảnh/tài liệu lên hệ thống");
        permissionsToSeed.put(PermissionName.MEDIA_DELETE, "Quyền xóa hình ảnh/tài liệu khỏi hệ thống");

        int newlyCreatedCount = 0;

        // Vòng lặp Idempotent kiểm tra dựa trên Enum Key
        for (Map.Entry<PermissionName, String> entry : permissionsToSeed.entrySet()) {
            PermissionName pName = entry.getKey();
            String description = entry.getValue();

            if (!permissionRepository.existsByName(pName)) {
                Permission permission = Permission.builder()
                        .name(pName)
                        .description(description)
                        .build();

                permissionRepository.save(permission);
                newlyCreatedCount++;
                log.info("➕ Đã bổ sung quyền mới thành công: [{}]", pName.name());
            }
        }

        if (newlyCreatedCount > 0) {
            log.info("✅ [PermissionSeeder] Hoàn thành! Đã tự động thêm mới {} quyền vào Database.", newlyCreatedCount);
        } else {
            log.info("⚡ [PermissionSeeder] Dữ liệu quyền đã đồng bộ đầy đủ từ Enum. Không cần thêm mới.");
        }
    }
}