# Campus Resource Booking System (MCA Academic Project)

A centralized, full-stack institutional facility and equipment reservation platform engineered with **Java 17+, Spring Boot 3, Spring Data JPA / Hibernate, MySQL, and a modern responsive HTML5/CSS3/JavaScript frontend**.

---

## 1. Project Title & Overview

**Title:** Campus Resource Booking System  
**Academic Target:** Master of Computer Applications (MCA) Degree Project  
**Author:** Pair programmed with Antigravity AI Assistant  
**Architecture:** Layered Enterprise Architecture (Controller &rarr; Service &rarr; Repository &rarr; Database)

The **Campus Resource Booking System** solves scheduling conflicts, overlapping reservations, and facility underutilization across academic institutions. It provides a centralized, role-based platform for reserving classrooms, high-performance computing laboratories, seminar halls, auditoriums, meeting rooms, and sports facilities.

---

## 2. Key System Features

- **Strict Zero Double-Booking Guarantee:** Backend interval overlap checking prevents concurrent conflicting reservations on the same resource (`existing.start_time < requested.end_time AND existing.end_time > requested.start_time`).
- **Role-Based Polymorphic Policies:**
  - **Student Tier:** Restricted to max 3 hours/reservation, max 3 concurrent active bookings, mandatory administrator vetting.
  - **Faculty Tier:** Priority booking up to 8 hours/reservation, up to 10 active bookings, fast-tracked instant approval.
  - **Administrator Tier:** Full campus-wide oversight, 24-hour booking capabilities, override controls, user lifecycle management, and operational reports.
- **Facility Catalog & Dynamic Filtering:** Multi-criteria search and filter across resource categories, minimum capacity thresholds, and operational statuses (`AVAILABLE`, `MAINTENANCE`, `INACTIVE`).
- **Live Conflict Pre-Check:** Real-time asynchronous slot verification as users pick dates and time intervals before form submission.
- **Self-Service Reservation Management:** User dashboard displaying active, approved, and completed bookings with cancellation and real-time status pills.
- **Dynamic QR Code Entry Passes:** Automated generation of unique cryptographic check-in pass codes (`CRB-PASS-XXXXXX`) and scannable digital entry passes for approved reservations.
- **Physical Entrance Verification:** Facility managers and administrators can scan QR codes or enter pass codes to instantly verify attendance and record check-in timestamps.
- **In-App Notification Center:** Real-time notification bell in navigation with live unread badge counters, status transition alerts on booking submission, approvals, rejections, and check-ins.
- **Administrative Operations:** Complete facility CRUD, soft-deletions to preserve historical booking records, approval queue, and rejection with administrative remarks.
- **Reporting & Operational Analytics:** Real-time aggregation pipelines computing facility demand ratings, category volume, status breakdown, and CSV export.

---

## 3. Technology Stack

### Backend
- **Language:** Java 17 LTS (Microsoft OpenJDK / Oracle OpenJDK)
- **Framework:** Spring Boot 3.2.4
- **Web:** Spring Web (REST Controllers, Global Exception Handling, Content Negotiation)
- **ORM / Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8.0 (with H2 in-memory dev profile support)
- **Security & Tokens:** Spring Security Crypto (BCrypt password hashing), JJWT (JSON Web Tokens)
- **Build & Dependency Tool:** Apache Maven 3.9+ (includes `mvnw` Maven Wrapper)

### Frontend
- **Markup & Styling:** HTML5, Vanilla CSS3 (custom academic design system), Bootstrap 5.3, Bootstrap Icons
- **Scripting:** Modern Vanilla JavaScript (ES6+ Fetch API, async/await, DOM manipulation)
- **Visualization:** Chart.js 4.4 (for administrative analytics)
- *Note:* Built without heavy frameworks like React or Angular, running as static assets directly served by Spring Boot on port 8080.

---

## 4. Java Programming Concepts Implemented

| Concept | Implementation Location | Practical Purpose in System |
| :--- | :--- | :--- |
| **Encapsulation** | [`User.java`](file:///src/main/java/com/campusbooking/entity/User.java), [`Booking.java`](file:///src/main/java/com/campusbooking/entity/Booking.java), [`Resource.java`](file:///src/main/java/com/campusbooking/entity/Resource.java) | Strict private member fields, defensive accessors/mutators, bean validation constraints (`@NotBlank`, `@Min`, `@Size`). |
| **Inheritance** | [`User.java`](file:///src/main/java/com/campusbooking/entity/User.java) &rarr; [`Student.java`](file:///src/main/java/com/campusbooking/entity/Student.java), [`Faculty.java`](file:///src/main/java/com/campusbooking/entity/Faculty.java), [`Admin.java`](file:///src/main/java/com/campusbooking/entity/Admin.java) | Single-table JPA inheritance mapping to `users` table with role-specific subclass attributes (roll numbers, designations). |
| **Polymorphism** | [`BookingServiceImpl.java`](file:///src/main/java/com/campusbooking/service/impl/BookingServiceImpl.java#L110-L150) | Polymorphic calls on abstract `User` references (`user.canCreateBookings()`, `user.getMaxBookingHoursPerDay()`, `user.requiresAdminApproval()`). |
| **Abstraction** | [`User.java`](file:///src/main/java/com/campusbooking/entity/User.java), Service Interfaces | Abstract base classes define mandatory template contracts that cannot be instantiated directly without concrete role policies. |
| **Interfaces** | [`Bookable.java`](file:///src/main/java/com/campusbooking/interfaces/Bookable.java), [`Manageable.java`](file:///src/main/java/com/campusbooking/interfaces/Manageable.java), [`Authenticatable.java`](file:///src/main/java/com/campusbooking/interfaces/Authenticatable.java) | Decouples capability contracts: `Resource` implements `Bookable` & `Manageable`; `User` implements `Authenticatable` & `Manageable`. |
| **Method Overloading** | [`ResourceService.java`](file:///src/main/java/com/campusbooking/service/ResourceService.java), [`ValidationUtil.java`](file:///src/main/java/com/campusbooking/util/ValidationUtil.java) | Multiple overloaded signatures: `findResources()`, `findResources(query)`, `findResources(catId, cap)`, `validateBookingSchedule(...)`. |
| **Method Overriding** | Subclasses in `com.campusbooking.entity` | `Student` overrides `canCreateBookings() -> false`, `Faculty` & `Admin` override `canCreateBookings() -> true`, plus role-specific quotas and approval workflows. |
| **Collections Framework** | Services & Repositories | Active use of `List`, `ArrayList`, `Map`, `HashMap`, `Set`, and `HashSet` for slot conflict sets and reporting groups. |
| **Exception Handling** | [`exception/`](file:///src/main/java/com/campusbooking/exception) package | Custom exceptions (`BookingConflictException`, `ResourceUnavailableException`, etc.) caught globally by [`GlobalExceptionHandler`](file:///src/main/java/com/campusbooking/exception/GlobalExceptionHandler.java). |
| **Enums** | [`enums/`](file:///src/main/java/com/campusbooking/enums) package | `UserRole`, `BookingStatus`, `ResourceStatus` with helper methods (`occupiesSlot()`, `isCancellable()`). |
| **Java Date/Time API** | Entities, DTOs, Queries | Modern `LocalDate`, `LocalTime`, `LocalDateTime`, and `Duration` throughout the scheduling lifecycle. |
| **Streams & Lambdas** | [`ReportServiceImpl.java`](file:///src/main/java/com/campusbooking/service/impl/ReportServiceImpl.java), [`GlobalExceptionHandler.java`](file:///src/main/java/com/campusbooking/exception/GlobalExceptionHandler.java) | Filter streams, map to DTOs, grouping by category (`Collectors.groupingBy`), summing usage hours (`mapToDouble`). |
| **Layered Architecture** | `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `config/`, `util/` | Strict separation of concerns; no database queries or raw SQL logic inside controllers or UI scripts. |

---

## 5. Database Schema & Tables

**Database Name:** `campus_resource_booking`  
**DDL Script:** [`campus_resource_booking.sql`](file:///campus_resource_booking.sql)

```text
users
├── user_id (BIGINT, PK, AUTO_INCREMENT)
├── name (VARCHAR(100), NOT NULL)
├── email (VARCHAR(120), NOT NULL, UNIQUE)
├── password (VARCHAR(255), NOT NULL, BCrypt Hashed)
├── role (VARCHAR(20), NOT NULL, STUDENT | FACULTY | ADMIN)
├── phone (VARCHAR(20))
├── department (VARCHAR(100))
├── student_roll_number (VARCHAR(50))
├── semester (INT)
├── employee_id (VARCHAR(50))
├── designation (VARCHAR(100))
├── admin_code (VARCHAR(50))
├── is_super_admin (BOOLEAN, DEFAULT TRUE)
├── is_active (BOOLEAN, NOT NULL, DEFAULT TRUE)
└── created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

resource_categories
├── category_id (BIGINT, PK, AUTO_INCREMENT)
├── category_name (VARCHAR(100), NOT NULL, UNIQUE)
├── description (VARCHAR(500))
└── icon_class (VARCHAR(50))

resources
├── resource_id (BIGINT, PK, AUTO_INCREMENT)
├── category_id (BIGINT, FK -> resource_categories)
├── resource_name (VARCHAR(150), NOT NULL)
├── location (VARCHAR(200), NOT NULL)
├── capacity (INT, NOT NULL)
├── description (TEXT)
├── facilities (VARCHAR(500))
├── status (VARCHAR(30), AVAILABLE | MAINTENANCE | INACTIVE)
├── image_url (VARCHAR(255))
└── created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

bookings
├── booking_id (BIGINT, PK, AUTO_INCREMENT)
├── user_id (BIGINT, FK -> users)
├── resource_id (BIGINT, FK -> resources)
├── booking_date (DATE, NOT NULL)
├── start_time (TIME, NOT NULL)
├── end_time (TIME, NOT NULL)
├── purpose (VARCHAR(500), NOT NULL)
├── status (VARCHAR(30), PENDING | APPROVED | REJECTED | CANCELLED | COMPLETED)
├── admin_remark (VARCHAR(500))
├── attendees_count (INT, DEFAULT 1)
└── created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
```

---

## 6. How to Run the Project

### Prerequisites
1. **Java 17 LTS or newer** (`java -version`)
2. **MySQL 8.0** (or use the built-in instant H2 profile)
3. **Maven 3.9+** (or use `./mvnw` / `mvnw.cmd` included in the root)

### Step 1: Database Setup (MySQL)
Open your MySQL client or MySQL Workbench:
```sql
SOURCE campus_resource_booking.sql;
```
*(Or let Hibernate auto-generate the schema on startup)*

### Step 2: Configure Database Credentials
Edit [`src/main/resources/application-mysql.properties`](file:///src/main/resources/application-mysql.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_resource_booking?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
Or set environment variables in your terminal:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

### Step 3: Run the Application
From the project root:

**With MySQL Profile (Default):**
```powershell
./mvnw spring-boot:run
```

**With Zero-Config H2 Profile (Instant Out-of-the-Box Demo):**
```powershell
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```
*Or with packaged JAR:*
```powershell
java -jar target/campus-resource-booking-1.0.0.jar
```

### Step 4: Open the Frontend
Once started, Tomcat binds to port 8080. Open your web browser:
```text
http://localhost:8080/index.html
```

---

## 7. Test Credentials

The system comes pre-loaded with sample accounts:

| Role | Email | Password | Tier Policy |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@campus.edu` | `Admin@123` | Full access, approvals, reports, user control |
| **Faculty Member** | `faculty@campus.edu` | `Faculty@123` | Up to 8 hrs/slot, 10 active bookings, priority |
| **Faculty Member 2** | `faculty2@campus.edu` | `Faculty@123` | Electronics & Communication Department |
| **Student** | `student@campus.edu` | `Student@123` | Up to 3 hrs/slot, 3 active bookings, requires approval |
| **Student 2** | `student2@campus.edu` | `Student@123` | Information Technology Department |

*Tip:* On the login page ([`login.html`](file:///src/main/resources/static/login.html)), click any of the "Quick Evaluator Logins" buttons for instant 1-click access.

---

## 8. REST API Documentation

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Create new student or faculty account.
- `POST /api/auth/login` - Authenticate with email & password, returns JWT token.
- `GET  /api/auth/me` - Get current authenticated user profile.

### Facilities (`/api/resources`)
- `GET    /api/resources` - Browse facilities with filters (`categoryId`, `minCapacity`, `status`, `search`).
- `GET    /api/resources/{id}` - Get single facility details.
- `GET    /api/resources/{id}/availability` - Query parameters `date`, `startTime`, `endTime` for slot check.
- `GET    /api/resources/{id}/schedule` - Get day's reservation schedule timeline.
- `GET    /api/resources/categories` - List all resource categories.
- `POST   /api/resources` *(Admin)* - Create new facility.
- `PUT    /api/resources/{id}` *(Admin)* - Update facility details.
- `PATCH  /api/resources/{id}/status` *(Admin)* - Update status (`AVAILABLE`, `MAINTENANCE`, `INACTIVE`).
- `DELETE /api/resources/{id}` *(Admin)* - Soft-deactivate facility.

### Bookings (`/api/bookings`)
- `GET    /api/bookings` - List bookings (user's bookings for student/faculty; all bookings for admin).
- `GET    /api/bookings/{id}` - Get booking details.
- `POST   /api/bookings` - Create reservation (runs double-booking check & returns HTTP 409 on conflict).
- `PUT    /api/bookings/{id}/cancel` - Cancel reservation if cancellable.
- `PUT    /api/bookings/{id}/approve` *(Admin)* - Approve pending booking.
- `PUT    /api/bookings/{id}/reject` *(Admin)* - Reject booking with required remark.
- `PUT    /api/bookings/{id}/complete` *(Admin)* - Mark approved session completed.
- `POST   /api/bookings/{id}/check-in` *(Admin)* - Confirm physical attendee arrival & timestamp for approved booking.
- `POST   /api/bookings/verify-checkin?code={passCode}` *(Admin)* - Verify scanned QR code pass and execute check-in.

### In-App Notifications (`/api/notifications`)
- `GET   /api/notifications` - Retrieve list of notifications for authenticated user.
- `GET   /api/notifications/unread-count` - Get count of unread notifications.
- `PATCH /api/notifications/{id}/read` - Mark a specific notification as read.
- `POST  /api/notifications/mark-all-read` - Mark all notifications as read for current user.

### Administrative Control & Analytics (`/api/admin`)
- `GET   /api/admin/users` - Search users by query and role.
- `PATCH /api/admin/users/{id}/status` - Enable or disable portal access.
- `GET   /api/admin/reports/summary` - Aggregate metrics for dashboard KPIs & charts.
- `GET   /api/admin/reports/bookings` - Multi-criteria filtered reservation report.
- `GET   /api/admin/reports/resource-usage` - Usage frequency ranked by demand.

---

## 9. Frontend Pages Directory

All 16 required academic pages are implemented in [`src/main/resources/static/`](file:///src/main/resources/static):

1. `index.html` - Homepage featuring quick facility statuses and capabilities.
2. `login.html` - Authentication screen with 1-click evaluator login buttons.
3. `register.html` - Role-aware registration form (Student vs Faculty).
4. `student-dashboard.html` - Student portal with reservation statistics and active requests.
5. `faculty-dashboard.html` - Faculty portal with priority booking shortcuts.
6. `resources.html` - Facility catalog with real-time text search, category, capacity, and status filters.
7. `resource-details.html` - Facility amenities, specifications, and daily booking timeline.
8. `booking.html` - Interactive booking form with live slot availability verification.
9. `my-bookings.html` - Reservation management with status filtering, digital QR entry passes, and cancellation modal.
10. `admin-dashboard.html` - Admin control panel with KPI counters, Chart.js graphs, check-in scanner, and approval queue.
11. `admin-users.html` - User table with role filters and account activation toggle.
12. `admin-resources.html` - Resource management with Add/Edit modals and operational status toggles.
13. `admin-bookings.html` - Master reservations list with Approve, Reject, Complete, and Instant Check-In Scanner.
14. `admin-reports.html` - Analytics report with CSV export and usage frequency rankings.
15. `profile.html` - User profile details and password change form.
16. `404.html` - Friendly 404 error page.

---

## 10. Completed & Future Enhancements

### Implemented Enhancements &radic;
1. **Dynamic QR Code Entry Passes:** Automated cryptographic pass code generation (`CRB-PASS-XXXXXXXX`) and interactive digital modal pass for approved reservations.
2. **Physical Entrance Check-In:** Admin scanner modal and table action verifying attendee arrivals and recording timestamped check-in records.
3. **In-App Notification Center:** Real-time notification bell with dynamic badge counters and status alerts across the platform.

### Future Roadmap
1. **Email / SMS Notifications:** Integration with Spring Mail / Twilio to send automated confirmation alerts upon booking approval.
2. **Recurring Bookings:** Schedule recurring lecture slots throughout an academic semester.
3. **OAuth2 / SSO:** Single Sign-On integration with Google Workspace or Microsoft Azure AD for institutional domains.
