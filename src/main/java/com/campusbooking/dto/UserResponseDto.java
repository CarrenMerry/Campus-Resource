package com.campusbooking.dto;

import com.campusbooking.entity.Admin;
import com.campusbooking.entity.Faculty;
import com.campusbooking.entity.Student;
import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Clean User DTO returned by API endpoints.
 * Never exposes hashed password credentials.
 * Concepts: Encapsulation, Polymorphic instantiation from User subclasses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
    
    // Subclass specific fields
    private String studentRollNumber;
    private Integer semester;
    private String employeeId;
    private String designation;
    private String adminCode;
    private String roleDescription;
    private int maxBookingHours;
    private int maxActiveBookings;
    private boolean canCreateBookings;

    public UserResponseDto() {}

    /**
     * Polymorphic factory method creating DTO from abstract User entity.
     */
    public static UserResponseDto fromEntity(User user) {
        if (user == null) return null;
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRoleDescription(user.getRoleDescription());
        dto.setMaxBookingHours(user.getMaxBookingHoursPerDay());
        dto.setMaxActiveBookings(user.getMaxActiveBookings());
        dto.setCanCreateBookings(user.canCreateBookings());

        if (user instanceof Student student) {
            dto.setStudentRollNumber(student.getStudentRollNumber());
            dto.setSemester(student.getSemester());
        } else if (user instanceof Faculty faculty) {
            dto.setEmployeeId(faculty.getEmployeeId());
            dto.setDesignation(faculty.getDesignation());
        } else if (user instanceof Admin admin) {
            dto.setAdminCode(admin.getAdminCode());
        }
        return dto;
    }

    // Getters and Setters
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
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

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public int getMaxBookingHours() {
        return maxBookingHours;
    }

    public void setMaxBookingHours(int maxBookingHours) {
        this.maxBookingHours = maxBookingHours;
    }

    public int getMaxActiveBookings() {
        return maxActiveBookings;
    }

    public void setMaxActiveBookings(int maxActiveBookings) {
        this.maxActiveBookings = maxActiveBookings;
    }

    public boolean isCanCreateBookings() {
        return canCreateBookings;
    }

    public void setCanCreateBookings(boolean canCreateBookings) {
        this.canCreateBookings = canCreateBookings;
    }
}
