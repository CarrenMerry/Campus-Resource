package com.campusbooking.entity;

import com.campusbooking.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Concrete entity representing a Faculty Member.
 * 
 * Concepts Demonstrated:
 * - Inheritance: Subclass of User.
 * - Method Overriding: Overrides booking duration and quotas for academic staff.
 * - Encapsulation: Faculty-specific attributes.
 */
@Entity
@DiscriminatorValue("FACULTY")
public class Faculty extends User {

    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "designation", length = 100)
    private String designation;

    public Faculty() {
        super();
        setRole(UserRole.FACULTY);
    }

    public Faculty(String name, String email, String password, String phone, String department,
                   String employeeId, String designation) {
        super(name, email, password, phone, department);
        this.employeeId = employeeId;
        this.designation = designation;
        setRole(UserRole.FACULTY);
    }

    // ==========================================
    // METHOD OVERRIDING (Polymorphic Behavior)
    // ==========================================

    @Override
    public int getMaxBookingHoursPerDay() {
        // Faculty members can book up to 8 hours per reservation for lectures/events
        return 8;
    }

    @Override
    public int getMaxActiveBookings() {
        // Faculty members are allowed up to 10 active bookings
        return 10;
    }

    @Override
    public boolean requiresAdminApproval() {
        return false;
    }

    @Override
    public boolean canCreateBookings() {
        // Faculty members have full booking privileges
        return true;
    }

    @Override
    public String getRoleDescription() {
        return "Faculty - Full reservation privileges up to 8 hours/session & 10 active bookings. Fast-track approval.";
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
