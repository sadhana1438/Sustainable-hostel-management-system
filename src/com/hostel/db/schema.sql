-- ==========================================================
-- SMART SUSTAINABLE HOSTEL MANAGEMENT SYSTEM
-- MySQL Relational Database Schema (DDL)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS hostel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hostel_db;

-- 1. Users & Authentication
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'WARDEN', 'STUDENT') NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    student_id VARCHAR(30),
    room_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Rooms & Blocks
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    block VARCHAR(50) NOT NULL,
    floor INT NOT NULL,
    capacity INT NOT NULL DEFAULT 2,
    occupied INT NOT NULL DEFAULT 0,
    monthly_rent DECIMAL(10, 2) NOT NULL DEFAULT 4500.00,
    room_type VARCHAR(50) NOT NULL,
    status ENUM('AVAILABLE', 'PARTIAL', 'FULL', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    amenities TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Students
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year_level VARCHAR(20) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    parent_contact VARCHAR(20) NOT NULL,
    emergency_contact VARCHAR(20),
    blood_group VARCHAR(10),
    room_id INT,
    room_number VARCHAR(20),
    block VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'ON_LEAVE') NOT NULL DEFAULT 'ACTIVE',
    admission_date DATE NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
);

-- 4. Fee Structures & Student Dues
CREATE TABLE IF NOT EXISTS fee_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    student_roll_no VARCHAR(30) NOT NULL,
    room_number VARCHAR(20),
    academic_term VARCHAR(50) NOT NULL,
    hostel_fee DECIMAL(10, 2) NOT NULL DEFAULT 35000.00,
    mess_fee DECIMAL(10, 2) NOT NULL DEFAULT 22000.00,
    utility_deposit DECIMAL(10, 2) NOT NULL DEFAULT 5000.00,
    total_fee DECIMAL(10, 2) NOT NULL DEFAULT 62000.00,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 62000.00,
    due_date DATE NOT NULL,
    status ENUM('PAID', 'PARTIAL', 'PENDING', 'OVERDUE') NOT NULL DEFAULT 'PENDING',
    last_payment_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 5. Payment Transactions
CREATE TABLE IF NOT EXISTS payment_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fee_id INT NOT NULL,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    receipt_no VARCHAR(40) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_ref VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100) NOT NULL,
    notes TEXT,
    FOREIGN KEY (fee_id) REFERENCES fee_records(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 6. Visitors & Gate Entry
CREATE TABLE IF NOT EXISTS visitors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    visitor_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    relation VARCHAR(50) NOT NULL,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    room_number VARCHAR(20),
    purpose TEXT NOT NULL,
    visit_date DATE NOT NULL,
    check_in_time VARCHAR(10) NOT NULL,
    check_out_time VARCHAR(10),
    id_proof_type VARCHAR(50),
    id_proof_number VARCHAR(50),
    status ENUM('INSIDE', 'CHECKED_OUT', 'REJECTED') NOT NULL DEFAULT 'INSIDE',
    security_officer VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 7. Leave Requests & Gate Passes
CREATE TABLE IF NOT EXISTS leave_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(30) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    student_roll_no VARCHAR(30) NOT NULL,
    room_number VARCHAR(20),
    leave_type VARCHAR(50) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    reason TEXT NOT NULL,
    destination_address TEXT,
    parent_consent_phone VARCHAR(20),
    parent_acknowledged BOOLEAN DEFAULT FALSE,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    applied_date DATE NOT NULL,
    reviewed_by VARCHAR(100),
    review_comments TEXT,
    reviewed_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 8. Complaints & Maintenance Tickets
CREATE TABLE IF NOT EXISTS complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(30) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    room_number VARCHAR(20),
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) DEFAULT 'Medium',
    filed_date DATE NOT NULL,
    assigned_to VARCHAR(100),
    status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'PENDING',
    resolution_notes TEXT,
    resolved_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 9. Compliance & Discipline Records
CREATE TABLE IF NOT EXISTS compliance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    student_roll_no VARCHAR(30) NOT NULL,
    room_number VARCHAR(20),
    rule_violated VARCHAR(100) NOT NULL,
    violation_date DATE NOT NULL,
    description TEXT NOT NULL,
    action_taken VARCHAR(100) NOT NULL,
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    recorded_by VARCHAR(100) NOT NULL,
    fine_paid BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 10. Electricity & Water Manual Sustainability Tracking
CREATE TABLE IF NOT EXISTS sustainability_usage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    year_month VARCHAR(10) NOT NULL UNIQUE, -- 'YYYY-MM'
    month_label VARCHAR(20) NOT NULL,        -- 'Aug 2024'
    electricity_kwh DECIMAL(10, 2) NOT NULL, -- kWh units
    water_kilolitres DECIMAL(10, 2) NOT NULL,-- kL (1000 Litres)
    prev_month_electricity DECIMAL(10, 2) DEFAULT 0,
    prev_month_water DECIMAL(10, 2) DEFAULT 0,
    electricity_change_pct DECIMAL(6, 2) DEFAULT 0,
    water_change_pct DECIMAL(6, 2) DEFAULT 0,
    electricity_anomaly BOOLEAN DEFAULT FALSE,
    water_anomaly BOOLEAN DEFAULT FALSE,
    remarks TEXT,
    recorded_date DATE NOT NULL,
    recorded_by VARCHAR(100) NOT NULL
);

-- 11. Inventory & Asset Management
CREATE TABLE IF NOT EXISTS assets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asset_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    location VARCHAR(100) NOT NULL,
    condition_status ENUM('GOOD', 'DAMAGED', 'UNDER_REPAIR', 'MISSING') NOT NULL DEFAULT 'GOOD',
    status VARCHAR(50) DEFAULT 'In Use',
    estimated_value DECIMAL(10, 2) DEFAULT 0.00,
    last_inspected_date DATE,
    damage_notes TEXT
);

-- 12. Notices & Announcements
CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority ENUM('NORMAL', 'HIGH', 'URGENT') NOT NULL DEFAULT 'NORMAL',
    posted_by VARCHAR(100) NOT NULL,
    posted_role VARCHAR(50) NOT NULL,
    posted_date DATE NOT NULL,
    target_audience VARCHAR(50) DEFAULT 'ALL',
    is_published BOOLEAN DEFAULT TRUE
);

-- 13. Mess Menu & Meal Timings
CREATE TABLE IF NOT EXISTS mess_menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL,
    meal_type VARCHAR(30) NOT NULL,
    menu_items TEXT NOT NULL,
    special_item VARCHAR(100),
    timings VARCHAR(50),
    dietary_type VARCHAR(50) DEFAULT 'Pure Veg'
);

-- 14. Mess Student Feedback & Food Complaints
CREATE TABLE IF NOT EXISTS mess_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    room_number VARCHAR(20),
    meal_type VARCHAR(30) NOT NULL,
    rating INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    comments TEXT,
    is_food_complaint BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'Received',
    date_submitted DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 15. Notification Center
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    target_user_id INT DEFAULT 0,
    target_role VARCHAR(20) DEFAULT 'ALL',
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(30) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    action_link VARCHAR(50)
);

-- 16. Audit Log & Activity Feed
CREATE TABLE IF NOT EXISTS activity_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    actor_name VARCHAR(100) NOT NULL,
    actor_role VARCHAR(30) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    entity_id VARCHAR(50)
);
