package com.shoestore.config;

import com.shoestore.common.enums.user.PermissionName;
import com.shoestore.common.enums.user.RoleName;
import com.shoestore.entity.Permission;
import com.shoestore.entity.Role;
import com.shoestore.repository.PermissionRepository;
import com.shoestore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional // Rất quan trọng để duy trì Session làm việc với @ManyToMany
    public void run(String... args) {

        // 1. Định nghĩa danh sách Quyền hạn cần có trong hệ thống
        Map<PermissionName, String> requiredPermissions = Map.of(
                PermissionName.CATEGORY_CREATE, "Quyền tạo mới danh mục",
                PermissionName.CATEGORY_VIEW, "Quyền xem danh mục quản trị",
                PermissionName.CATEGORY_UPDATE, "Quyền sửa đổi thông tin danh mục",
                PermissionName.CATEGORY_DELETE, "Quyền xóa danh mục",
                PermissionName.PRODUCT_VIEW, "Quyền xem sản phẩm",
                PermissionName.PRODUCT_CREATE, "Quyền tạo sản phẩm",
                PermissionName.PRODUCT_UPDATE, "Quyền sửa sản phẩm",
                PermissionName.PRODUCT_DELETE, "Quyền xóa sản phẩm",
                PermissionName.ORDER_UPDATE, "Quyền cập nhật trạng thái đơn hàng"
        );

        // 🚀 TỐI ƯU BƯỚC 1: Chỉ gọi đúng 1 câu SELECT lấy toàn bộ Permission hiện tại
        List<Permission> existingPermissionsInDb = permissionRepository.findAll();
        Map<PermissionName, Permission> permissionMap = existingPermissionsInDb.stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        // Lọc xem quyền nào trong Map "requiredPermissions" chưa có ở DB thì tiến hành lưu batch
        List<Permission> permissionsToSave = requiredPermissions.entrySet().stream()
                .filter(entry -> !permissionMap.containsKey(entry.getKey()))
                .map(entry -> Permission.builder()
                        .name(entry.getKey())
                        .description(entry.getValue())
                        .build())
                .toList();

        if (!permissionsToSave.isEmpty()) {
            permissionRepository.saveAll(permissionsToSave);
            // Nạp lại danh sách mới sau khi insert bổ sung thành công
            existingPermissionsInDb = permissionRepository.findAll();
        }

        // Tạo tập hợp chứa tất cả Permission
        Set<Permission> allPermissions = Set.copyOf(existingPermissionsInDb);

        // Lọc tập quyền giới hạn cho Staff
        Set<Permission> staffPermissions = allPermissions.stream()
                .filter(p -> p.getName() == PermissionName.CATEGORY_VIEW
                        || p.getName() == PermissionName.ORDER_UPDATE)
                .collect(Collectors.toSet());

        // 2. Đồng bộ các Vai trò và tập Quyền tương ứng
        syncRolePermissions(RoleName.ROLE_ADMIN, "System Administrator", allPermissions);
        syncRolePermissions(RoleName.ROLE_STAFF, "Store Staff", staffPermissions);
        syncRolePermissions(RoleName.ROLE_CUSTOMER, "Customer", Set.of());
    }

    /**
     * Đồng bộ hóa thông minh: Không phá vỡ PersistentSet, không save thừa nếu dữ liệu giữ nguyên
     */
    private void syncRolePermissions(RoleName roleName, String description, Set<Permission> targetPermissions) {
        roleRepository.findByName(roleName).ifPresentOrElse(
                existingRole -> {
                    // 🚀 TỐI ƯU BƯỚC 2 & 3: So sánh tập quyền hiện tại với tập quyền mong muốn
                    // Nhờ có @EqualsAndHashCode(of = "name"), hàm .equals() này sẽ chạy đúng bản chất dữ liệu
                    if (!existingRole.getPermissions().equals(targetPermissions)) {

                        // GIỮ NGUYÊN Ô NHỚ CŨ (PersistentSet), chỉ clear và nạp lại phần tử bên trong
                        existingRole.getPermissions().clear();
                        existingRole.getPermissions().addAll(targetPermissions);

                        // Chỉ lưu khi thực sự có sự thay đổi quyền
                        roleRepository.save(existingRole);
                    }
                    // Nếu tập quyền bằng nhau -> Bỏ qua hoàn toàn, KHÔNG SINH RA SQL THỪA!
                },
                () -> {
                    // Nếu Role chưa tồn tại, tiến hành tạo mới hoàn toàn
                    roleRepository.save(
                            Role.builder()
                                    .name(roleName)
                                    .description(description)
                                    .permissions(targetPermissions)
                                    .build()
                    );
                }
        );
    }
}