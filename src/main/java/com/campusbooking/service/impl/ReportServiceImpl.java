package com.campusbooking.service.impl;

import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.dto.ReportSummaryDto;
import com.campusbooking.entity.Booking;
import com.campusbooking.entity.Resource;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.enums.UserRole;
import com.campusbooking.repository.BookingRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reporting Service implementation generating statistical and operational intelligence.
 * 
 * Concepts Demonstrated:
 * - Streams and Lambda Expressions: Extensively used to aggregate, group, transform, and sort.
 * - Collections Framework: Maps, Lists, HashMaps, HashSets used for statistical aggregations.
 * - Layered Architecture: Clean separation of reporting business logic.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(BookingRepository bookingRepository,
                             ResourceRepository resourceRepository,
                             UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryDto getDashboardSummary() {
        List<Booking> allBookings = bookingRepository.findAll();
        List<Resource> allResources = resourceRepository.findAll();

        ReportSummaryDto summary = new ReportSummaryDto();

        // Total counters
        summary.setTotalBookings(allBookings.size());
        summary.setTotalResources(allResources.size());
        summary.setTotalUsers(userRepository.count());

        // Status counts using Java Streams & Lambdas
        summary.setPendingBookings(allBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count());
        summary.setApprovedBookings(allBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).count());
        summary.setRejectedBookings(allBookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED).count());
        summary.setCancelledBookings(allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count());
        summary.setCompletedBookings(allBookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count());

        // Resource counts using Streams
        summary.setAvailableResources(allResources.stream().filter(r -> r.getStatus() == ResourceStatus.AVAILABLE).count());
        summary.setMaintenanceResources(allResources.stream().filter(r -> r.getStatus() == ResourceStatus.MAINTENANCE).count());

        // User role counts
        summary.setStudentCount(userRepository.countByRole(UserRole.STUDENT));
        summary.setFacultyCount(userRepository.countByRole(UserRole.FACULTY));
        summary.setAdminCount(userRepository.countByRole(UserRole.ADMIN));

        // Concept: Java Streams collectors groupingBy to build status distribution Map<String, Long>
        Map<String, Long> statusDist = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getStatus().name(), Collectors.counting()));
        summary.setStatusDistribution(statusDist);

        // Concept: Java Streams groupingBy to aggregate bookings by Category name
        Map<String, Long> catDist = allBookings.stream()
                .filter(b -> b.getResource() != null && b.getResource().getCategory() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getResource().getCategory().getCategoryName(),
                        Collectors.counting()
                ));
        summary.setBookingsByCategory(catDist);

        // Top booked resources using Streams grouping and sorting
        List<ReportSummaryDto.ResourceUsageItem> usageItems = getResourceUsageReport(null, null);
        summary.setTopBookedResources(usageItems.stream().limit(5).collect(Collectors.toList()));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getFilteredReport(LocalDate fromDate, LocalDate toDate,
                                                      Long resourceId, Long categoryId,
                                                      BookingStatus status) {
        List<Booking> bookings = bookingRepository.findAll();

        // Concept: Multi-criteria stream filtering with method references and lambdas
        return bookings.stream()
                .filter(b -> fromDate == null || !b.getBookingDate().isBefore(fromDate))
                .filter(b -> toDate == null || !b.getBookingDate().isAfter(toDate))
                .filter(b -> resourceId == null || (b.getResource() != null && b.getResource().getResourceId().equals(resourceId)))
                .filter(b -> categoryId == null || (b.getResource() != null && b.getResource().getCategory() != null &&
                        b.getResource().getCategory().getCategoryId().equals(categoryId)))
                .filter(b -> status == null || b.getStatus() == status)
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed()
                        .thenComparing(Booking::getStartTime).reversed())
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportSummaryDto.ResourceUsageItem> getResourceUsageReport(LocalDate fromDate, LocalDate toDate) {
        List<Booking> bookings = bookingRepository.findAll();

        // Filter bookings by date window if provided
        List<Booking> filteredBookings = bookings.stream()
                .filter(b -> fromDate == null || !b.getBookingDate().isBefore(fromDate))
                .filter(b -> toDate == null || !b.getBookingDate().isAfter(toDate))
                .collect(Collectors.toList());

        // Group bookings by Resource ID using Map
        Map<Long, List<Booking>> bookingsByResource = filteredBookings.stream()
                .filter(b -> b.getResource() != null)
                .collect(Collectors.groupingBy(b -> b.getResource().getResourceId()));

        List<Resource> allResources = resourceRepository.findAll();

        // Concept: Transform resource list with Stream map, and sort by highest booking count
        return allResources.stream()
                .map(resource -> {
                    List<Booking> resBookings = bookingsByResource.getOrDefault(resource.getResourceId(), Collections.emptyList());
                    long count = resBookings.size();
                    double totalHours = resBookings.stream()
                            .mapToDouble(Booking::getDurationInHours)
                            .sum();

                    String catName = resource.getCategory() != null ? resource.getCategory().getCategoryName() : "General";
                    return new ReportSummaryDto.ResourceUsageItem(
                            resource.getResourceId(),
                            resource.getResourceName(),
                            catName,
                            count,
                            Math.round(totalHours * 10.0) / 10.0
                    );
                })
                .sorted(Comparator.comparingLong(ReportSummaryDto.ResourceUsageItem::getBookingCount).reversed())
                .collect(Collectors.toList());
    }
}
