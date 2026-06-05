package com.shoestore.entity;

import com.shoestore.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_category_slug", columnList = "slug"),
                @Index(name = "idx_category_parent", columnList = "parent_id"),
                @Index(name = "idx_category_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

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
    private String imageUrl; // 🌟 Đã thêm trường ảnh phục vụ Frontend hiển thị icon/banner

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer sortOrder = 0; // 🌟 Đã thêm trường sắp xếp vị trí hiển thị Menu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 🌟 Đã loại bỏ cascade = CascadeType.ALL nguy hiểm để bảo vệ dữ liệu con
    @Builder.Default
    @OneToMany(mappedBy = "parent")
    @OrderBy("sortOrder ASC, id ASC")
    private List<Category> children = new ArrayList<>();
}