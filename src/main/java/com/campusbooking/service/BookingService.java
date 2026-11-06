package com.campusbooking.service;

import com.campusbooking.dto.BookingRequest;
import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service interface governing reservation lifecycles and concurrency rules.
 * Concepts: Method Overloading, Double Booking Conflict Detection, Layered Architecture.
 */
public interface BookingService {

    // ==========================================
    // METHOD OVERLOADING (Querying Bookings)
    // ==========================================

    List<BookingResponseDto> getBookings();

    List<BookingResponseDto> getBookings(BookingStatus status);

    List<BookingResponseDto> getBookings(Long userId);

    List<BookingResponseDto> getBookings(Long userId, BookingStatus status);

    List<BookingResponseDto> getBookings(LocalDate fromDate, LocalDate toDate);

    List<BookingResponseDto> getBookingsForResourceAndDate(Long resourceId, LocalDate date);

    // ==========================================
    // CORE RESERVATION WORKFLOWS
    // ==========================================

    BookingResponseDto getBookingById(Long bookingId);

    BookingResponseDto createBooking(Long userId, BookingRequest request);

    BookingResponseDto cancelBooking(Long bookingId, Long userId, UserRole userRole);

    BookingResponseDto approveBooking(Long bookingId, String adminRemark);

    BookingResponseDto rejectBooking(Long bookingId, String adminRemark);

    BookingResponseDto completeBooking(Long bookingId);

    BookingResponseDto checkInBooking(Long bookingId);

    BookingResponseDto verifyAndCheckIn(String checkinCode);

    // Availability validation check
    boolean isSlotAvailable(Long resourceId, LocalDate date, LocalTime startTime, LocalTime endTime);
}
