package com.campusbooking.entity;

import com.campusbooking.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Concrete entity representing a Student.
 * 
 * Concepts Demonstrated:
 * - Inheritance: Extends User base class.
 * - Method Overriding: Customizes booking allowances and verification rules.
 * - Encapsulation: Private student-specific fields with getters and setters.
 */
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(name = "student_roll_number", length = 50)
    private String studentRollNumber;

    @Column(name = "semester")
    private Integer semester;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    public Student(String name, String email, String password, String phone, String department, 
                   String studentRollNumber, Integer semester) {
        super(name, email, password, phone, department);
        this.studentRollNumber = studentRollNumber;
        this.semester = semester;
        setRole(UserRole.STUDENT);
    }

    // ==========================================
    // METHOD OVERRIDING (Polymorphic Behavior)
    // ==========================================

    @Override
    public int getMaxBookingHoursPerDay() {
        // Students are restricted to maximum 3 consecutive hours per reservation
        return 3;
    }

    @Override
    public int getMaxActiveBookings() {
        // Students can have at most 3 active (pending/approved) bookings
        return 3;
    }

    @Override
    public boolean requiresAdminApproval() {
        return true;
    }

    @Override
    public boolean canCreateBookings() {
        // Students are read-only: booking access is restricted to Faculty and Administrators
        return false;
    }

    @Override
    public String getRoleDescription() {
        return "Student - View-only access to campus resources and schedules. Reservations reserved for faculty & admin.";
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }
}
