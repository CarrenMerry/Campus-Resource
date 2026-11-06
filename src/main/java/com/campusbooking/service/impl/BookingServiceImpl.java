package com.campusbooking.service.impl;

import com.campusbooking.dto.BookingRequest;
import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.entity.Booking;
import com.campusbooking.entity.Resource;
import com.campusbooking.entity.User;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.*;
import com.campusbooking.repository.BookingRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.BookingService;
import com.campusbooking.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation managing bookings and preventing double-booking conflicts.
 * 
 * Concepts Demonstrated:
 * - Double-Booking Prevention: Strict overlap validation in Java backend.
 * - Polymorphism & Method Overriding: Delegates duration and approval rules to User subclasses.
 * - Method Overloading: Overloaded getBookings() methods.
 * - Collections: List, Set, HashSet, ArrayList.
 * - Streams & Lambdas: Mapping and filtering booking models.
 * - Exception Handling: Throws BookingConflictException, ResourceUnavailableException, etc.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final com.campusbooking.service.NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ResourceRepository resourceRepository,
                              com.campusbooking.service.NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // METHOD OVERLOADING DEMONSTRATIONS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings() {
        return bookingRepository.findAllByOrderByBookingDateDescStartTimeDesc().stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(BookingStatus status) {
        return bookingRepository.findByStatusOrderByBookingDateAscStartTimeAsc(status).stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(Long userId) {
        return bookingRepository.findByUser_UserIdOrderByBookingDateDescStartTimeDesc(userId).stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(Long userId, BookingStatus status) {
        // Concept: Streams & Lambda filter on user bookings
        return bookingRepository.findByUser_UserIdOrderByBookingDateDescStartTimeDesc(userId).stream()
                .filter(b -> status == null || b.getStatus() == status)
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(LocalDate fromDate, LocalDate toDate) {
        return bookingRepository.findFilteredBookings(null, null, fromDate, toDate).stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsForResourceAndDate(Long resourceId, LocalDate date) {
        return bookingRepository.findByResource_ResourceIdAndBookingDateOrderByStartTimeAsc(resourceId, date).stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        return BookingResponseDto.fromEntity(booking);
    }

    // ==========================================
    // CRITICAL CORE: RESERVATION CREATION & CONFLICT DETECTION
    // ==========================================

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingRequest request) {
        // 1. Fetch and validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException("User not found with id: " + userId));

        if (!user.isActive()) {
            throw new InvalidUserException("Your account is deactivated. Booking is not permitted.");
        }

        // Restrict reservations strictly to Faculty and Administrators (Students have view-only access)
        if (!user.canCreateBookings()) {
            throw new UnauthorizedOperationException("Facility reservations are restricted to Faculty and Administrators. Students have view-only access.");
        }

        // 2. Polymorphic validation: User subclass policies (Max hours, active booking quotas)
        ValidationUtil.validateBookingSchedule(request, user);

        long currentActive = bookingRepository.countActiveBookingsByUser(user.getUserId());
        if (currentActive >= user.getMaxActiveBookings()) {
            throw new BookingConflictException(String.format(
                    "You have reached your tier quota of %d active bookings. Please wait for previous bookings to complete or cancel.",
                    user.getMaxActiveBookings()
            ));
        }

        // 3. Fetch and validate Resource
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + request.getResourceId()));

        if (resource.getStatus() != ResourceStatus.AVAILABLE) {
            throw new ResourceUnavailableException("Resource '" + resource.getResourceName() + "' is currently " +
                    resource.getStatus().getDescription() + " and cannot be booked.");
        }

        // 4. Validate Capacity
        if (request.getAttendeesCount() != null && request.getAttendeesCount() > resource.getCapacity()) {
            throw new IllegalArgumentException(String.format(
                    "Requested attendee count (%d) exceeds resource capacity of %d.",
                    request.getAttendeesCount(), resource.getCapacity()
            ));
        }

        // 5. DOUBLE-BOOKING DETECTION (Java Backend Validation)
        // Rule: existing.start_time < requested.end_time AND existing.end_time > requested.start_time
        // Status must be in PENDING or APPROVED
        Set<BookingStatus> activeStatuses = new HashSet<>(Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED));
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                resource.getResourceId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime(),
                activeStatuses,
                null
        );

        if (!conflicts.isEmpty()) {
            Booking clash = conflicts.get(0);
            throw new BookingConflictException(String.format(
                    "This resource is already booked during the selected time (%s to %s) on %s.",
                    clash.getStartTime(), clash.getEndTime(), clash.getBookingDate()
            ));
        }

        // 6. Build Booking Entity
        Booking booking = new Booking(
                user,
                resource,
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getPurpose().trim(),
                request.getAttendeesCount()
        );

        // Polymorphic check: Does this user tier require admin approval?
        // Students require approval -> PENDING
        // Faculty / Admin -> APPROVED (or PENDING if institution default, here Faculty/Admin get fast-tracked)
        if (user.requiresAdminApproval()) {
            booking.setStatus(BookingStatus.PENDING);
        } else {
            booking.setStatus(BookingStatus.APPROVED);
            booking.setAdminRemark("Auto-approved based on " + user.getRole().getDisplayName() + " credentials.");
        }

        Booking savedBooking = bookingRepository.save(booking);

        if (user.requiresAdminApproval()) {
            notificationService.sendNotification(user, "Booking Submitted",
                    "Your reservation request for " + resource.getResourceName() + " on " + savedBooking.getBookingDate() + " has been submitted for administrative review.",
                    "INFO", savedBooking.getBookingId());
            notificationService.sendNotificationToAdmins("New Booking Request",
                    user.getName() + " submitted a reservation for " + resource.getResourceName() + " on " + savedBooking.getBookingDate() + ".",
                    "WARNING", savedBooking.getBookingId());
        } else {
            notificationService.sendNotification(user, "Booking Confirmed",
                    "Your reservation for " + resource.getResourceName() + " is confirmed! Your Digital Entry QR Pass is ready.",
                    "SUCCESS", savedBooking.getBookingId());
        }

        return BookingResponseDto.fromEntity(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId, Long userId, UserRole userRole) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        // Security check: Only booking owner or an administrator can cancel
        if (userRole != UserRole.ADMIN && !booking.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedOperationException("You do not have permission to cancel another user's booking.");
        }

        if (!booking.getStatus().isCancellable()) {
            throw new IllegalArgumentException("Booking cannot be cancelled because its current status is " +
                    booking.getStatus().name());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setAdminRemark(booking.getAdminRemark() != null ?
                booking.getAdminRemark() + " (Cancelled by " + userRole.getDisplayName() + ")" :
                "Cancelled by " + userRole.getDisplayName());

        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(saved.getUser(), "Booking Cancelled",
                "Your reservation for " + saved.getResource().getResourceName() + " on " + saved.getBookingDate() + " was cancelled.",
                "WARNING", saved.getBookingId());

        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, String adminRemark) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be approved.");
        }

        booking.setStatus(BookingStatus.APPROVED);
        if (adminRemark != null && !adminRemark.trim().isEmpty()) {
            booking.setAdminRemark(adminRemark.trim());
        }

        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(saved.getUser(), "Booking Approved!",
                "Your reservation for " + saved.getResource().getResourceName() + " on " + saved.getBookingDate() + " (" + saved.getStartTime() + " - " + saved.getEndTime() + ") has been approved. Digital Entry Pass is ready.",
                "SUCCESS", saved.getBookingId());

        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto rejectBooking(Long bookingId, String adminRemark) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Only PENDING or APPROVED bookings can be rejected.");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setAdminRemark((adminRemark != null && !adminRemark.trim().isEmpty()) ?
                adminRemark.trim() : "Rejected by Administrator");

        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(saved.getUser(), "Booking Rejected",
                "Your booking request for " + saved.getResource().getResourceName() + " was rejected. Remark: " + saved.getAdminRemark(),
                "DANGER", saved.getBookingId());

        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);
        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto checkInBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED reservations can be checked in. Current status: " + booking.getStatus());
        }

        if (booking.isCheckedIn()) {
            throw new IllegalArgumentException("Reservation is already checked in at " + booking.getCheckInTime());
        }

        booking.setCheckedIn(true);
        booking.setCheckInTime(java.time.LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(saved.getUser(), "Check-In Verified",
                "Attendance verified for " + saved.getResource().getResourceName() + " on " + saved.getBookingDate() + ".",
                "SUCCESS", saved.getBookingId());

        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto verifyAndCheckIn(String checkinCode) {
        if (checkinCode == null || checkinCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Check-in pass code cannot be blank.");
        }

        Booking booking = bookingRepository.findByCheckinCode(checkinCode.trim().toUpperCase())
                .orElseThrow(() -> new BookingNotFoundException("Invalid or unrecognized check-in pass code: " + checkinCode));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED reservations can be checked in. Current status is " + booking.getStatus());
        }

        if (booking.isCheckedIn()) {
            throw new IllegalArgumentException("Pass code " + checkinCode + " was already checked in at " + booking.getCheckInTime());
        }

        booking.setCheckedIn(true);
        booking.setCheckInTime(java.time.LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(saved.getUser(), "Check-In Verified",
                "Attendance verified for " + saved.getResource().getResourceName() + " on " + saved.getBookingDate() + ".",
                "SUCCESS", saved.getBookingId());

        return BookingResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlotAvailable(Long resourceId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (resourceId == null || date == null || startTime == null || endTime == null) {
            return false;
        }
        if (!startTime.isBefore(endTime)) {
            return false;
        }

        Optional<Resource> resOpt = resourceRepository.findById(resourceId);
        if (resOpt.isEmpty() || resOpt.get().getStatus() != ResourceStatus.AVAILABLE) {
            return false;
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                resourceId, date, startTime, endTime,
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED),
                null
        );
        return conflicts.isEmpty();
    }
}
