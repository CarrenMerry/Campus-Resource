-- ==============================================================================
-- CAMPUS RESOURCE BOOKING SYSTEM
-- MCA Academic Project - Complete MySQL Database Script
-- ==============================================================================

-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS `campus_resource_booking` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `campus_resource_booking`;

-- Drop existing tables in reverse order of foreign keys if re-running
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `resources`;
DROP TABLE IF EXISTS `resource_categories`;
DROP TABLE IF EXISTS `users`;

-- ==============================================================================
-- 2. CREATE TABLE: users
-- Hierarchy: Student, Faculty, Admin stored via single-table inheritance
-- ==============================================================================
CREATE TABLE `users` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `phone` VARCHAR(20),
    `department` VARCHAR(100),
    `student_roll_number` VARCHAR(50),
    `semester` INT,
    `employee_id` VARCHAR(50),
    `designation` VARCHAR(100),
    `admin_code` VARCHAR(50),
    `is_super_admin` BOOLEAN DEFAULT TRUE,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_email` (`email`),
    INDEX `idx_user_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 3. CREATE TABLE: resource_categories
-- ==============================================================================
CREATE TABLE `resource_categories` (
    `category_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(500),
    `icon_class` VARCHAR(50) DEFAULT 'bi-building'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 4. CREATE TABLE: resources
-- ==============================================================================
CREATE TABLE `resources` (
    `resource_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_id` BIGINT NOT NULL,
    `resource_name` VARCHAR(150) NOT NULL,
    
    `location` VARCHAR(200) NOT NULL,
    `capacity` INT NOT NULL,
    `description` TEXT,
    `facilities` VARCHAR(500),
    `status` VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    `image_url` VARCHAR(255),
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_resource_category` FOREIGN KEY (`category_id`) 
        REFERENCES `resource_categories` (`category_id`) ON DELETE RESTRICT,
    INDEX `idx_resource_status` (`status`),
    INDEX `idx_resource_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 5. CREATE TABLE: bookings
-- ==============================================================================
CREATE TABLE `bookings` (
    `booking_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `resource_id` BIGINT NOT NULL,
    `booking_date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `purpose` VARCHAR(500) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `admin_remark` VARCHAR(500),
    `attendees_count` INT DEFAULT 1,
    `checkin_code` VARCHAR(64) UNIQUE,
    `is_checked_in` BOOLEAN NOT NULL DEFAULT FALSE,
    `check_in_time` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_booking_resource` FOREIGN KEY (`resource_id`) 
        REFERENCES `resources` (`resource_id`) ON DELETE RESTRICT,
    INDEX `idx_booking_res_date` (`resource_id`, `booking_date`),
    INDEX `idx_booking_user` (`user_id`),
    INDEX `idx_booking_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 6. CREATE TABLE: notifications
-- ==============================================================================
CREATE TABLE `notifications` (
    `notification_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` VARCHAR(500) NOT NULL,
    `type` VARCHAR(30) NOT NULL DEFAULT 'INFO',
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `related_booking_id` BIGINT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    INDEX `idx_notification_user` (`user_id`),
    INDEX `idx_notification_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 7. SAMPLE SEED DATA
-- Default Passwords (BCrypt Encrypted):
-- Admin: Admin@123
-- Faculty: Faculty@123
-- Student: Student@123
-- ==============================================================================

-- 6.1 Seed Users
INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`, `phone`, `department`, `admin_code`, `is_super_admin`, `is_active`) VALUES
(1, 'System Administrator', 'admin@campus.edu', '$2a$10$Q7eY79M9FzB436v4QkI7UuvFk9c6tS5gP2ZgM.e/8u7i2P1aVz1sW', 'ADMIN', '9876543210', 'IT Operations', 'ADM-001', TRUE, TRUE);

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`, `phone`, `department`, `employee_id`, `designation`, `is_active`) VALUES
(2, 'Dr. Robert Smith', 'faculty@campus.edu', '$2a$10$vN9d2g5hN1z3C4b8eF6iGuP8r2k5L7m4W1s9Q3t5V7x2Z4b6N8m0a', 'FACULTY', '9876543211', 'Computer Science', 'FAC-CS-101', 'Associate Professor', TRUE),
(3, 'Dr. Eleanor Vance', 'faculty2@campus.edu', '$2a$10$vN9d2g5hN1z3C4b8eF6iGuP8r2k5L7m4W1s9Q3t5V7x2Z4b6N8m0a', 'FACULTY', '9876543212', 'Electronics & Comm', 'FAC-EC-102', 'Professor', TRUE);

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`, `phone`, `department`, `student_roll_number`, `semester`, `is_active`) VALUES
(4, 'Aarav Sharma', 'student@campus.edu', '$2a$10$uA3z7k8bL9x4C1d2eF5hIuO7r1k4L6m3W0s8Q2t4V6x1Z3b5N7m9z', 'STUDENT', '9876543213', 'Computer Applications (MCA)', 'MCA202401', 3, TRUE),
(5, 'Priya Patel', 'student2@campus.edu', '$2a$10$uA3z7k8bL9x4C1d2eF5hIuO7r1k4L6m3W0s8Q2t4V6x1Z3b5N7m9z', 'STUDENT', '9876543214', 'Information Technology', 'IT202415', 4, TRUE);

-- 6.2 Seed Categories
INSERT INTO `resource_categories` (`category_id`, `category_name`, `description`, `icon_class`) VALUES
(1, 'Computer Laboratories', 'High-performance computing labs with workstations, network switches, and GPU clusters.', 'bi-laptop'),
(2, 'Seminar Halls', 'Auditorium spaces with acoustic treatment, dual laser projection, and stage lighting.', 'bi-building'),
(3, 'Smart Classrooms', 'Interactive digital board rooms for lectures, presentations, and tutorials.', 'bi-easel2'),
(4, 'Meeting Rooms', 'Boardrooms equipped for committee meetings, departmental reviews, and interviews.', 'bi-people'),
(5, 'Sports Facilities', 'Campus athletics facilities, indoor courts, and recreation centers.', 'bi-trophy');

-- 6.3 Seed Resources
INSERT INTO `resources` (`resource_id`, `category_id`, `resource_name`, `location`, `capacity`, `description`, `facilities`, `status`, `image_url`) VALUES
(1, 1, 'Computer Lab 1 (AI & ML Lab)', 'Block B - 1st Floor, Room 104', 45, 'State-of-the-art laboratory equipped with 45 Dell Core-i7 workstations and Nvidia RTX GPUs.', '45 Workstations, 1 Gbps LAN, Dual 4K Projectors, UPS Backup, AC', 'AVAILABLE', 'https://images.unsplash.com/photo-1562774053-701939374585?w=600&auto=format&fit=crop&q=80'),
(2, 1, 'Computer Lab 2 (Software Engineering)', 'Block B - 2nd Floor, Room 205', 40, 'General software engineering and coding lab with complete Linux / Windows dual boot.', '40 Workstations, Central Projector, Whiteboard, High-Speed WiFi', 'AVAILABLE', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80'),
(3, 2, 'APJ Abdul Kalam Seminar Hall', 'Main Academic Block - Ground Floor', 250, 'Tiered institutional seminar hall ideal for national conferences, workshops, and guest talks.', '250 Tiered Seats, Stage Podium, Dual 1080p Laser Projectors, Surround Sound, Video Recording', 'AVAILABLE', 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80'),
(4, 2, 'Sir CV Raman Auditorium', 'Science & Research Block - 3rd Floor', 400, 'Grand auditorium for university convocations, cultural symposiums, and major keynote events.', '400 Ergonomic Seats, Full Stage Truss Lighting, Wireless Microphones, Green Rooms, Central HVAC', 'AVAILABLE', 'https://images.unsplash.com/photo-1511578314322-379afb476865?w=600&auto=format&fit=crop&q=80'),
(5, 3, 'Smart Classroom 101', 'Academic Block A - 1st Floor, Room 101', 60, 'Equipped with 86-inch interactive touchscreen smartboard and hybrid learning video camera.', 'Interactive Smartboard, Audio Amplification, Document Camera, Ergonomic Benches', 'AVAILABLE', 'https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=600&auto=format&fit=crop&q=80'),
(6, 3, 'Smart Classroom 204', 'Academic Block A - 2nd Floor, Room 204', 70, 'Spacious lecture room with flexible seating arrangements for group discussions.', 'Smart Projector, Motorized Screen, PA System, 70 Capacity Seating', 'AVAILABLE', 'https://images.unsplash.com/photo-1577495508048-b635879837f1?w=600&auto=format&fit=crop&q=80'),
(7, 4, 'Executive Board Room', 'Administrative Block - 2nd Floor', 20, 'Executive boardroom for syndicate discussions, board meetings, and accreditation panels.', 'Conference Table, 20 Leather Chairs, 75-inch Display, Polycom Video Conferencing, High WiFi', 'AVAILABLE', 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=600&auto=format&fit=crop&q=80'),
(8, 5, 'Indoor Badminton Court 1', 'Sports & Fitness Complex - Court 1', 8, 'Synthetic wooden surface tournament-standard indoor badminton court.', 'Wooden Flooring, Anti-glare Floodlights, Electronic Scoreboard, Spectator Gallery', 'AVAILABLE', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600&auto=format&fit=crop&q=80');

-- 6.4 Seed Sample Bookings
INSERT INTO `bookings` (`booking_id`, `user_id`, `resource_id`, `booking_date`, `start_time`, `end_time`, `purpose`, `status`, `admin_remark`, `attendees_count`) VALUES
(1, 2, 1, CURRENT_DATE(), '09:00:00', '11:00:00', 'MCA Cloud Computing Lab Practical Examination', 'APPROVED', 'Approved by HOD and Dean.', 42),
(2, 4, 3, DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '14:00:00', '16:30:00', 'MCA Technical Coding Club Hackathon Orientation', 'PENDING', NULL, 80),
(3, 3, 7, CURRENT_DATE(), '15:00:00', '16:00:00', 'Departmental Curriculum Revision Committee Meeting', 'APPROVED', 'Direct Faculty Fast-Track Approval', 12),
(4, 5, 8, DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '17:00:00', '18:00:00', 'Inter-Department Badminton Doubles Practice', 'APPROVED', 'Standard slot approval', 4);
