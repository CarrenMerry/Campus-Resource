package com.campusbooking.entity;

import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.interfaces.Bookable;
import com.campusbooking.interfaces.Manageable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a physical bookable campus resource (classroom, lab, hall, etc.).
 * 
 * Concepts Demonstrated:
 * - Encapsulation: Private member variables with validation and getters/setters.
 * - Interface Implementation: Implements Bookable and Manageable contracts.
 * - Relational Mapping: Many-to-One association with ResourceCategory.
 */
@Entity
@Table(name = "resources", indexes = {
    @Index(name = "idx_resource_status", columnList = "status"),
    @Index(name = "idx_resource_category", columnList = "category_id")
})
public class Resource implements Bookable, Manageable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @NotBlank(message = "Resource name is required")
    @Size(min = 2, max = 150)
    @Column(name = "resource_name", nullable = false, length = 150)
    private String resourceName;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private ResourceCategory category;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 200)
    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1 person")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "facilities", length = 500)
    private String facilities;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ResourceStatus status = ResourceStatus.AVAILABLE;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Resource() {
        this.createdAt = LocalDateTime.now();
        this.status = ResourceStatus.AVAILABLE;
    }

    public Resource(String resourceName, ResourceCategory category, String location, 
                    Integer capacity, String description, String facilities, String imageUrl) {
        this();
        this.resourceName = resourceName;
        this.category = category;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.facilities = facilities;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ==========================================
    // INTERFACE IMPLEMENTATION: Bookable
    // ==========================================

    @Override
    public boolean checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Basic operational availability check on the resource itself
        if (this.status != ResourceStatus.AVAILABLE) {
            return false;
        }
        // Booking date must not be in past
        if (date == null || date.isBefore(LocalDate.now())) {
            return false;
        }
        // Valid time window
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    @Override
    public boolean validateCapacity(int requestedCapacity) {
        return requestedCapacity <= 0 || (this.capacity != null && requestedCapacity <= this.capacity);
    }

    @Override
    public boolean isBookableNow() {
        return this.status == ResourceStatus.AVAILABLE;
    }

    // ==========================================
    // INTERFACE IMPLEMENTATION: Manageable
    // ==========================================

    @Override
    public void activate() {
        this.status = ResourceStatus.AVAILABLE;
    }

    @Override
    public void deactivate() {
        this.status = ResourceStatus.INACTIVE;
    }

    @Override
    public void setMaintenanceMode() {
        this.status = ResourceStatus.MAINTENANCE;
    }

    @Override
    public boolean isActive() {
        return this.status == ResourceStatus.AVAILABLE;
    }

    // ==========================================
    // ENCAPSULATION: GETTERS AND SETTERS
    // ==========================================

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

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
        this.category = category;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
