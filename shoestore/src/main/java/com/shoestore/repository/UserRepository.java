package com.shoestore.repository;

import com.shoestore.entity.User;
import com.shoestore.common.enums.user.PermissionName;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.rolesAndPermissions")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findWithRolesAndPermissionsByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "JOIN u.roles r " +
            "JOIN r.permissions p " +
            "WHERE u.id = :userId AND p.name = :permissionName")
    boolean hasPermission(@Param("userId") Long userId, @Param("permissionName") PermissionName permissionName);
}