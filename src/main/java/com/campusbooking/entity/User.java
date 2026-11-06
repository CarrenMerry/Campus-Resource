package com.campusbooking.entity;

import com.campusbooking.enums.UserRole;
import com.campusbooking.interfaces.Authenticatable;
import com.campusbooking.interfaces.Manageable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Abstract Base Entity representing a system User.
 * 
 * Concepts Demonstrated:
 * - Abstraction: Cannot be instantiated directly; concrete subclasses define specific roles.
 * - Encapsulation: All state fields are private with verified accessors and mutators.
 * - Inheritance: Subclasses Student, Faculty, and Admin extend this base class.
 * - Interfaces: Implements Authenticatable and Manageable.
 * - JPA Single-Table Inheritance mapping to the 'users' table.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements Authenticatable, Manageable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false)
    private UserRole role;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Parameterized constructor
    public User(String name, String email, String password, String phone, String department) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.department = department;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ==========================================
    // ABSTRACT METHODS (To be overridden by subclasses)
    // Concept: Polymorphism and Abstraction
    // ==========================================

    /**
     * Maximum hours per booking session allowed for this user tier.
     */
    public abstract int getMaxBookingHoursPerDay();

    /**
     * Maximum number of concurrent active/pending bookings allowed.
     */
    public abstract int getMaxActiveBookings();

    /**
     * Indicates whether bookings submitted by this user type require admin approval.
     */
    public abstract boolean requiresAdminApproval();

    /**
     * Indicates whether this user role is permitted to initiate reservations.
     * Concept: Polymorphic permission check.
     */
    public abstract boolean canCreateBookings();

    /**
     * Role-specific description summary.
     */
    public abstract String getRoleDescription();

    // ==========================================
    // INTERFACE IMPLEMENTATIONS (Authenticatable & Manageable)
    // ==========================================

    @Override
    public String getPrincipalIdentifier() {
        return this.email;
    }

    @Override
    public String getEncryptedCredential() {
        return this.password;
    }

    @Override
    public boolean isAccountActive() {
        return this.active;
    }

    @Override
    public void activate() {
        this.active = true;
    }

    @Override
    public void deactivate() {
        this.active = false;
    }

    @Override
    public void setMaintenanceMode() {
        // Users do not enter maintenance mode, but flag could be suspended
        this.active = false;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    // ==========================================
    // ENCAPSULATION: GETTERS AND SETTERS
    // ==========================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public UserRole getRole() {
        return role;
    }

    protected void setRole(UserRole role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
