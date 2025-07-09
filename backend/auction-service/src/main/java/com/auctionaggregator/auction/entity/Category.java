package com.auctionaggregator.auction.entity;

import com.auctionaggregator.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(unique = true)
    private String slug;
    
    private String description;
    
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer level = 0;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @Column(nullable = false)
    private Boolean featured = false;
    
    @ElementCollection
    @CollectionTable(name = "category_attributes", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "attribute")
    private List<String> requiredAttributes = new ArrayList<>();
    
    @Column(columnDefinition = "jsonb")
    private String metadata; // JSON field for flexible category-specific data
}