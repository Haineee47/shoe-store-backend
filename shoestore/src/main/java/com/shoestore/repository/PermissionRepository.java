package com.shoestore.repository;

import com.shoestore.common.enums.user.PermissionName;
import com.shoestore.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionName name);
}