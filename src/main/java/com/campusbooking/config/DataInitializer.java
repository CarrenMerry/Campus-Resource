package com.campusbooking.config;

import com.campusbooking.entity.*;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.repository.BookingRepository;
import com.campusbooking.repository.ResourceCategoryRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Initializes sample institutional seed data if database tables are empty.
 * Ensures the application is immediately interactive out-of-the-box.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceCategoryRepository categoryRepository;
    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;
    private final PasswordUtil passwordUtil;
    private final com.campusbooking.repository.NotificationRepository notificationRepository;

    public DataInitializer(UserRepository userRepository,
                           ResourceCategoryRepository categoryRepository,
                           ResourceRepository resourceRepository,
                           BookingRepository bookingRepository,
                           PasswordUtil passwordUtil,
                           com.campusbooking.repository.NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.resourceRepository = resourceRepository;
        this.bookingRepository = bookingRepository;
        this.passwordUtil = passwordUtil;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println(">>> [DataInitializer] Seeding initial campus resource booking data...");

        // 1. Seed Users
        Admin admin = new Admin(
                "System Administrator",
                "admin@campus.edu",
                passwordUtil.hashPassword("Admin@123"),
                "9876543210",
                "IT Operations",
                "ADM-001",
                true
        );
        userRepository.save(admin);

        Faculty faculty1 = new Faculty(
                "Dr. Robert Smith",
                "faculty@campus.edu",
                passwordUtil.hashPassword("Faculty@123"),
                "9876543211",
                "Computer Science",
                "FAC-CS-101",
                "Associate Professor"
        );
        userRepository.save(faculty1);

        Faculty faculty2 = new Faculty(
                "Dr. Eleanor Vance",
                "faculty2@campus.edu",
                passwordUtil.hashPassword("Faculty@123"),
                "9876543212",
                "Electronics & Communication",
                "FAC-EC-102",
                "Professor"
        );
        userRepository.save(faculty2);

        Student student1 = new Student(
                "Aarav Sharma",
                "student@campus.edu",
                passwordUtil.hashPassword("Student@123"),
                "9876543213",
                "Computer Applications (MCA)",
                "MCA202401",
                3
        );
        userRepository.save(student1);

        Student student2 = new Student(
                "Priya Patel",
                "student2@campus.edu",
                passwordUtil.hashPassword("Student@123"),
                "9876543214",
                "Information Technology",
                "IT202415",
                4
        );
        userRepository.save(student2);

        // 2. Seed Categories
        ResourceCategory labCat = categoryRepository.save(new ResourceCategory(
                "Computer Laboratories",
                "High-performance computer labs with workstations, network switches, and GPU clusters.",
                "bi-laptop"
        ));

        ResourceCategory seminarCat = categoryRepository.save(new ResourceCategory(
                "Seminar Halls",
                "Auditorium spaces with acoustic treatment, dual laser projection, and stage lighting.",
                "bi-building"
        ));

        ResourceCategory classCat = categoryRepository.save(new ResourceCategory(
                "Smart Classrooms",
                "Interactive digital board rooms for lectures, presentations, and tutorials.",
                "bi-easel2"
        ));

        ResourceCategory meetingCat = categoryRepository.save(new ResourceCategory(
                "Meeting Rooms",
                "Boardrooms equipped for committee meetings, departmental reviews, and interviews.",
                "bi-people"
        ));

        ResourceCategory sportsCat = categoryRepository.save(new ResourceCategory(
                "Sports Facilities",
                "Campus athletics facilities, indoor courts, and recreation centers.",
                "bi-trophy"
        ));

        // 3. Seed Resources
        Resource lab1 = resourceRepository.save(new Resource(
                "Computer Lab 1 (AI & ML Lab)",
                labCat,
                "Block B - 1st Floor, Room 104",
                45,
                "State-of-the-art laboratory equipped with 45 Dell Core-i7 workstations and Nvidia RTX GPUs.",
                "45 Workstations, 1 Gbps LAN, Dual 4K Projectors, UPS Backup, AC",
                "https://images.unsplash.com/photo-1562774053-701939374585?w=600&auto=format&fit=crop&q=80"
        ));

        Resource lab2 = resourceRepository.save(new Resource(
                "Computer Lab 2 (Software Engineering)",
                labCat,
                "Block B - 2nd Floor, Room 205",
                40,
                "General software engineering and coding lab with complete Linux / Windows dual boot.",
                "40 Workstations, Central Projector, Whiteboard, High-Speed WiFi",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80"
        ));

        Resource hall1 = resourceRepository.save(new Resource(
                "APJ Abdul Kalam Seminar Hall",
                seminarCat,
                "Main Academic Block - Ground Floor",
                250,
                "Tiered institutional seminar hall ideal for national conferences, workshops, and guest talks.",
                "250 Tiered Seats, Stage Podium, Dual 1080p Laser Projectors, Surround Sound, Video Recording",
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80"
        ));

        Resource hall2 = resourceRepository.save(new Resource(
                "Sir CV Raman Auditorium",
                seminarCat,
                "Science & Research Block - 3rd Floor",
                400,
                "Grand auditorium for university convocations, cultural symposiums, and major keynote events.",
                "400 Ergonomic Seats, Full Stage Truss Lighting, Wireless Microphones, Green Rooms, Central HVAC",
                "https://images.unsplash.com/photo-1511578314322-379afb476865?w=600&auto=format&fit=crop&q=80"
        ));

        Resource class1 = resourceRepository.save(new Resource(
                "Smart Classroom 101",
                classCat,
                "Academic Block A - 1st Floor, Room 101",
                60,
                "Equipped with 86-inch interactive touchscreen smartboard and hybrid learning video camera.",
                "Interactive Smartboard, Audio Amplification, Document Camera, Ergonomic Benches",
                "https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=600&auto=format&fit=crop&q=80"
        ));

        Resource class2 = resourceRepository.save(new Resource(
                "Smart Classroom 204",
                classCat,
                "Academic Block A - 2nd Floor, Room 204",
                70,
                "Spacious lecture room with flexible seating arrangements for group discussions.",
                "Smart Projector, Motorized Screen, PA System, 70 Capacity Seating",
                "https://images.unsplash.com/photo-1577495508048-b635879837f1?w=600&auto=format&fit=crop&q=80"
        ));

        Resource meeting1 = resourceRepository.save(new Resource(
                "Executive Board Room",
                meetingCat,
                "Administrative Block - 2nd Floor",
                20,
                "Executive boardroom for syndicate discussions, board meetings, and accreditation panels.",
                "Conference Table, 20 Leather Chairs, 75-inch Display, Polycom Video Conferencing, High WiFi",
                "https://images.unsplash.com/photo-1497366216548-37526070297c?w=600&auto=format&fit=crop&q=80"
        ));

        Resource sports1 = resourceRepository.save(new Resource(
                "Indoor Badminton Court 1",
                sportsCat,
                "Sports & Fitness Complex - Court 1",
                8,
                "Synthetic wooden surface tournament-standard indoor badminton court.",
                "Wooden Flooring, Anti-glare Floodlights, Electronic Scoreboard, Spectator Gallery",
                "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600&auto=format&fit=crop&q=80"
        ));

        // 4. Seed Sample Bookings
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Booking b1 = new Booking(
                faculty1,
                lab1,
                today,
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "MCA Cloud Computing Lab Practical Examination",
                42
        );
        b1.setStatus(BookingStatus.APPROVED);
        b1.setAdminRemark("Approved by HOD and Dean.");
        bookingRepository.save(b1);

        Booking b2 = new Booking(
                faculty1,
                hall1,
                tomorrow,
                LocalTime.of(14, 0),
                LocalTime.of(16, 30),
                "MCA Technical Coding Club Hackathon Orientation (Faculty Coordinator)",
                80
        );
        b2.setStatus(BookingStatus.PENDING);
        bookingRepository.save(b2);

        Booking b3 = new Booking(
                faculty2,
                meeting1,
                today,
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                "Departmental Curriculum Revision Committee Meeting",
                12
        );
        b3.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(b3);

        Booking b4 = new Booking(
                student2,
                sports1,
                tomorrow,
                LocalTime.of(17, 0),
                LocalTime.of(18, 0),
                "Inter-Department Badminton Doubles Practice",
                4
        );
        b4.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(b4);

        // 5. Seed Sample In-App Notifications
        notificationRepository.save(new com.campusbooking.entity.Notification(
                admin,
                "New Booking Request",
                "Dr. Robert Smith submitted a reservation request for APJ Abdul Kalam Seminar Hall.",
                "WARNING",
                b2.getBookingId()
        ));
        notificationRepository.save(new com.campusbooking.entity.Notification(
                admin,
                "System Initialized",
                "Campus Resource Booking System initialized successfully with 5 categories and 8 resources.",
                "INFO",
                null
        ));
        notificationRepository.save(new com.campusbooking.entity.Notification(
                faculty1,
                "Booking Confirmed & QR Pass Ready",
                "Your reservation for Computer Lab 1 (AI & ML Lab) has been approved. Your QR Entry Pass is ready.",
                "SUCCESS",
                b1.getBookingId()
        ));
        notificationRepository.save(new com.campusbooking.entity.Notification(
                student2,
                "Booking Approved",
                "Your reservation for Indoor Badminton Court 1 has been approved.",
                "SUCCESS",
                b4.getBookingId()
        ));

        System.out.println(">>> [DataInitializer] Seed data initialized successfully!");
    }
}
