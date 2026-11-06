package com.campusbooking.service;

import com.campusbooking.entity.*;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test validating double-booking prevention and polymorphic user tier policies.
 */
public class BookingConflictTest {

    private Resource sampleLab;
    private Student student;
    private Faculty faculty;
    private Admin admin;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        ResourceCategory category = new ResourceCategory("Computer Lab", "Lab description", "bi-laptop");
        category.setCategoryId(1L);

        sampleLab = new Resource("AI Lab 1", category, "Block B", 40, "Specs", "Projector, AC", null);
        sampleLab.setResourceId(1L);
        sampleLab.setStatus(ResourceStatus.AVAILABLE);

        student = new Student("Alice Student", "alice@campus.edu", "hash", "1234567890", "MCA", "MCA001", 3);
        student.setUserId(10L);

        faculty = new Faculty("Dr. Bob", "bob@campus.edu", "hash", "1234567891", "CS", "FAC001", "Professor");
        faculty.setUserId(20L);

        admin = new Admin("Admin Carol", "carol@campus.edu", "hash", "1234567892", "IT", "ADM001", true);
        admin.setUserId(30L);

        testDate = LocalDate.now().plusDays(2);
    }

    @Test
    @DisplayName("Test Double-Booking: Identical interval must detect conflict")
    void testExactOverlap() {
        Booking existing = new Booking(student, sampleLab, testDate, LocalTime.of(10, 0), LocalTime.of(12, 0), "Lab Session", 20);
        existing.setStatus(BookingStatus.APPROVED);

        assertTrue(existing.overlapsWith(testDate, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                "Exact matching interval must trigger overlap conflict");
    }

    @Test
    @DisplayName("Test Double-Booking: Partial overlap at start and end")
    void testPartialOverlaps() {
        Booking existing = new Booking(student, sampleLab, testDate, LocalTime.of(10, 0), LocalTime.of(12, 0), "Class", 20);
        existing.setStatus(BookingStatus.APPROVED);

        // Starts earlier, ends inside (09:30 - 10:30)
        assertTrue(existing.overlapsWith(testDate, LocalTime.of(9, 30), LocalTime.of(10, 30)),
                "Requested slot overlapping start of existing booking must conflict");

        // Starts inside, ends later (11:30 - 13:00)
        assertTrue(existing.overlapsWith(testDate, LocalTime.of(11, 30), LocalTime.of(13, 0)),
                "Requested slot overlapping end of existing booking must conflict");

        // Completely enclosing (09:00 - 13:00)
        assertTrue(existing.overlapsWith(testDate, LocalTime.of(9, 0), LocalTime.of(13, 0)),
                "Requested slot encompassing existing booking must conflict");

        // Sub-interval inside (10:30 - 11:30)
        assertTrue(existing.overlapsWith(testDate, LocalTime.of(10, 30), LocalTime.of(11, 30)),
                "Requested slot inside existing booking must conflict");
    }

    @Test
    @DisplayName("Test Consecutive Slots: Adjacent back-to-back bookings should NOT conflict")
    void testAdjacentSlotsDoNotConflict() {
        Booking existing = new Booking(student, sampleLab, testDate, LocalTime.of(10, 0), LocalTime.of(12, 0), "Class", 20);
        existing.setStatus(BookingStatus.APPROVED);

        // Previous adjacent slot ending exactly at 10:00 (08:00 - 10:00)
        assertFalse(existing.overlapsWith(testDate, LocalTime.of(8, 0), LocalTime.of(10, 0)),
                "Adjacent previous booking should not conflict");

        // Following adjacent slot starting exactly at 12:00 (12:00 - 14:00)
        assertFalse(existing.overlapsWith(testDate, LocalTime.of(12, 0), LocalTime.of(14, 0)),
                "Adjacent succeeding booking should not conflict");
    }

    @Test
    @DisplayName("Test Inactive Booking Status: Cancelled or Rejected bookings do not occupy slots")
    void testCancelledBookingDoesNotBlockSlot() {
        Booking cancelled = new Booking(student, sampleLab, testDate, LocalTime.of(10, 0), LocalTime.of(12, 0), "Class", 20);
        cancelled.setStatus(BookingStatus.CANCELLED);

        assertFalse(cancelled.overlapsWith(testDate, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                "Cancelled booking must release slot and not trigger conflict");

        Booking rejected = new Booking(student, sampleLab, testDate, LocalTime.of(10, 0), LocalTime.of(12, 0), "Class", 20);
        rejected.setStatus(BookingStatus.REJECTED);

        assertFalse(rejected.overlapsWith(testDate, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                "Rejected booking must release slot and not trigger conflict");
    }

    @Test
    @DisplayName("Test Polymorphic Behavior: Student vs Faculty vs Admin booking allowances")
    void testPolymorphicTierPolicies() {
        // Concept: Polymorphism - calling methods on abstract User reference behaves according to concrete type
        User u1 = student;
        User u2 = faculty;
        User u3 = admin;

        assertEquals(3, u1.getMaxBookingHoursPerDay(), "Student allowed max 3 hours");
        assertEquals(8, u2.getMaxBookingHoursPerDay(), "Faculty allowed max 8 hours");
        assertEquals(24, u3.getMaxBookingHoursPerDay(), "Admin allowed full 24 hours");

        assertEquals(3, u1.getMaxActiveBookings(), "Student allowed max 3 active bookings");
        assertEquals(10, u2.getMaxActiveBookings(), "Faculty allowed max 10 active bookings");
        assertEquals(999, u3.getMaxActiveBookings(), "Admin allowed unlimited active bookings");

        assertTrue(u1.requiresAdminApproval(), "Student requires admin approval");
        assertFalse(u2.requiresAdminApproval(), "Faculty is fast-tracked");
        assertFalse(u3.requiresAdminApproval(), "Admin requires no approval");

        // Role-based booking permissions: Students are restricted to view-only access
        assertFalse(u1.canCreateBookings(), "Student cannot create bookings - read-only access");
        assertTrue(u2.canCreateBookings(), "Faculty has reservation creation privileges");
        assertTrue(u3.canCreateBookings(), "Admin has reservation creation privileges");
    }
}
