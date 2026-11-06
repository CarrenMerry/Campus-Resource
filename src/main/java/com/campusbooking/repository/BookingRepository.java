package com.campusbooking.repository;

import com.campusbooking.entity.Booking;
import com.campusbooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA Repository for Booking entity.
 * Implements high-performance queries including double-booking overlap detection.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_UserIdOrderByBookingDateDescStartTimeDesc(Long userId);

    List<Booking> findByResource_ResourceIdAndBookingDateOrderByStartTimeAsc(Long resourceId, LocalDate bookingDate);

    List<Booking> findByStatusOrderByBookingDateAscStartTimeAsc(BookingStatus status);

    List<Booking> findAllByOrderByBookingDateDescStartTimeDesc();

    java.util.Optional<Booking> findByCheckinCode(String checkinCode);

    /**
     * Critical Overlap Query:
     * Finds any conflicting booking for the same resource on the specified date where:
     * existing.startTime < requestedEndTime AND existing.endTime > requestedStartTime
     * and status is among slot-occupying statuses (PENDING, APPROVED).
     */
    @Query("SELECT b FROM Booking b WHERE b.resource.resourceId = :resourceId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.status IN :occupyingStatuses " +
           "AND b.startTime < :requestedEnd " +
           "AND b.endTime > :requestedStart " +
           "AND (:excludeBookingId IS NULL OR b.bookingId != :excludeBookingId)")
    List<Booking> findConflictingBookings(@Param("resourceId") Long resourceId,
                                         @Param("bookingDate") LocalDate bookingDate,
                                         @Param("requestedStart") LocalTime requestedStart,
                                         @Param("requestedEnd") LocalTime requestedEnd,
                                         @Param("occupyingStatuses") Collection<BookingStatus> occupyingStatuses,
                                         @Param("excludeBookingId") Long excludeBookingId);

    /**
     * Counts active bookings (PENDING or APPROVED) for a given user.
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.userId = :userId AND b.status IN ('PENDING', 'APPROVED')")
    long countActiveBookingsByUser(@Param("userId") Long userId);

    long countByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:resourceId IS NULL OR b.resource.resourceId = :resourceId) AND " +
           "(:fromDate IS NULL OR b.bookingDate >= :fromDate) AND " +
           "(:toDate IS NULL OR b.bookingDate <= :toDate) " +
           "ORDER BY b.bookingDate DESC, b.startTime DESC")
    List<Booking> findFilteredBookings(@Param("status") BookingStatus status,
                                       @Param("resourceId") Long resourceId,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate);
}
