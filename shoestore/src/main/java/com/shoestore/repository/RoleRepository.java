package com.shoestore.repository;

import com.shoestore.common.enums.user.RoleName;
import com.shoestore.entity.Role;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}