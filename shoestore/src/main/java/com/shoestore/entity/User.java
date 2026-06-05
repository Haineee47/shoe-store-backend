package com.shoestore.entity;

import com.shoestore.common.enums.user.AuthProvider;
import com.shoestore.common.enums.user.RoleName;
import com.shoestore.common.enums.user.UserStatus;
import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(
                        name = "idx_user_email",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 255
    )
    private String email;

    @Column(length = 255)
    private String password;

    @Column(
            nullable = false,
            length = 100
    )
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Boolean emailVerified = false;

    private LocalDateTime lastLoginAt;

    @Builder.Default
    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean accountLocked = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")
            }
    )
    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(RoleName roleName) {

        return roles.stream()
                .anyMatch(role ->
                        role.getName() == roleName);
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(length = 100)
    private String providerId;
}