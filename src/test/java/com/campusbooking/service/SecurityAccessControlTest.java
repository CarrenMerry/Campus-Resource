package com.campusbooking.service;

import com.campusbooking.dto.BookingRequest;
import com.campusbooking.entity.*;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.InvalidUserException;
import com.campusbooking.exception.UnauthorizedOperationException;
import com.campusbooking.repository.BookingRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.impl.BookingServiceImpl;
import com.campusbooking.util.JwtUtil;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityAccessControlTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TokenRevocationService tokenRevocationService;

    @Mock
    private HttpServletRequest request;

    @Spy
    private JwtUtil jwtUtil = new JwtUtil("campusResourceBookingSystemSecureSecretKey2026MCAProjectAcademicKey1234567890");

    private SecurityUtil securityUtil;
    private BookingServiceImpl bookingService;

    private Student studentUser;
    private Faculty facultyUser;
    private Admin adminUser;
    private Resource testResource;
    private Booking facultyBooking;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil(jwtUtil, userRepository, tokenRevocationService);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, resourceRepository, notificationService);

        studentUser = new Student("Alice Student", "alice@campus.edu", "pass123", "1234567890", "MCA", "MCA001", 3);
        studentUser.setUserId(101L);

        facultyUser = new Faculty("Dr. Bob", "bob@campus.edu", "pass123", "1234567891", "CS", "FAC001", "Professor");
        facultyUser.setUserId(202L);

        adminUser = new Admin("Admin Carol", "carol@campus.edu", "pass123", "1234567892", "IT", "ADM001", true);
        adminUser.setUserId(303L);

        ResourceCategory category = new ResourceCategory("Classroom", "Lecture Room", "bi-building");
        testResource = new Resource("Hall 101", category, "Main Block", 60, "AC", "Projector", null);
        testResource.setResourceId(50L);
        testResource.setStatus(ResourceStatus.AVAILABLE);

        facultyBooking = new Booking(facultyUser, testResource, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), "Lecture", 40);
        facultyBooking.setBookingId(888L);
        facultyBooking.setStatus(BookingStatus.APPROVED);
    }

    // =========================================================================
    // ENDPOINT AUTHENTICATION (TOKEN VALIDATION)
    // =========================================================================

    @Test
    @DisplayName("Access Control: Missing Authorization header throws 401 Unauthorized")
    void testMissingAuthorizationHeaderThrows() {
        when(request.getHeader("Authorization")).thenReturn(null);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                securityUtil.getAuthenticatedUser(request)
        );

        assertTrue(ex.getMessage().contains("missing or malformed"));
    }

    @Test
    @DisplayName("Access Control: Malformed token throws 401 Unauthorized")
    void testMalformedTokenThrows() {
        when(request.getHeader("Authorization")).thenReturn("Bearer NotAValidTokenAtAll");

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                securityUtil.getAuthenticatedUser(request)
        );

        assertTrue(ex.getMessage().contains("Session expired or invalid"));
    }

    @Test
    @DisplayName("Access Control: Revoked token after logout throws 401 Unauthorized")
    void testRevokedTokenThrows() {
        String token = jwtUtil.generateToken(studentUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                securityUtil.getAuthenticatedUser(request)
        );

        assertTrue(ex.getMessage().contains("Session has been logged out"));
    }

    @Test
    @DisplayName("Access Control: Deactivated user account is immediately blocked")
    void testDeactivatedUserBlocked() {
        studentUser.setActive(false);
        String token = jwtUtil.generateToken(studentUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(false);
        when(userRepository.findByEmail(studentUser.getEmail())).thenReturn(Optional.of(studentUser));

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                securityUtil.getAuthenticatedUser(request)
        );

        assertTrue(ex.getMessage().contains("deactivated"));
    }

    // =========================================================================
    // ROLE-BASED ACCESS CONTROL (STUDENT vs FACULTY vs ADMIN)
    // =========================================================================

    @Test
    @DisplayName("Role RBAC: Student attempting admin operation is rejected with 403 Forbidden")
    void testStudentCannotPerformAdminAction() {
        String token = jwtUtil.generateToken(studentUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(false);
        when(userRepository.findByEmail(studentUser.getEmail())).thenReturn(Optional.of(studentUser));

        UnauthorizedOperationException ex = assertThrows(UnauthorizedOperationException.class, () ->
                securityUtil.requireAdmin(request)
        );

        assertTrue(ex.getMessage().contains("Administrator privileges required"));
    }

    @Test
    @DisplayName("Role RBAC: Faculty attempting admin operation is rejected with 403 Forbidden")
    void testFacultyCannotPerformAdminAction() {
        String token = jwtUtil.generateToken(facultyUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(false);
        when(userRepository.findByEmail(facultyUser.getEmail())).thenReturn(Optional.of(facultyUser));

        UnauthorizedOperationException ex = assertThrows(UnauthorizedOperationException.class, () ->
                securityUtil.requireAdmin(request)
        );

        assertTrue(ex.getMessage().contains("Administrator privileges required"));
    }

    @Test
    @DisplayName("Role RBAC: Administrator successfully passes admin-check")
    void testAdminPassesAdminAction() {
        String token = jwtUtil.generateToken(adminUser);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(false);
        when(userRepository.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));

        assertDoesNotThrow(() -> securityUtil.requireAdmin(request));
    }

    // =========================================================================
    // BOOKING POLICY & DATA ISOLATION
    // =========================================================================

    @Test
    @DisplayName("Policy RBAC: Student cannot create bookings (403 Forbidden)")
    void testStudentCannotCreateBookings() {
        when(userRepository.findById(studentUser.getUserId())).thenReturn(Optional.of(studentUser));

        BookingRequest bookingReq = new BookingRequest();
        bookingReq.setResourceId(50L);
        bookingReq.setBookingDate(LocalDate.now().plusDays(1));
        bookingReq.setStartTime(LocalTime.of(10, 0));
        bookingReq.setEndTime(LocalTime.of(12, 0));
        bookingReq.setPurpose("Student Project Meeting");

        UnauthorizedOperationException ex = assertThrows(UnauthorizedOperationException.class, () ->
                bookingService.createBooking(studentUser.getUserId(), bookingReq)
        );

        assertTrue(ex.getMessage().contains("Facility reservations are restricted to Faculty and Administrators"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Data Isolation: User cannot cancel another user's booking (403 Forbidden)")
    void testUserCannotCancelOtherUserBooking() {
        when(bookingRepository.findById(888L)).thenReturn(Optional.of(facultyBooking));

        // Attempting to cancel facultyBooking using studentUser's ID
        UnauthorizedOperationException ex = assertThrows(UnauthorizedOperationException.class, () ->
                bookingService.cancelBooking(888L, studentUser.getUserId(), UserRole.STUDENT)
        );

        assertTrue(ex.getMessage().contains("do not have permission to cancel another user's booking"));
    }

    @Test
    @DisplayName("Data Isolation: Admin can cancel any booking for management oversight")
    void testAdminCanCancelAnyBooking() {
        when(bookingRepository.findById(888L)).thenReturn(Optional.of(facultyBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() ->
                bookingService.cancelBooking(888L, adminUser.getUserId(), UserRole.ADMIN)
        );

        assertEquals(BookingStatus.CANCELLED, facultyBooking.getStatus());
        verify(bookingRepository, times(1)).save(facultyBooking);
    }
}

