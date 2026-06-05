package com.shoestore.config;

import com.shoestore.common.enums.user.RoleName;
import com.shoestore.entity.Role;
import com.shoestore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRole(
                RoleName.ROLE_ADMIN,
                "System Administrator"
        );

        createRole(
                RoleName.ROLE_STAFF,
                "Store Staff"
        );

        createRole(
                RoleName.ROLE_CUSTOMER,
                "Customer"
        );
    }

    private void createRole(
            RoleName roleName,
            String description
    ) {

        roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name(roleName)
                                        .description(description)
                                        .build()
                        ));
    }
}
