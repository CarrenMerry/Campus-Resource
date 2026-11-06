package com.campusbooking.service;

import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.entity.*;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.repository.BookingRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceCheckInTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Faculty testFaculty;
    private Resource testResource;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testFaculty = new Faculty("Dr. Smith", "smith@campus.edu", "pass", "9999999999", "CS", "EMP-01", "Professor");
        testFaculty.setUserId(1L);

        ResourceCategory cat = new ResourceCategory("Labs", "High tech", "bi-laptop");
        testResource = new Resource("AI Lab", cat, "Block A", 50, "Full specs", "AC", null);
        testResource.setResourceId(10L);
        testResource.setStatus(ResourceStatus.AVAILABLE);

        testBooking = new Booking(testFaculty, testResource, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), "Lab Session", 30);
        testBooking.setBookingId(100L);
        testBooking.setStatus(BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Check-In: Approved booking is successfully checked in")
    void testCheckInSuccess() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDto response = bookingService.checkInBooking(100L);

        assertNotNull(response);
        assertTrue(response.isCheckedIn());
        assertNotNull(response.getCheckInTime());
        verify(bookingRepository, times(1)).save(testBooking);
        verify(notificationService, times(1)).sendNotification(eq(testFaculty), anyString(), anyString(), eq("SUCCESS"), eq(100L));
    }

    @Test
    @DisplayName("Check-In: Pending booking cannot be checked in")
    void testCheckInPendingBookingThrows() {
        testBooking.setStatus(BookingStatus.PENDING);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.checkInBooking(100L);
        });
        assertTrue(ex.getMessage().contains("Only APPROVED reservations can be checked in"));
    }

    @Test
    @DisplayName("Check-In: Duplicate check-in is rejected")
    void testDuplicateCheckInThrows() {
        testBooking.setCheckedIn(true);
        testBooking.setCheckInTime(java.time.LocalDateTime.now());
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.checkInBooking(100L);
        });
        assertTrue(ex.getMessage().contains("already checked in"));
    }

    @Test
    @DisplayName("Verify Pass Code: Successfully checks in via pass code")
    void testVerifyAndCheckInByCode() {
        String code = testBooking.getCheckinCode();
        when(bookingRepository.findByCheckinCode(code)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDto response = bookingService.verifyAndCheckIn(code);

        assertNotNull(response);
        assertTrue(response.isCheckedIn());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    @DisplayName("Verify Pass Code: Invalid pass code throws BookingNotFoundException")
    void testVerifyAndCheckInInvalidCode() {
        when(bookingRepository.findByCheckinCode("CRB-INVALID")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            bookingService.verifyAndCheckIn("CRB-INVALID");
        });
    }
}
