package com.hostel.db;

import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.*;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * High-performance JDBC & Data Manager for the Smart Sustainable Hostel Management System.
 * Uses embedded H2 SQL database by default for zero-setup, persistent local storage
 * across restarts. Core modules (Auth, Students, Rooms, Fees) run on real SQL tables.
 * Live MySQL connection option is preserved for administrative configuration.
 */
public class DatabaseManager {
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    // H2 Embedded Connection Settings (Persistent local database file)
    private static final String H2_URL = "jdbc:h2:./data/hostel_db;NON_KEYWORDS=YEAR;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    // Optional MySQL Connection Settings
    private String dbHost = "localhost";
    private int dbPort = 3306;
    private String dbName = "hostel_db";
    private String dbUser = "root";
    private String dbPassword = "";
    private boolean mysqlConnected = false;
    private boolean useMySQL = false;

    // In-memory stores for secondary modules (trimmed to 1-2 records)
    private final Map<Integer, Visitor> visitors = new ConcurrentHashMap<>();
    private final Map<Integer, LeaveRequest> leaveRequests = new ConcurrentHashMap<>();
    private final Map<Integer, Complaint> complaints = new ConcurrentHashMap<>();
    private final List<ComplianceRecord> complianceRecords = new CopyOnWriteArrayList<>();
    private final List<SustainabilityUsage> sustainabilityUsages = new CopyOnWriteArrayList<>();
    private final Map<Integer, Asset> assets = new ConcurrentHashMap<>();
    private final List<Notice> notices = new CopyOnWriteArrayList<>();
    private final List<MessMenu> messMenus = new CopyOnWriteArrayList<>();
    private final List<MessFeedback> messFeedbacks = new CopyOnWriteArrayList<>();
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();
    private final List<ActivityLog> activityLogs = new CopyOnWriteArrayList<>();

    // ID Generators for secondary modules
    private final AtomicInteger visitorIdGen = new AtomicInteger(100);
    private final AtomicInteger leaveIdGen = new AtomicInteger(100);
    private final AtomicInteger complaintIdGen = new AtomicInteger(100);
    private final AtomicInteger complianceIdGen = new AtomicInteger(100);
    private final AtomicInteger sustainabilityIdGen = new AtomicInteger(100);
    private final AtomicInteger assetIdGen = new AtomicInteger(100);
    private final AtomicInteger noticeIdGen = new AtomicInteger(100);
    private final AtomicInteger feedbackIdGen = new AtomicInteger(100);
    private final AtomicInteger notificationIdGen = new AtomicInteger(100);
    private final AtomicInteger activityIdGen = new AtomicInteger(100);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DatabaseManager() {
        initDatabase();
        seedSecondaryModules();
        attemptMySQLConnection();
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    private Connection getConnection() throws SQLException {
        if (useMySQL && mysqlConnected) {
            try {
                String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                return DriverManager.getConnection(url, dbUser, dbPassword);
            } catch (SQLException e) {
                // Fallback to H2 on MySQL connection failure
            }
        }
        return DriverManager.getConnection(H2_URL, H2_USER, H2_PASS);
    }

    private void initDatabase() {
        try {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Register H2 driver
            Class.forName("org.h2.Driver");

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                // 1. Users table (Authentication & Roles)
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "username VARCHAR(100) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "full_name VARCHAR(255) NOT NULL, " +
                        "role VARCHAR(50) NOT NULL, " +
                        "email VARCHAR(255), " +
                        "phone VARCHAR(50), " +
                        "student_id VARCHAR(50), " +
                        "room_number VARCHAR(50)" +
                        ");");

                // 2. Rooms table
                stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "room_number VARCHAR(50) UNIQUE NOT NULL, " +
                        "block VARCHAR(100) NOT NULL, " +
                        "floor INT NOT NULL, " +
                        "capacity INT NOT NULL, " +
                        "occupied INT NOT NULL, " +
                        "fee_per_term DOUBLE NOT NULL, " +
                        "room_type VARCHAR(100) NOT NULL, " +
                        "status VARCHAR(50) NOT NULL, " +
                        "amenities VARCHAR(255)" +
                        ");");

                // 3. Students table
                stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "student_id VARCHAR(50) UNIQUE NOT NULL, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "department VARCHAR(255) NOT NULL, " +
                        "\"year\" VARCHAR(50) NOT NULL, " +
                        "contact VARCHAR(50) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL, " +
                        "emergency_contact VARCHAR(50), " +
                        "guardian_contact VARCHAR(50), " +
                        "blood_group VARCHAR(10), " +
                        "room_id INT, " +
                        "room_number VARCHAR(50), " +
                        "block VARCHAR(100), " +
                        "status VARCHAR(50) NOT NULL, " +
                        "admission_date VARCHAR(50) NOT NULL" +
                        ");");

                // 4. Fee Records table
                stmt.execute("CREATE TABLE IF NOT EXISTS fee_records (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "student_id INT NOT NULL, " +
                        "student_name VARCHAR(255) NOT NULL, " +
                        "student_roll_no VARCHAR(50) NOT NULL, " +
                        "room_number VARCHAR(50), " +
                        "total_fee DOUBLE NOT NULL, " +
                        "paid_amount DOUBLE NOT NULL, " +
                        "balance DOUBLE NOT NULL, " +
                        "status VARCHAR(50) NOT NULL, " +
                        "academic_term VARCHAR(100) NOT NULL, " +
                        "due_date VARCHAR(50) NOT NULL" +
                        ");");

                // 5. Payment Transactions table
                stmt.execute("CREATE TABLE IF NOT EXISTS payment_transactions (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "fee_id INT NOT NULL, " +
                        "student_id INT NOT NULL, " +
                        "student_name VARCHAR(255), " +
                        "receipt_no VARCHAR(100) NOT NULL, " +
                        "amount DOUBLE NOT NULL, " +
                        "payment_method VARCHAR(50) NOT NULL, " +
                        "transaction_ref VARCHAR(255), " +
                        "payment_date VARCHAR(50) NOT NULL, " +
                        "recorded_by VARCHAR(255) NOT NULL, " +
                        "notes VARCHAR(255)" +
                        ");");

                try {
                    stmt.execute("ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS student_name VARCHAR(255);");
                    stmt.execute("ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS notes VARCHAR(255);");
                } catch (Exception ignored) {}

                // Seed initial SQL records if empty
                seedCoreTablesIfEmpty(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedCoreTablesIfEmpty(Connection conn) throws SQLException {
        // Seed Users (1 Admin, 1 Warden, 1 Student)
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password, full_name, role, email, phone, student_id, room_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    // Admin
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.setString(3, "Dr. Sarah Jenkins");
                    ps.setString(4, "ADMIN");
                    ps.setString(5, "admin@hostel.edu");
                    ps.setString(6, "+91 98450 11223");
                    ps.setString(7, null);
                    ps.setString(8, null);
                    ps.executeUpdate();

                    // Warden
                    ps.setString(1, "warden");
                    ps.setString(2, "warden123");
                    ps.setString(3, "Prof. Marcus Vance");
                    ps.setString(4, "WARDEN");
                    ps.setString(5, "warden@hostel.edu");
                    ps.setString(6, "+91 98450 33445");
                    ps.setString(7, null);
                    ps.setString(8, null);
                    ps.executeUpdate();

                    // Student
                    ps.setString(1, "student");
                    ps.setString(2, "student123");
                    ps.setString(3, "Aarav Sharma");
                    ps.setString(4, "STUDENT");
                    ps.setString(5, "aarav.sharma@college.edu");
                    ps.setString(6, "+91 98765 43210");
                    ps.setString(7, "STU-2024-001");
                    ps.setString(8, "A-101");
                    ps.executeUpdate();
                }
            }
        }

        // Seed Rooms (1 sample room)
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO rooms (room_number, block, floor, capacity, occupied, fee_per_term, room_type, status, amenities) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    // Room 1
                    ps.setString(1, "A-101");
                    ps.setString(2, "A Block (North Wing)");
                    ps.setInt(3, 1);
                    ps.setInt(4, 2);
                    ps.setInt(5, 1);
                    ps.setDouble(6, 45000.00);
                    ps.setString(7, "Non-AC 2-Sharing");
                    ps.setString(8, "PARTIAL");
                    ps.setString(9, "2 Study Desks, Attached Bath, Balcony");
                    ps.executeUpdate();
                }
            }
        }

        // Seed Students (1 sample student)
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO students (student_id, name, department, \"year\", contact, email, emergency_contact, guardian_contact, blood_group, room_id, room_number, block, status, admission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    // Student 1
                    ps.setString(1, "STU-2024-001");
                    ps.setString(2, "Aarav Sharma");
                    ps.setString(3, "Computer Science");
                    ps.setString(4, "3rd Year");
                    ps.setString(5, "+91 98765 43210");
                    ps.setString(6, "aarav.sharma@college.edu");
                    ps.setString(7, "+91 94432 12345");
                    ps.setString(8, "+91 94432 12346");
                    ps.setString(9, "O+");
                    ps.setInt(10, 1);
                    ps.setString(11, "A-101");
                    ps.setString(12, "A Block (North Wing)");
                    ps.setString(13, "ACTIVE");
                    ps.setString(14, "2024-08-01");
                    ps.executeUpdate();
                }
            }
        }

        // Seed Fee Records (1 sample record: pending with balance for Razorpay)
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM fee_records")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO fee_records (student_id, student_name, student_roll_no, room_number, total_fee, paid_amount, balance, status, academic_term, due_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    // Fee 1: Aarav Sharma (Pending balance of ₹20,000 for Razorpay test payment flow)
                    ps.setInt(1, 1);
                    ps.setString(2, "Aarav Sharma");
                    ps.setString(3, "STU-2024-001");
                    ps.setString(4, "A-101");
                    ps.setDouble(5, 45000.00);
                    ps.setDouble(6, 25000.00);
                    ps.setDouble(7, 20000.00);
                    ps.setString(8, "PARTIAL");
                    ps.setString(9, "Fall 2026");
                    ps.setString(10, "2026-09-30");
                    ps.executeUpdate();
                }
            }
        }

        // Seed Payment Transactions (1 sample record)
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM payment_transactions")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO payment_transactions (fee_id, student_id, student_name, receipt_no, amount, payment_method, transaction_ref, payment_date, recorded_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, 1);
                    ps.setInt(2, 1);
                    ps.setString(3, "Aarav Sharma");
                    ps.setString(4, "RCP-2026-001");
                    ps.setDouble(5, 25000.00);
                    ps.setString(6, "ONLINE_UPI");
                    ps.setString(7, "pay_demo_init_001");
                    ps.setString(8, "2026-08-15 10:30:00");
                    ps.setString(9, "Admin Office");
                    ps.setString(10, "Hostel admission installment");
                    ps.executeUpdate();
                }
            }
        }
    }

    private void seedSecondaryModules() {
        // Trimmed seed data: exactly 1 record per module across all roles
        visitors.put(1, new Visitor(1, "Rajesh Sharma", "+91 98220 11223", "Parent", 1, "Aarav Sharma", "A-101", "Routine family visit", "2026-09-07", "10:15", null, "National ID", "ID-901", Visitor.VisitorStatus.INSIDE, "Security Gate 1"));

        leaveRequests.put(1, new LeaveRequest(1, "LR-2024-101", 1, "Aarav Sharma", "STU-2024-001", "A-101", LeaveRequest.LeaveType.HOME_VISIT, "2026-09-18", "2026-09-21", "Family wedding function", "Mumbai", "+91 94432 12345", true, LeaveRequest.LeaveStatus.PENDING, "2026-09-07 09:20:00", null, "", null));

        complaints.put(1, new Complaint(1, "CMP-2024-001", 1, "Aarav Sharma", "A-101", Complaint.Category.PLUMBING, "Bathroom tap is leaking continuously in Room A-101.", "High", "2026-09-06", "Ramesh (Plumber)", Complaint.Status.IN_PROGRESS, "Under inspection", null));

        complianceRecords.add(new ComplianceRecord(1, 1, "Aarav Sharma", "STU-2024-001", "A-101", "Unauthorized Electrical Appliances", "2026-08-20", "Electric kettle found during routine inspection", "Written Warning Issued", 0.0, "Admin Safety Cell", true));

        sustainabilityUsages.add(new SustainabilityUsage(1, "2026-09", "Sep 2026", 13800.0, 295.0, 14200.0, 310.0, "Efficiency drive active", "2026-09-07", "Energy Officer"));

        assets.put(1, new Asset(1, "AST-FUR-101", "Air Conditioner 1.5 Ton", "Appliance", 1, "A-101", Asset.Condition.GOOD, "In Use", 35000.0, "2026-08-15", "Samsung Inverter Unit"));

        notices.add(new Notice(1, "Eco-Living Energy Conservation Drive", "All residents are requested to turn off AC and lights when leaving rooms. Solar backup test scheduled this Saturday.", "Sustainability", Notice.Priority.HIGH, "Prof. Marcus Vance", "WARDEN", "2026-09-05", "ALL", true, false));

        messMenus.add(new MessMenu(1, "Monday", MessMenu.MealType.BREAKFAST, "Idli Sambar, Coconut Chutney, Banana & Masala Chai", "Kesari Bath", "07:30 AM - 09:15 AM", "Pure Veg"));

        messFeedbacks.add(new MessFeedback(1, 1, "Aarav Sharma", "A-101", "Lunch", 4, "Taste & Food Quality", "Lunch dal tadka was excellent.", false, "Reviewed", "2026-09-06"));

        notifications.add(new Notification(1, 0, "ALL", "Energy Target Achieved", "Electricity consumption dropped by 2.8% this billing cycle.", Notification.NotificationType.SUSTAINABILITY, "2026-09-07 10:00:00", false, "MODULE_SUSTAINABILITY"));

        activityLogs.add(new ActivityLog(1, "2026-09-07 09:20:00", "Aarav Sharma", "STUDENT", "LEAVE_SUBMITTED", "Submitted leave request LR-2024-101", "LR-2024-101"));
    }

    public boolean attemptMySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                mysqlConnected = (conn != null && !conn.isClosed());
                return mysqlConnected;
            }
        } catch (Exception e) {
            mysqlConnected = false;
            return false;
        }
    }

    public boolean isMysqlConnected() {
        return mysqlConnected;
    }

    public void configureMySQL(String host, int port, String database, String user, String password) {
        this.dbHost = host;
        this.dbPort = port;
        this.dbName = database;
        this.dbUser = user;
        this.dbPassword = password;
        if (attemptMySQLConnection()) {
            this.useMySQL = true;
        }
    }

    // ==========================================
    // 1. AUTHENTICATION & MULTI-ACCOUNT (SQL)
    // ==========================================
    public User authenticate(String username, String password, User.Role expectedRole) {
        if (username == null || password == null) return null;
        String sql = "SELECT * FROM users WHERE (LOWER(username) = ? OR LOWER(email) = ? OR LOWER(student_id) = ?) AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String trimmed = username.trim().toLowerCase();
            ps.setString(1, trimmed);
            ps.setString(2, trimmed);
            ps.setString(3, trimmed);
            ps.setString(4, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User.Role role = User.Role.valueOf(rs.getString("role"));
                    if (expectedRole == null || role == expectedRole) {
                        User u = new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("full_name"),
                                role,
                                rs.getString("email"),
                                rs.getString("phone"),
                                rs.getString("student_id"),
                                rs.getString("room_number")
                        );
                        logActivity(u.getFullName(), u.getRole().name(), "USER_LOGGED_IN", "User logged in to " + u.getRole().getDisplayName() + " workspace", String.valueOf(u.getId()));
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        User.Role.valueOf(rs.getString("role")),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("student_id"),
                        rs.getString("room_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null) return false;
        String sql = "INSERT INTO users (username, password, full_name, role, email, phone, student_id, room_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().trim().toLowerCase());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getStudentId());
            ps.setString(8, user.getRoomNumber());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 2. STUDENTS CRUD (SQL)
    // ==========================================
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getStudentByStudentId(String studentId) {
        if (studentId == null) return null;
        String sql = "SELECT * FROM students WHERE LOWER(student_id) = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student addStudent(Student student, String actorName, String actorRole) {
        String sql = "INSERT INTO students (student_id, name, department, \"year\", contact, email, emergency_contact, guardian_contact, blood_group, room_id, room_number, block, status, admission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
                student.setStudentId("STU-2024-" + System.currentTimeMillis() % 10000);
            }
            if (student.getAdmissionDate() == null || student.getAdmissionDate().isEmpty()) {
                student.setAdmissionDate(LocalDate.now().format(DATE_FMT));
            }

            // Sync room info if allocated
            if (student.getRoomId() > 0) {
                Room r = getRoomById(student.getRoomId());
                if (r != null) {
                    student.setRoomNumber(r.getRoomNumber());
                    student.setBlock(r.getBlock());
                    updateRoomOccupancy(conn, r.getId(), r.getOccupied() + 1);
                }
            }

            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getYear());
            ps.setString(5, student.getContact());
            ps.setString(6, student.getEmail());
            ps.setString(7, student.getEmergencyContact());
            ps.setString(8, student.getParentContact());
            ps.setString(9, student.getBloodGroup());
            ps.setInt(10, student.getRoomId());
            ps.setString(11, student.getRoomNumber());
            ps.setString(12, student.getBlock());
            ps.setString(13, student.getStatus() != null ? student.getStatus().name() : "ACTIVE");
            ps.setString(14, student.getAdmissionDate());

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    student.setId(gk.getInt(1));
                }
            }

            // Create real User Login for this student so they can log in immediately
            String studentUsername = student.getStudentId().toLowerCase().replace("-", "").trim();
            addUser(new User(0, studentUsername, "student123", student.getName(), User.Role.STUDENT, student.getEmail(), student.getContact(), student.getStudentId(), student.getRoomNumber()));

            // Create default Fee Record in SQL
            createFeeRecordForStudent(student);

            logActivity(actorName, actorRole, "STUDENT_ADDED", "Enrolled student " + student.getName() + " (" + student.getStudentId() + ")", String.valueOf(student.getId()));
            AppEventBus.getInstance().publish(AppEvent.EventType.STUDENT_CREATED, student, "Student " + student.getName() + " added");
            return student;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateStudent(Student student, String actorName, String actorRole) {
        if (student == null) return false;
        String sql = "UPDATE students SET name = ?, department = ?, \"year\" = ?, contact = ?, email = ?, emergency_contact = ?, guardian_contact = ?, blood_group = ?, room_id = ?, room_number = ?, block = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getDepartment());
            ps.setString(3, student.getYear());
            ps.setString(4, student.getContact());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getEmergencyContact());
            ps.setString(7, student.getParentContact());
            ps.setString(8, student.getBloodGroup());
            ps.setInt(9, student.getRoomId());
            ps.setString(10, student.getRoomNumber());
            ps.setString(11, student.getBlock());
            ps.setString(12, student.getStatus().name());
            ps.setInt(13, student.getId());

            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                logActivity(actorName, actorRole, "STUDENT_UPDATED", "Updated profile for " + student.getName(), String.valueOf(student.getId()));
                AppEventBus.getInstance().publish(AppEvent.EventType.STUDENT_UPDATED, student, "Student " + student.getName() + " updated");
            }
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id, String actorName, String actorRole) {
        Student s = getStudentById(id);
        if (s == null) return false;
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean del = ps.executeUpdate() > 0;
            if (del) {
                if (s.getRoomId() > 0) {
                    Room r = getRoomById(s.getRoomId());
                    if (r != null) {
                        updateRoomOccupancy(conn, r.getId(), Math.max(0, r.getOccupied() - 1));
                    }
                }
                logActivity(actorName, actorRole, "STUDENT_DELETED", "Removed student record " + s.getName(), String.valueOf(id));
                AppEventBus.getInstance().publish(AppEvent.EventType.STUDENT_DELETED, s, "Student " + s.getName() + " deleted");
            }
            return del;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("year"),
                rs.getString("contact"),
                rs.getString("email"),
                rs.getString("emergency_contact"),
                rs.getString("guardian_contact"),
                rs.getString("blood_group"),
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getString("block"),
                Student.Status.valueOf(rs.getString("status")),
                rs.getString("admission_date")
        );
    }

    // ==========================================
    // 3. ROOMS CRUD (SQL)
    // ==========================================
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null) return null;
        String sql = "SELECT * FROM rooms WHERE LOWER(room_number) = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRoom(Room room) {
        if (room == null || room.getRoomNumber() == null) return false;
        String sql = "INSERT INTO rooms (room_number, block, floor, capacity, occupied, fee_per_term, room_type, status, amenities) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getBlock());
            ps.setInt(3, room.getFloor());
            ps.setInt(4, room.getCapacity());
            ps.setInt(5, room.getOccupied());
            ps.setDouble(6, room.getMonthlyRent());
            ps.setString(7, room.getRoomType());
            ps.setString(8, room.getStatus().name());
            ps.setString(9, room.getAmenities());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) room.setId(gk.getInt(1));
            }
            AppEventBus.getInstance().publish(AppEvent.EventType.ROOM_UPDATED, room, "Room " + room.getRoomNumber() + " added");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean allocateRoom(int studentId, int roomId, String actorName, String actorRole) {
        Student student = getStudentById(studentId);
        Room newRoom = getRoomById(roomId);
        if (student == null || newRoom == null || newRoom.isFull()) return false;

        try (Connection conn = getConnection()) {
            // Free previous room if assigned
            if (student.getRoomId() > 0 && student.getRoomId() != roomId) {
                Room oldRoom = getRoomById(student.getRoomId());
                if (oldRoom != null) {
                    updateRoomOccupancy(conn, oldRoom.getId(), Math.max(0, oldRoom.getOccupied() - 1));
                }
            }

            // Assign new room
            updateRoomOccupancy(conn, newRoom.getId(), newRoom.getOccupied() + 1);

            try (PreparedStatement ps = conn.prepareStatement("UPDATE students SET room_id = ?, room_number = ?, block = ? WHERE id = ?")) {
                ps.setInt(1, newRoom.getId());
                ps.setString(2, newRoom.getRoomNumber());
                ps.setString(3, newRoom.getBlock());
                ps.setInt(4, student.getId());
                ps.executeUpdate();
            }

            student.setRoomId(newRoom.getId());
            student.setRoomNumber(newRoom.getRoomNumber());
            student.setBlock(newRoom.getBlock());

            logActivity(actorName, actorRole, "ROOM_ALLOCATED", "Allocated Room " + newRoom.getRoomNumber() + " to " + student.getName(), newRoom.getRoomNumber());
            AppEventBus.getInstance().publish(AppEvent.EventType.ROOM_ALLOCATED, student, "Room " + newRoom.getRoomNumber() + " allocated to " + student.getName());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transferRoom(int studentId, int targetRoomId, String actorName, String actorRole) {
        return allocateRoom(studentId, targetRoomId, actorName, actorRole);
    }

    private void updateRoomOccupancy(Connection conn, int roomId, int newOccupied) throws SQLException {
        Room r = getRoomById(roomId);
        if (r == null) return;
        Room.RoomStatus status = newOccupied >= r.getCapacity() ? Room.RoomStatus.FULL :
                newOccupied > 0 ? Room.RoomStatus.PARTIAL : Room.RoomStatus.AVAILABLE;

        try (PreparedStatement ps = conn.prepareStatement("UPDATE rooms SET occupied = ?, status = ? WHERE id = ?")) {
            ps.setInt(1, newOccupied);
            ps.setString(2, status.name());
            ps.setInt(3, roomId);
            ps.executeUpdate();
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getString("room_number"),
                rs.getString("block"),
                rs.getInt("floor"),
                rs.getInt("capacity"),
                rs.getInt("occupied"),
                rs.getDouble("fee_per_term"),
                rs.getString("room_type"),
                Room.RoomStatus.valueOf(rs.getString("status")),
                rs.getString("amenities")
        );
    }

    // ==========================================
    // 4. FEES & PAYMENTS CRUD (SQL)
    // ==========================================
    public List<FeeRecord> getAllFeeRecords() {
        List<FeeRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM fee_records ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapFeeRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public FeeRecord getFeeRecordForStudent(int studentId) {
        String sql = "SELECT * FROM fee_records WHERE student_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFeeRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FeeRecord getFeeRecordByStudentId(int studentId) {
        return getFeeRecordForStudent(studentId);
    }

    public FeeRecord getFeeRecordById(int feeId) {
        String sql = "SELECT * FROM fee_records WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFeeRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FeeRecord createFeeRecordForStudent(Student s) {
        double feeAmount = 45000.00;
        if (s.getRoomId() > 0) {
            Room r = getRoomById(s.getRoomId());
            if (r != null) feeAmount = r.getMonthlyRent();
        }

        FeeRecord rec = new FeeRecord(0, s.getId(), s.getName(), s.getStudentId(), s.getRoomNumber(), feeAmount, 0.0, feeAmount, FeeRecord.FeeStatus.PENDING, "Fall 2026", LocalDate.now().plusDays(30).format(DATE_FMT));
        String sql = "INSERT INTO fee_records (student_id, student_name, student_roll_no, room_number, total_fee, paid_amount, balance, status, academic_term, due_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rec.getStudentId());
            ps.setString(2, rec.getStudentName());
            ps.setString(3, rec.getStudentRollNo());
            ps.setString(4, rec.getRoomNumber());
            ps.setDouble(5, rec.getTotalFee());
            ps.setDouble(6, rec.getPaidAmount());
            ps.setDouble(7, rec.getBalance());
            ps.setString(8, rec.getStatus().name());
            ps.setString(9, rec.getAcademicTerm());
            ps.setString(10, rec.getDueDate());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) rec.setId(gk.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rec;
    }

    public PaymentTransaction recordPayment(int feeId, double amount, PaymentTransaction.PaymentMethod method, String transactionRef, String actorName) {
        return recordPayment(feeId, amount, method, transactionRef, actorName, "STUDENT", "Hostel fee payment");
    }

    public PaymentTransaction recordPayment(int feeId, double amount, PaymentTransaction.PaymentMethod method, String transactionRef, String actorName, String actorRole, String notes) {
        FeeRecord fee = getFeeRecordById(feeId);
        if (fee == null || amount <= 0) return null;

        double newPaid = fee.getPaidAmount() + amount;
        double newBalance = Math.max(0, fee.getTotalFee() - newPaid);
        FeeRecord.FeeStatus newStatus = newBalance == 0 ? FeeRecord.FeeStatus.PAID : FeeRecord.FeeStatus.PARTIAL;

        String receiptNo = "RCP-2026-" + (System.currentTimeMillis() % 100000);
        String payDate = LocalDateTime.now().format(DATETIME_FMT);
        PaymentTransaction tx = new PaymentTransaction(0, feeId, fee.getStudentId(), fee.getStudentName(), receiptNo, amount, method, transactionRef, payDate, actorName, notes != null ? notes : "");

        try (Connection conn = getConnection()) {
            // Update fee record
            try (PreparedStatement ps = conn.prepareStatement("UPDATE fee_records SET paid_amount = ?, balance = ?, status = ? WHERE id = ?")) {
                ps.setDouble(1, newPaid);
                ps.setDouble(2, newBalance);
                ps.setString(3, newStatus.name());
                ps.setInt(4, feeId);
                ps.executeUpdate();
            }

            // Insert transaction
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payment_transactions (fee_id, student_id, student_name, receipt_no, amount, payment_method, transaction_ref, payment_date, recorded_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, tx.getFeeId());
                ps.setInt(2, tx.getStudentId());
                ps.setString(3, tx.getStudentName());
                ps.setString(4, tx.getReceiptNo());
                ps.setDouble(5, tx.getAmount());
                ps.setString(6, tx.getPaymentMethod().name());
                ps.setString(7, tx.getTransactionRef());
                ps.setString(8, tx.getPaymentDate());
                ps.setString(9, tx.getRecordedBy());
                ps.setString(10, tx.getNotes());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) tx.setId(gk.getInt(1));
                }
            }

            logActivity(actorName, actorRole != null ? actorRole : "ACCOUNTING", "FEE_PAID", "Recorded payment of ₹" + amount + " for " + fee.getStudentName() + " (" + receiptNo + ")", receiptNo);
            AppEventBus.getInstance().publish(AppEvent.EventType.FEE_PAID, tx, "Fee payment of ₹" + amount + " received for " + fee.getStudentName());
            return tx;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PaymentTransaction> getPaymentTransactionsForStudent(int studentId) {
        List<PaymentTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE student_id = ? ORDER BY id DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPaymentTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private PaymentTransaction mapPaymentTransaction(ResultSet rs) throws SQLException {
        return new PaymentTransaction(
                rs.getInt("id"),
                rs.getInt("fee_id"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getString("receipt_no"),
                rs.getDouble("amount"),
                PaymentTransaction.PaymentMethod.valueOf(rs.getString("payment_method")),
                rs.getString("transaction_ref"),
                rs.getString("payment_date"),
                rs.getString("recorded_by"),
                rs.getString("notes")
        );
    }

    private FeeRecord mapFeeRecord(ResultSet rs) throws SQLException {
        return new FeeRecord(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getString("student_roll_no"),
                rs.getString("room_number"),
                rs.getDouble("total_fee"),
                rs.getDouble("paid_amount"),
                rs.getDouble("balance"),
                FeeRecord.FeeStatus.valueOf(rs.getString("status")),
                rs.getString("academic_term"),
                rs.getString("due_date")
        );
    }

    // ==========================================
    // SECONDARY MODULES GETTERS & ACTIONS
    // ==========================================
    public List<PaymentTransaction> getAllPaymentTransactions() {
        List<PaymentTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions ORDER BY id DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPaymentTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean allocateStudentToRoom(int studentId, int roomId, String actorName, String actorRole) {
        return allocateRoom(studentId, roomId, actorName, actorRole);
    }

    public List<Visitor> getAllVisitors() { return new ArrayList<>(visitors.values()); }
    public Visitor checkInVisitor(Visitor visitor, String actor) {
        int id = visitorIdGen.incrementAndGet();
        visitor.setId(id);
        visitor.setCheckInTime(LocalDateTime.now().format(DATETIME_FMT));
        visitor.setStatus(Visitor.VisitorStatus.INSIDE);
        visitors.put(id, visitor);
        logActivity(actor, "WARDEN", "VISITOR_CHECK_IN", "Visitor " + visitor.getVisitorName() + " checked in", visitor.getIdProofNumber());
        AppEventBus.getInstance().publish(AppEvent.EventType.VISITOR_CHECKED_IN, visitor, "Visitor checked in");
        return visitor;
    }
    public Visitor addVisitor(Visitor visitor, String actorName, String actorRole) {
        return checkInVisitor(visitor, actorName);
    }
    public boolean checkOutVisitor(int id, String actor) {
        Visitor v = visitors.get(id);
        if (v != null) {
            v.setCheckOutTime(LocalDateTime.now().format(DATETIME_FMT));
            v.setStatus(Visitor.VisitorStatus.CHECKED_OUT);
            logActivity(actor, "WARDEN", "VISITOR_CHECK_OUT", "Visitor " + v.getVisitorName() + " checked out", v.getIdProofNumber());
            AppEventBus.getInstance().publish(AppEvent.EventType.VISITOR_CHECKED_OUT, v, "Visitor checked out");
            return true;
        }
        return false;
    }
    public boolean checkOutVisitor(int id, String actorName, String actorRole) {
        return checkOutVisitor(id, actorName);
    }

    public List<LeaveRequest> getAllLeaveRequests() { return new ArrayList<>(leaveRequests.values()); }
    public List<LeaveRequest> getLeaveRequestsForStudent(int studentId) {
        return leaveRequests.values().stream().filter(l -> l.getStudentId() == studentId).collect(Collectors.toList());
    }
    public LeaveRequest submitLeaveRequest(LeaveRequest lr, String actorName, String actorRole) {
        int id = leaveIdGen.incrementAndGet();
        lr.setId(id);
        if (lr.getRequestId() == null || lr.getRequestId().isEmpty()) {
            lr.setRequestId("LR-2026-" + String.format("%03d", id));
        }
        lr.setAppliedDate(LocalDateTime.now().format(DATETIME_FMT));
        lr.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequests.put(id, lr);
        logActivity(actorName, actorRole, "LEAVE_SUBMITTED", "Submitted leave request", lr.getRequestId());
        AppEventBus.getInstance().publish(AppEvent.EventType.LEAVE_REQUESTED, lr, "Leave request submitted");
        return lr;
    }
    public LeaveRequest applyLeave(LeaveRequest req) {
        return submitLeaveRequest(req, req.getStudentName(), "STUDENT");
    }
    public boolean updateLeaveStatus(int id, LeaveRequest.LeaveStatus status, String remarks, String actorName, String actorRole) {
        LeaveRequest lr = leaveRequests.get(id);
        if (lr != null) {
            lr.setStatus(status);
            lr.setReviewedBy(actorName);
            lr.setReviewComments(remarks);
            lr.setReviewedDate(LocalDateTime.now().format(DATETIME_FMT));
            String action = status == LeaveRequest.LeaveStatus.APPROVED ? "LEAVE_APPROVED" : "LEAVE_REJECTED";
            logActivity(actorName, actorRole, action, "Reviewed leave request as " + status.name(), lr.getRequestId());
            AppEventBus.getInstance().publish(status == LeaveRequest.LeaveStatus.APPROVED ? AppEvent.EventType.LEAVE_APPROVED : AppEvent.EventType.LEAVE_REJECTED, lr, "Leave status updated");
            return true;
        }
        return false;
    }
    public boolean approveLeave(int id, String approvedBy, String remarks) {
        return updateLeaveStatus(id, LeaveRequest.LeaveStatus.APPROVED, remarks, approvedBy, "WARDEN");
    }
    public boolean rejectLeave(int id, String approvedBy, String remarks) {
        return updateLeaveStatus(id, LeaveRequest.LeaveStatus.REJECTED, remarks, approvedBy, "WARDEN");
    }

    public List<Complaint> getAllComplaints() { return new ArrayList<>(complaints.values()); }
    public List<Complaint> getComplaintsForStudent(int studentId) {
        return complaints.values().stream().filter(c -> c.getStudentId() == studentId).collect(Collectors.toList());
    }
    public Complaint submitComplaint(Complaint c, String actorName, String actorRole) {
        int id = complaintIdGen.incrementAndGet();
        c.setId(id);
        if (c.getComplaintId() == null || c.getComplaintId().isEmpty()) {
            c.setComplaintId("CMP-2026-" + String.format("%03d", id));
        }
        if (c.getFiledDate() == null || c.getFiledDate().isEmpty()) {
            c.setFiledDate(LocalDate.now().format(DATE_FMT));
        }
        complaints.put(id, c);
        logActivity(actorName, actorRole, "COMPLAINT_FILED", "Logged issue: " + c.getDescription(), c.getComplaintId());
        AppEventBus.getInstance().publish(AppEvent.EventType.COMPLAINT_FILED, c, "Complaint logged");
        return c;
    }
    public Complaint logComplaint(Complaint cmp) {
        return submitComplaint(cmp, cmp.getStudentName(), "STUDENT");
    }
    public boolean updateComplaintStatus(int id, Complaint.Status status, String assignedTo) {
        return updateComplaintStatus(id, status, assignedTo, null, "System", "ADMIN");
    }
    public boolean updateComplaintStatus(int id, Complaint.Status status, String assignedTo, String notes, String actorName, String actorRole) {
        Complaint c = complaints.get(id);
        if (c != null) {
            c.setStatus(status);
            if (assignedTo != null && !assignedTo.isEmpty()) c.setAssignedTo(assignedTo);
            if (notes != null && !notes.isEmpty()) c.setResolutionNotes(notes);
            if (status == Complaint.Status.RESOLVED) {
                c.setResolvedDate(LocalDate.now().format(DATE_FMT));
            }
            logActivity(actorName, actorRole, "COMPLAINT_UPDATED", "Updated status to " + status.name(), c.getComplaintId());
            AppEventBus.getInstance().publish(AppEvent.EventType.COMPLAINT_STATUS_CHANGED, c, "Complaint updated");
            return true;
        }
        return false;
    }

    public List<ComplianceRecord> getAllComplianceRecords() { return complianceRecords; }
    public boolean addComplianceRecord(ComplianceRecord cr, String actorName, String actorRole) {
        int id = complianceIdGen.incrementAndGet();
        cr.setId(id);
        complianceRecords.add(0, cr);
        logActivity(actorName, actorRole, "COMPLIANCE_RECORDED", "Recorded infraction for " + cr.getStudentName(), cr.getRuleViolated());
        AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, cr, "Compliance record added");
        return true;
    }

    public List<SustainabilityUsage> getSustainabilityHistory() { return sustainabilityUsages; }
    public List<SustainabilityUsage> getAllSustainabilityUsages() { return sustainabilityUsages; }
    public void addSustainabilityUsage(SustainabilityUsage u) { sustainabilityUsages.add(0, u); }
    public void addSustainabilityUsage(String ym, String lbl, double el, double wt, String remarks, String actorName, String actorRole) {
        int id = sustainabilityIdGen.incrementAndGet();
        double prevEl = 0;
        double prevWt = 0;
        if (!sustainabilityUsages.isEmpty()) {
            SustainabilityUsage last = sustainabilityUsages.get(0);
            prevEl = last.getElectricityKwh();
            prevWt = last.getWaterKiloLitres();
        }
        SustainabilityUsage u = new SustainabilityUsage(id, ym, lbl, el, wt, prevEl, prevWt, remarks, LocalDate.now().format(DATE_FMT), actorName);
        sustainabilityUsages.add(0, u);
        logActivity(actorName, actorRole, "SUSTAINABILITY_LOGGED", "Logged monthly resource usage: " + lbl, ym);
        AppEventBus.getInstance().publish(AppEvent.EventType.SUSTAINABILITY_LOGGED, u, "Sustainability data logged");
    }

    public List<Asset> getAllAssets() { return new ArrayList<>(assets.values()); }
    public boolean addAsset(Asset asset, String actorName, String actorRole) {
        int id = assetIdGen.incrementAndGet();
        asset.setId(id);
        assets.put(id, asset);
        logActivity(actorName, actorRole, "ASSET_ADDED", "Added inventory asset " + asset.getName(), asset.getAssetCode());
        AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, asset, "Asset added");
        return true;
    }
    public boolean flagAssetDamage(int id, Asset.Condition condition, String notes, String actorName, String actorRole) {
        Asset a = assets.get(id);
        if (a != null) {
            a.setCondition(condition);
            if (notes != null) a.setDamageNotes(notes);
            logActivity(actorName, actorRole, "ASSET_FLAGGED", "Asset flagged as " + condition.name(), a.getAssetCode());
            AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, a, "Asset status updated");
            return true;
        }
        return false;
    }

    public List<Notice> getAllNotices() { return notices; }
    public void addNotice(Notice n) { notices.add(0, n); }
    public void addNotice(Notice n, String actorName, String actorRole) {
        int id = noticeIdGen.incrementAndGet();
        n.setId(id);
        n.setPostedDate(LocalDateTime.now().format(DATETIME_FMT));
        notices.add(0, n);
        logActivity(actorName, actorRole, "NOTICE_POSTED", "Posted notice: " + n.getTitle(), String.valueOf(id));
        AppEventBus.getInstance().publish(AppEvent.EventType.NOTICE_POSTED, n, "Notice posted");
    }

    public List<MessMenu> getAllMessMenus() { return messMenus; }
    public List<MessMenu> getWeeklyMessMenu() { return messMenus; }
    public List<MessMenu> getMessMenuForDay(String day) {
        return messMenus.stream().filter(m -> m.getDayOfWeek().equalsIgnoreCase(day)).collect(Collectors.toList());
    }
    public List<MessFeedback> getAllMessFeedbacks() { return messFeedbacks; }
    public List<MessFeedback> getMessFeedbacks() { return messFeedbacks; }
    public void submitMessFeedback(MessFeedback f) { messFeedbacks.add(0, f); }
    public void submitMessFeedback(MessFeedback fb, String actorName, String actorRole) {
        int id = feedbackIdGen.incrementAndGet();
        fb.setId(id);
        fb.setDateSubmitted(LocalDateTime.now().format(DATETIME_FMT));
        messFeedbacks.add(0, fb);
        logActivity(actorName, actorRole, "MESS_FEEDBACK", "Submitted mess feedback: " + fb.getRating() + " stars", String.valueOf(id));
        AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, fb, "Mess feedback submitted");
    }
    public void updateMessMenuItem(int id, String items, String special, String timings) {
        for (MessMenu m : messMenus) {
            if (m.getId() == id) {
                if (items != null) m.setMenuItems(items);
                if (special != null) m.setSpecialItem(special);
                if (timings != null) m.setTimings(timings);
                AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, m, "Mess menu updated");
                break;
            }
        }
    }

    public List<Notification> getRecentNotifications() { return notifications; }
    public List<Notification> getNotificationsForUser(User user) { return notifications; }
    public int getUnreadNotificationCount(User user) {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }
    public void markNotificationAsRead(int id) {
        for (Notification n : notifications) {
            if (n.getId() == id) {
                n.setRead(true);
                break;
            }
        }
    }
    public void markAllNotificationsAsRead(User user) {
        for (Notification n : notifications) {
            n.setRead(true);
        }
        AppEventBus.getInstance().publish(AppEvent.EventType.DATA_CHANGED, user, "Notifications marked read");
    }

    public List<ActivityLog> getRecentActivities() { return activityLogs; }
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogs.stream().limit(limit).collect(Collectors.toList());
    }

    public void logActivity(String user, String role, String action, String desc, String ref) {
        int id = activityIdGen.incrementAndGet();
        activityLogs.add(0, new ActivityLog(id, LocalDateTime.now().format(DATETIME_FMT), user, role, action, desc, ref));
        if (activityLogs.size() > 50) activityLogs.remove(activityLogs.size() - 1);
    }
}
