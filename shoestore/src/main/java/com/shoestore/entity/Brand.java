package com.shoestore.entity;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_brand_slug", columnNames = "slug"),
                @UniqueConstraint(name = "uk_brand_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_brand_name", columnList = "name"), // 🌟 Tối ưu câu query LIKE của Admin Search
                @Index(name = "idx_brand_slug", columnList = "slug"),
                @Index(name = "idx_brand_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String logoUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer sortOrder = 0;
}