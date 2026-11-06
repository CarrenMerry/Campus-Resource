package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.dto.ResourceRequest;
import com.campusbooking.dto.ResourceResponseDto;
import com.campusbooking.entity.ResourceCategory;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.service.BookingService;
import com.campusbooking.service.ResourceService;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for resource catalog and administration.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final BookingService bookingService;
    private final SecurityUtil securityUtil;

    public ResourceController(ResourceService resourceService,
                              BookingService bookingService,
                              SecurityUtil securityUtil) {
        this.resourceService = resourceService;
        this.bookingService = bookingService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceResponseDto>>> getResources(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) ResourceStatus status,
            @RequestParam(required = false) String search) {
        List<ResourceResponseDto> resources = resourceService.findResources(categoryId, minCapacity, status, search);
        return ResponseEntity.ok(ApiResponse.ok("Resources retrieved", resources));
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<Map<String, List<ResourceResponseDto>>>> getResourcesGrouped() {
        Map<String, List<ResourceResponseDto>> grouped = resourceService.getResourcesGroupedByCategory();
        return ResponseEntity.ok(ApiResponse.ok("Resources grouped by category", grouped));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ResourceCategory>>> getCategories() {
        List<ResourceCategory> categories = resourceService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved", categories));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ResourceCategory>> createCategory(
            @Valid @RequestBody ResourceCategory category,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        ResourceCategory created = resourceService.createCategory(category);
        return new ResponseEntity<>(ApiResponse.ok("Category created", created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceResponseDto>> getResourceById(@PathVariable("id") Long id) {
        ResourceResponseDto resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.ok("Resource details retrieved", resource));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAvailability(
            @PathVariable("id") Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {

        boolean isAvailable = bookingService.isSlotAvailable(id, date, startTime, endTime);
        Map<String, Object> response = Map.of(
                "resourceId", id,
                "date", date,
                "startTime", startTime,
                "endTime", endTime,
                "available", isAvailable
        );
        return ResponseEntity.ok(ApiResponse.ok(isAvailable ? "Slot is available" : "Slot is booked or unavailable", response));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getScheduleForDate(
            @PathVariable("id") Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingResponseDto> schedule = bookingService.getBookingsForResourceAndDate(id, date);
        return ResponseEntity.ok(ApiResponse.ok("Resource schedule for " + date, schedule));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResourceResponseDto>> createResource(
            @Valid @RequestBody ResourceRequest resourceRequest,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        ResourceResponseDto created = resourceService.createResource(resourceRequest);
        return new ResponseEntity<>(ApiResponse.ok("Resource created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceResponseDto>> updateResource(
            @PathVariable("id") Long id,
            @Valid @RequestBody ResourceRequest resourceRequest,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        ResourceResponseDto updated = resourceService.updateResource(id, resourceRequest);
        return ResponseEntity.ok(ApiResponse.ok("Resource updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ResourceResponseDto>> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        ResourceStatus status = ResourceStatus.valueOf(body.get("status").toUpperCase());
        ResourceResponseDto updated = resourceService.updateResourceStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Resource status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        securityUtil.requireAdmin(request);
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.ok("Resource deactivated successfully"));
    }
}
