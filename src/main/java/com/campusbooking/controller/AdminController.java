package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.dto.ReportSummaryDto;
import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.UserRole;
import com.campusbooking.service.ReportService;
import com.campusbooking.service.UserService;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Administrator operations (User control and Reporting).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;
    private final SecurityUtil securityUtil;

    public AdminController(UserService userService, ReportService reportService, SecurityUtil securityUtil) {
        this.userService = userService;
        this.reportService = reportService;
        this.securityUtil = securityUtil;
    }

    // ==========================================
    // USER MANAGEMENT
    // ==========================================

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String search,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        List<UserResponseDto> users = userService.searchUsers(role, search);
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User retrieved", user));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> toggleUserStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        Boolean active = body.getOrDefault("active", true);
        UserResponseDto updated = userService.toggleUserStatus(id, active);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    // ==========================================
    // REPORTING AND ANALYTICS
    // ==========================================

    @GetMapping("/reports/summary")
    public ResponseEntity<ApiResponse<ReportSummaryDto>> getDashboardSummary(HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        ReportSummaryDto summary = reportService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.ok("Dashboard summary metrics", summary));
    }

    @GetMapping("/reports/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getBookingReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long resourceId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BookingStatus status,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        List<BookingResponseDto> reports = reportService.getFilteredReport(fromDate, toDate, resourceId, categoryId, status);
        return ResponseEntity.ok(ApiResponse.ok("Booking reports retrieved", reports));
    }

    @GetMapping("/reports/resource-usage")
    public ResponseEntity<ApiResponse<List<ReportSummaryDto.ResourceUsageItem>>> getResourceUsageReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        List<ReportSummaryDto.ResourceUsageItem> usage = reportService.getResourceUsageReport(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.ok("Resource usage metrics", usage));
    }
}
