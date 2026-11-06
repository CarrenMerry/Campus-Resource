package com.campusbooking.dto;

import com.campusbooking.entity.Resource;
import com.campusbooking.enums.ResourceStatus;

import java.time.LocalDateTime;

public class ResourceResponseDto {

    private Long resourceId;
    private String resourceName;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String location;
    private Integer capacity;
    private String description;
    private String facilities;
    private ResourceStatus status;
    private String statusDescription;
    private String badgeColor;
    private String imageUrl;
    private boolean bookable;
    private LocalDateTime createdAt;

    public ResourceResponseDto() {}

    public static ResourceResponseDto fromEntity(Resource resource) {
        if (resource == null) return null;
        ResourceResponseDto dto = new ResourceResponseDto();
        dto.setResourceId(resource.getResourceId());
        dto.setResourceName(resource.getResourceName());
        dto.setLocation(resource.getLocation());
        dto.setCapacity(resource.getCapacity());
        dto.setDescription(resource.getDescription());
        dto.setFacilities(resource.getFacilities());
        dto.setStatus(resource.getStatus());
        dto.setStatusDescription(resource.getStatus() != null ? resource.getStatus().getDescription() : "");
        dto.setBadgeColor(resource.getStatus() != null ? resource.getStatus().getBadgeColor() : "secondary");
        dto.setImageUrl(resource.getImageUrl());
        dto.setBookable(resource.isBookableNow());
        dto.setCreatedAt(resource.getCreatedAt());

        if (resource.getCategory() != null) {
            dto.setCategoryId(resource.getCategory().getCategoryId());
            dto.setCategoryName(resource.getCategory().getCategoryName());
            dto.setCategoryIcon(resource.getCategory().getIconClass());
        }

        return dto;
    }

    // Getters and Setters
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

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

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String location() {
        return location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isBookable() {
        return bookable;
    }

    public void setBookable(boolean bookable) {
        this.bookable = bookable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
