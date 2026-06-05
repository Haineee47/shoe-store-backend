package com.shoestore.entity;

import com.shoestore.common.enums.user.PermissionName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "name") // 🔥 BỔ SUNG DÒNG NÀY
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private PermissionName name;

    @Column(length = 255)
    private String description;
}