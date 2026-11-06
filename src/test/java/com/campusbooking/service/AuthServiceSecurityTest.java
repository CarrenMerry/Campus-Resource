package com.campusbooking.service;

import com.campusbooking.dto.AuthResponse;
import com.campusbooking.dto.LoginRequest;
import com.campusbooking.dto.RegisterRequest;
import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.entity.Faculty;
import com.campusbooking.entity.Student;
import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.InvalidUserException;
import com.campusbooking.exception.UnauthorizedOperationException;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.impl.AuthServiceImpl;
import com.campusbooking.util.JwtUtil;
import com.campusbooking.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceSecurityTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordUtil passwordUtil = new PasswordUtil();

    @Spy
    private JwtUtil jwtUtil = new JwtUtil("campusResourceBookingSystemSecureSecretKey2026MCAProjectAcademicKey1234567890");

    @Mock
    private TokenRevocationService tokenRevocationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Student mockStudent;
    private Faculty mockFaculty;

    @BeforeEach
    void setUp() {
        String hashedPwd = passwordUtil.hashPassword("Password@123");

        mockStudent = new Student("Alice Student", "alice@campus.edu", hashedPwd, "9876543210",
                "Computer Science", "MCA202401", 3);
        mockStudent.setUserId(1L);

        mockFaculty = new Faculty("Dr. Bob", "bob@campus.edu", hashedPwd, "9876543211",
                "Computer Science", "FAC-CS-01", "Associate Professor");
        mockFaculty.setUserId(2L);
    }

    // =========================================================================
    // REGISTRATION TESTS
    // =========================================================================

    @Test
    @DisplayName("Registration: Register valid Student successfully with BCrypt hash")
    void testRegisterValidStudent() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice Student");
        request.setEmail("alice@campus.edu");
        request.setPassword("Password@123");
        request.setPhone("9876543210");
        request.setRole(UserRole.STUDENT);
        request.setDepartment("Computer Science");
        request.setStudentRollNumber("MCA202401");
        request.setSemester(3);

        when(userRepository.existsByEmail("alice@campus.edu")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(10L);
            return u;
        });

        UserResponseDto dto = authService.register(request);

        assertNotNull(dto);
        assertEquals("Alice Student", dto.getName());
        assertEquals("alice@campus.edu", dto.getEmail());
        assertEquals(UserRole.STUDENT, dto.getRole());
        assertEquals("MCA202401", dto.getStudentRollNumber());
        assertEquals(3, dto.getSemester());

        // Verify password hashing was applied (not plaintext)
        verify(userRepository, times(1)).save(argThat(user ->
                passwordUtil.verifyPassword("Password@123", user.getPassword()) &&
                        !user.getPassword().equals("Password@123")
        ));
    }

    @Test
    @DisplayName("Registration: Register valid Faculty member successfully")
    void testRegisterValidFaculty() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Dr. Bob");
        request.setEmail("bob@campus.edu");
        request.setPassword("FacultyPass@123");
        request.setPhone("9876543211");
        request.setRole(UserRole.FACULTY);
        request.setDepartment("Computer Science");
        request.setEmployeeId("FAC-CS-01");
        request.setDesignation("Associate Professor");

        when(userRepository.existsByEmail("bob@campus.edu")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(20L);
            return u;
        });

        UserResponseDto dto = authService.register(request);

        assertNotNull(dto);
        assertEquals("Dr. Bob", dto.getName());
        assertEquals(UserRole.FACULTY, dto.getRole());
        assertEquals("FAC-CS-01", dto.getEmployeeId());
        assertEquals("Associate Professor", dto.getDesignation());
    }

    @Test
    @DisplayName("Registration: Reject duplicate email addresses")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Duplicate User");
        request.setEmail("alice@campus.edu");
        request.setPassword("Password@123");
        request.setRole(UserRole.STUDENT);
        request.setDepartment("MCA");
        request.setStudentRollNumber("MCA202499");
        request.setSemester(1);

        when(userRepository.existsByEmail("alice@campus.edu")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.register(request)
        );

        assertTrue(ex.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registration: Reject public administrator self-registration with 403 Forbidden")
    void testRegisterAdminForbidden() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Hacker Admin");
        request.setEmail("hacker@campus.edu");
        request.setPassword("HackedPassword123");
        request.setRole(UserRole.ADMIN);
        request.setDepartment("IT");

        when(userRepository.existsByEmail("hacker@campus.edu")).thenReturn(false);

        UnauthorizedOperationException ex = assertThrows(UnauthorizedOperationException.class, () ->
                authService.register(request)
        );

        assertTrue(ex.getMessage().contains("Administrator accounts cannot be created via public registration"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registration: Reject student without roll number")
    void testRegisterStudentMissingRollNumber() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Student No Roll");
        request.setEmail("noroll@campus.edu");
        request.setPassword("Password@123");
        request.setRole(UserRole.STUDENT);
        request.setDepartment("MCA");
        request.setStudentRollNumber(""); // Empty
        request.setSemester(2);

        when(userRepository.existsByEmail("noroll@campus.edu")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.register(request)
        );

        assertTrue(ex.getMessage().contains("roll number is required"));
    }

    @Test
    @DisplayName("Registration: Reject faculty without employee ID")
    void testRegisterFacultyMissingEmployeeId() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Faculty No ID");
        request.setEmail("noid@campus.edu");
        request.setPassword("Password@123");
        request.setRole(UserRole.FACULTY);
        request.setDepartment("Engineering");
        request.setEmployeeId(null);
        request.setDesignation("Assistant Professor");

        when(userRepository.existsByEmail("noid@campus.edu")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.register(request)
        );

        assertTrue(ex.getMessage().contains("Employee ID is required"));
    }

    @Test
    @DisplayName("Registration: Reject invalid password length")
    void testRegisterShortPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Short Pass User");
        request.setEmail("short@campus.edu");
        request.setPassword("123"); // Too short
        request.setRole(UserRole.STUDENT);
        request.setDepartment("MCA");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.register(request)
        );

        assertTrue(ex.getMessage().contains("at least 6 characters"));
    }

    // =========================================================================
    // LOGIN TESTS
    // =========================================================================

    @Test
    @DisplayName("Login: Valid credentials return JWT token and user profile")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("alice@campus.edu", "Password@123");
        when(userRepository.findByEmail("alice@campus.edu")).thenReturn(Optional.of(mockStudent));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getToken().length() > 20);
        assertEquals("Alice Student", response.getUser().getName());
        assertEquals("alice@campus.edu", response.getUser().getEmail());
        assertEquals(UserRole.STUDENT, response.getUser().getRole());

        // Validate token claims
        assertTrue(jwtUtil.isTokenValid(response.getToken()));
        assertEquals("alice@campus.edu", jwtUtil.extractEmail(response.getToken()));
        assertEquals(UserRole.STUDENT, jwtUtil.extractRole(response.getToken()));
    }

    @Test
    @DisplayName("Login: Reject incorrect password with 401 Unauthorized")
    void testLoginWrongPassword() {
        LoginRequest request = new LoginRequest("alice@campus.edu", "WrongPassword!999");
        when(userRepository.findByEmail("alice@campus.edu")).thenReturn(Optional.of(mockStudent));

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                authService.login(request)
        );

        assertEquals("Invalid email or password.", ex.getMessage());
    }

    @Test
    @DisplayName("Login: Reject nonexistent account with 401 Unauthorized")
    void testLoginNonexistentUser() {
        LoginRequest request = new LoginRequest("ghost@campus.edu", "AnyPass@123");
        when(userRepository.findByEmail("ghost@campus.edu")).thenReturn(Optional.empty());

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                authService.login(request)
        );

        assertEquals("Invalid email or password.", ex.getMessage());
    }

    @Test
    @DisplayName("Login: Reject deactivated user account")
    void testLoginDeactivatedAccount() {
        mockStudent.setActive(false);
        LoginRequest request = new LoginRequest("alice@campus.edu", "Password@123");
        when(userRepository.findByEmail("alice@campus.edu")).thenReturn(Optional.of(mockStudent));

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                authService.login(request)
        );

        assertTrue(ex.getMessage().contains("deactivated"));
    }

    // =========================================================================
    // LOGOUT & SESSION TESTS
    // =========================================================================

    @Test
    @DisplayName("Logout: Revokes token and rejects subsequent session retrieval")
    void testLogoutRevokesToken() {
        String token = jwtUtil.generateToken(mockStudent);

        authService.logout(token);
        verify(tokenRevocationService, times(1)).revokeToken(token);

        // When token is revoked, getCurrentUser must reject it
        when(tokenRevocationService.isTokenRevoked(token)).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                authService.getCurrentUser(token)
        );

        assertTrue(ex.getMessage().contains("logged out"));
    }

    @Test
    @DisplayName("Session: Expired or malformed token is rejected")
    void testInvalidTokenRejected() {
        assertThrows(InvalidUserException.class, () ->
                authService.getCurrentUser("malformed.jwt.token")
        );
    }
}

