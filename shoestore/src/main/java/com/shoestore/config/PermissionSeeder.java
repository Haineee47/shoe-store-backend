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
        log.info("[PermissionSeeder] Starting system permission check and auto-synchronization...");

        Map<PermissionName, String> permissionsToSeed = new EnumMap<>(PermissionName.class);

        permissionsToSeed.put(PermissionName.CATEGORY_CREATE, "Create product category permission");
        permissionsToSeed.put(PermissionName.CATEGORY_VIEW, "View admin category list and details permission");
        permissionsToSeed.put(PermissionName.CATEGORY_UPDATE, "Update category information and status permission");
        permissionsToSeed.put(PermissionName.CATEGORY_DELETE, "Delete category from system permission");

        permissionsToSeed.put(PermissionName.BRAND_CREATE, "Create brand permission");
        permissionsToSeed.put(PermissionName.BRAND_VIEW, "View admin brand list and details permission");
        permissionsToSeed.put(PermissionName.BRAND_UPDATE, "Update brand information and status permission");
        permissionsToSeed.put(PermissionName.BRAND_DELETE, "Delete brand from system permission");

        permissionsToSeed.put(PermissionName.MEDIA_UPLOAD, "Upload images/documents to system permission");
        permissionsToSeed.put(PermissionName.MEDIA_DELETE, "Delete images/documents from system permission");

        int newlyCreatedCount = 0;

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
                log.info("[PermissionSeeder] ➕ Successfully added new permission: [{}]", pName.name());
            }
        }

        if (newlyCreatedCount > 0) {
            log.info("[PermissionSeeder] ✅ Completed! Automatically added {} new permissions to the database.", newlyCreatedCount);
        } else {
            log.info("[PermissionSeeder] All permissions are already fully synchronized from Enum. No seeding required.");
        }
    }
}