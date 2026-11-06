package com.campusbooking.entity;

import com.campusbooking.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Concrete entity representing a System Administrator.
 * 
 * Concepts Demonstrated:
 * - Inheritance: Subclass of User.
 * - Method Overriding: Overrides booking limitations with full administrative privilege.
 * - Encapsulation: Admin-specific fields.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(name = "admin_code", length = 50)
    private String adminCode;

    @Column(name = "is_super_admin")
    private boolean superAdmin = true;

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(String name, String email, String password, String phone, String department,
                 String adminCode, boolean superAdmin) {
        super(name, email, password, phone, department);
        this.adminCode = adminCode;
        this.superAdmin = superAdmin;
        setRole(UserRole.ADMIN);
    }

    // ==========================================
    // METHOD OVERRIDING (Polymorphic Behavior)
    // ==========================================

    @Override
    public int getMaxBookingHoursPerDay() {
        // Administrators have 24-hour booking capability for institution schedules
        return 24;
    }

    @Override
    public int getMaxActiveBookings() {
        // Unlimited active bookings for administrative scheduling
        return 999;
    }

    @Override
    public boolean requiresAdminApproval() {
        // Direct administrative approval
        return false;
    }

    @Override
    public boolean canCreateBookings() {
        // Administrators have full booking privileges
        return true;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator - Full access to all campus resources, approvals, and system reports.";
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}
