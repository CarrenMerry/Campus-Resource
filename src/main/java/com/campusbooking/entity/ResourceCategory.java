package com.campusbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity representing a category of campus resources (e.g., Computer Lab, Seminar Hall, Classroom).
 * Concept: Encapsulation, Relational Entity.
 */
@Entity
@Table(name = "resource_categories")
public class ResourceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100)
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "icon_class", length = 50)
    private String iconClass = "bi-building";

    public ResourceCategory() {
    }

    public ResourceCategory(String categoryName, String description, String iconClass) {
        this.categoryName = categoryName;
        this.description = description;
        this.iconClass = iconClass;
    }

    // ==========================================
    // GETTERS AND SETTERS (Encapsulation)
    // ==========================================

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
}
