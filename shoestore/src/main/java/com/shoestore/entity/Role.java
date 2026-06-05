package com.shoestore.entity;

import com.shoestore.common.enums.user.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            unique = true,
            length = 50
    )
    private RoleName name;

    @Column(length = 255)
    private String description;
}