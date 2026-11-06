package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.BookingRequest;
import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.dto.StatusUpdateRequest;
import com.campusbooking.entity.User;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.UserRole;
import com.campusbooking.service.BookingService;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for booking transactions, status updates, and lifecycle management.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtil securityUtil;

    public BookingController(BookingService bookingService, SecurityUtil securityUtil) {
        this.bookingService = bookingService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getBookings(
            @RequestParam(required = false) BookingStatus status,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);

        List<BookingResponseDto> bookings;
        if (user.getRole() == UserRole.ADMIN) {
            bookings = status != null ? bookingService.getBookings(status) : bookingService.getBookings();
        } else {
            bookings = bookingService.getBookings(user.getUserId(), status);
        }

        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingById(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        BookingResponseDto booking = bookingService.getBookingById(id);

        if (user.getRole() != UserRole.ADMIN && !booking.getUserId().equals(user.getUserId())) {
            throw new com.campusbooking.exception.UnauthorizedOperationException("You are not authorized to view this booking.");
        }

        return ResponseEntity.ok(ApiResponse.ok("Booking details retrieved", booking));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        BookingResponseDto created = bookingService.createBooking(user.getUserId(), bookingRequest);
        return new ResponseEntity<>(ApiResponse.ok("Booking reservation submitted successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDto>> cancelBooking(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        BookingResponseDto cancelled = bookingService.cancelBooking(id, user.getUserId(), user.getRole());
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", cancelled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> deleteBooking(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        return cancelBooking(id, request);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<BookingResponseDto>> approveBooking(
            @PathVariable("id") Long id,
            @RequestBody(required = false) StatusUpdateRequest updateRequest,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        String remark = updateRequest != null ? updateRequest.getRemark() : "Approved by administrator";
        BookingResponseDto approved = bookingService.approveBooking(id, remark);
        return ResponseEntity.ok(ApiResponse.ok("Booking approved successfully", approved));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResponseDto>> rejectBooking(
            @PathVariable("id") Long id,
            @RequestBody(required = false) StatusUpdateRequest updateRequest,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        String remark = (updateRequest != null && updateRequest.getRemark() != null) ?
                updateRequest.getRemark() : "Rejected by administrator";
        BookingResponseDto rejected = bookingService.rejectBooking(id, remark);
        return ResponseEntity.ok(ApiResponse.ok("Booking rejected", rejected));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<BookingResponseDto>> completeBooking(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        BookingResponseDto completed = bookingService.completeBooking(id);
        return ResponseEntity.ok(ApiResponse.ok("Booking marked as completed", completed));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<BookingResponseDto>> checkInBooking(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        BookingResponseDto checkedIn = bookingService.checkInBooking(id);
        return ResponseEntity.ok(ApiResponse.ok("Attendee check-in verified successfully", checkedIn));
    }

    @PostMapping("/verify-checkin")
    public ResponseEntity<ApiResponse<BookingResponseDto>> verifyCheckinCode(
            @RequestParam("code") String code,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        BookingResponseDto checkedIn = bookingService.verifyAndCheckIn(code);
        return ResponseEntity.ok(ApiResponse.ok("Pass verified and check-in confirmed!", checkedIn));
    }
}
