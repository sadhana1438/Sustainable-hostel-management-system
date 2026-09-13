-- ==========================================================
-- SMART SUSTAINABLE HOSTEL MANAGEMENT SYSTEM
-- Seed Initial Data
-- ==========================================================

USE hostel_db;

-- 1. Users
INSERT INTO users (username, password_hash, full_name, role, email, phone, student_id, room_number) VALUES
('admin', 'admin123', 'Dr. Sarah Jenkins', 'ADMIN', 'admin@hostel.edu', '+91 98450 11223', NULL, NULL),
('warden', 'warden123', 'Prof. Marcus Vance', 'WARDEN', 'warden@hostel.edu', '+91 98450 33445', NULL, NULL),
('student', 'student123', 'Aarav Sharma', 'STUDENT', 'aarav.sharma@college.edu', '+91 98765 43210', 'STU-2024-001', 'A-204'),
('student2', 'student123', 'Rohan Iyer', 'STUDENT', 'rohan.iyer@college.edu', '+91 98765 43211', 'STU-2024-002', 'A-204'),
('student3', 'student123', 'Priya Menon', 'STUDENT', 'priya.menon@college.edu', '+91 98765 43212', 'STU-2024-003', 'B-102');

-- 2. Rooms
INSERT INTO rooms (room_number, block, floor, capacity, occupied, monthly_rent, room_type, status, amenities) VALUES
('A-101', 'A Block (North Wing)', 1, 2, 2, 4500.00, 'Non-AC 2-Sharing', 'FULL', '2 Study Desks, Attached Bath, Balcony'),
('A-102', 'A Block (North Wing)', 1, 2, 1, 4500.00, 'Non-AC 2-Sharing', 'PARTIAL', '2 Study Desks, Attached Bath'),
('A-103', 'A Block (North Wing)', 1, 2, 0, 4500.00, 'Non-AC 2-Sharing', 'AVAILABLE', '2 Study Desks, Attached Bath'),
('A-204', 'A Block (North Wing)', 2, 3, 2, 5500.00, 'AC 3-Sharing', 'PARTIAL', '3 Study Desks, Air Conditioner, Attached Bath, Wi-Fi Extender'),
('A-205', 'A Block (North Wing)', 2, 3, 3, 5500.00, 'AC 3-Sharing', 'FULL', '3 Study Desks, Air Conditioner, Attached Bath'),
('B-101', 'B Block (South Wing)', 1, 2, 1, 4200.00, 'Non-AC 2-Sharing', 'PARTIAL', '2 Desks, Wardrobes, Shared Bath'),
('B-102', 'B Block (South Wing)', 1, 1, 1, 7500.00, 'Single Deluxe', 'FULL', 'Single Bed, Study Station, AC, Private Bath, Mini Fridge'),
('B-103', 'B Block (South Wing)', 1, 1, 0, 7500.00, 'Single Deluxe', 'AVAILABLE', 'Single Bed, Study Station, AC, Private Bath'),
('C-301', 'C Block (Green Wing)', 3, 4, 4, 3800.00, 'Economy 4-Sharing', 'FULL', '4 Bunk Desks, Natural Ventilation, Balcony'),
('C-302', 'C Block (Green Wing)', 3, 4, 2, 3800.00, 'Economy 4-Sharing', 'PARTIAL', '4 Bunk Desks, Natural Ventilation'),
('C-303', 'C Block (Green Wing)', 3, 4, 0, 3800.00, 'Economy 4-Sharing', 'AVAILABLE', '4 Bunk Desks, Natural Ventilation');

-- 3. Students
INSERT INTO students (student_id, name, department, year_level, contact, email, parent_contact, emergency_contact, blood_group, room_id, room_number, block, status, admission_date) VALUES
('STU-2024-001', 'Aarav Sharma', 'Computer Science', '3rd Year', '+91 98765 43210', 'aarav.sharma@college.edu', '+91 94432 12345', '+91 94432 12346', 'O+', 4, 'A-204', 'A Block (North Wing)', 'ACTIVE', '2023-08-01'),
('STU-2024-002', 'Rohan Iyer', 'Electronics & Comm', '3rd Year', '+91 98765 43211', 'rohan.iyer@college.edu', '+91 94432 23456', '+91 94432 23457', 'B+', 4, 'A-204', 'A Block (North Wing)', 'ACTIVE', '2023-08-01'),
('STU-2024-003', 'Priya Menon', 'Information Technology', '2nd Year', '+91 98765 43212', 'priya.menon@college.edu', '+91 94432 34567', '+91 94432 34568', 'A+', 7, 'B-102', 'B Block (South Wing)', 'ACTIVE', '2024-08-01'),
('STU-2024-004', 'Kavya Nair', 'Mechanical Engg', '4th Year', '+91 98765 43213', 'kavya.nair@college.edu', '+91 94432 45678', '+91 94432 45679', 'AB+', 1, 'A-101', 'A Block (North Wing)', 'ACTIVE', '2022-08-01'),
('STU-2024-005', 'Vikramaditya Rao', 'Civil Engg', '1st Year', '+91 98765 43214', 'vikram.rao@college.edu', '+91 94432 56789', '+91 94432 56780', 'B-', 2, 'A-102', 'A Block (North Wing)', 'ACTIVE', '2025-08-01'),
('STU-2024-006', 'Siddharth Varma', 'Computer Science', '2nd Year', '+91 98765 43215', 'sid.varma@college.edu', '+91 94432 67890', '+91 94432 67891', 'O-', 9, 'C-301', 'C Block (Green Wing)', 'ON_LEAVE', '2024-08-01');

-- 4. Fee Records
INSERT INTO fee_records (student_id, student_name, student_roll_no, room_number, academic_term, hostel_fee, mess_fee, utility_deposit, total_fee, paid_amount, balance, due_date, status, last_payment_date) VALUES
(1, 'Aarav Sharma', 'STU-2024-001', 'A-204', 'Academic Year 2024-25', 38500.00, 24000.00, 5000.00, 67500.00, 67500.00, 0.00, '2024-09-15', 'PAID', '2024-08-20'),
(2, 'Rohan Iyer', 'STU-2024-002', 'A-204', 'Academic Year 2024-25', 38500.00, 24000.00, 5000.00, 67500.00, 40000.00, 27500.00, '2024-09-15', 'PARTIAL', '2024-08-22'),
(3, 'Priya Menon', 'STU-2024-003', 'B-102', 'Academic Year 2024-25', 52500.00, 24000.00, 6000.00, 82500.00, 82500.00, 0.00, '2024-09-15', 'PAID', '2024-08-15'),
(4, 'Kavya Nair', 'STU-2024-004', 'A-101', 'Academic Year 2024-25', 31500.00, 24000.00, 5000.00, 60500.00, 0.00, 60500.00, '2024-09-15', 'PENDING', NULL),
(5, 'Vikramaditya Rao', 'STU-2024-005', 'A-102', 'Academic Year 2024-25', 31500.00, 24000.00, 5000.00, 60500.00, 30000.00, 30500.00, '2024-09-15', 'PARTIAL', '2024-08-28'),
(6, 'Siddharth Varma', 'STU-2024-006', 'C-301', 'Academic Year 2024-25', 26600.00, 24000.00, 4000.00, 54600.00, 0.00, 54600.00, '2024-08-30', 'OVERDUE', NULL);

-- 5. Payment Transactions
INSERT INTO payment_transactions (fee_id, student_id, student_name, receipt_no, amount, payment_method, transaction_ref, payment_date, recorded_by, notes) VALUES
(1, 1, 'Aarav Sharma', 'RCP-2024-0891', 67500.00, 'ONLINE_UPI', 'UPI/240820/998124', '2024-08-20 11:30:00', 'Admin Office', 'Full annual payment with deposit'),
(2, 2, 'Rohan Iyer', 'RCP-2024-0895', 40000.00, 'NET_BANKING', 'HDFC982173618', '2024-08-22 14:15:00', 'Admin Office', 'Installment 1 of 2'),
(3, 3, 'Priya Menon', 'RCP-2024-0870', 82500.00, 'DEBIT_CREDIT_CARD', 'POS-CARD-449102', '2024-08-15 10:05:00', 'Admin Office', 'Single Deluxe annual full settlement'),
(5, 5, 'Vikramaditya Rao', 'RCP-2024-0912', 30000.00, 'BANK_CHALLAN', 'SBI-CHL-10029', '2024-08-28 16:45:00', 'Admin Office', 'First installment');

-- 6. Visitors
INSERT INTO visitors (visitor_name, phone, relation, student_id, student_name, room_number, purpose, visit_date, check_in_time, check_out_time, id_proof_type, id_proof_number, status, security_officer) VALUES
('Sunil Sharma', '+91 94432 12345', 'Parent (Father)', 1, 'Aarav Sharma', 'A-204', 'Delivering academic project materials & books', '2024-09-07', '10:30 AM', NULL, 'National ID', 'ABCD-9821-44', 'INSIDE', 'Officer Balraj'),
('Deepa Iyer', '+91 94432 23456', 'Parent (Mother)', 2, 'Rohan Iyer', 'A-204', 'Weekend personal visit & groceries', '2024-09-07', '11:15 AM', NULL, 'Driver License', 'DL-2018-00912', 'INSIDE', 'Officer Balraj'),
('Ramesh Menon', '+91 94432 34567', 'Parent (Father)', 3, 'Priya Menon', 'B-102', 'Medical checkup pickup', '2024-09-07', '09:00 AM', '11:45 AM', 'National ID', 'ABCD-1122-33', 'CHECKED_OUT', 'Officer Balraj'),
('Ananya Sen', '+91 98840 99881', 'Academic Peer', 4, 'Kavya Nair', 'A-101', 'Final year group study discussion', '2024-09-06', '04:00 PM', '06:30 PM', 'College ID', 'CLG-2024-SEN', 'CHECKED_OUT', 'Officer Sundar');

-- 7. Leave Requests
INSERT INTO leave_requests (request_id, student_id, student_name, student_roll_no, room_number, leave_type, from_date, to_date, reason, destination_address, parent_consent_phone, parent_acknowledged, status, applied_date, reviewed_by, review_comments, reviewed_date) VALUES
('LR-2024-101', 1, 'Aarav Sharma', 'STU-2024-001', 'A-204', 'HOME_VISIT', '2024-09-13', '2024-09-16', 'Family festive celebration over extended weekend', 'Plot 42, Green Park, Bangalore', '+91 94432 12345', 1, 'APPROVED', '2024-09-05', 'Prof. Marcus Vance (Warden)', 'Parent consent verified via phone call.', '2024-09-06'),
('LR-2024-102', 2, 'Rohan Iyer', 'STU-2024-002', 'A-204', 'ACADEMIC_EVENT', '2024-09-18', '2024-09-21', 'Representing College in National Hackathon at IIT Madras', 'IIT Madras Campus, Chennai', '+91 94432 23456', 1, 'PENDING', '2024-09-07', NULL, NULL, NULL),
('LR-2024-103', 4, 'Kavya Nair', 'STU-2024-004', 'A-101', 'MEDICAL_LEAVE', '2024-09-09', '2024-09-11', 'Scheduled dental root canal surgery', '34 MG Road, Kochi', '+91 94432 45678', 1, 'PENDING', '2024-09-07', NULL, NULL, NULL),
('LR-2024-104', 6, 'Siddharth Varma', 'STU-2024-006', 'C-301', 'HOME_VISIT', '2024-09-01', '2024-09-08', 'Attending sister wedding ceremony', 'Hyderabad', '+91 94432 67890', 1, 'APPROVED', '2024-08-28', 'Prof. Marcus Vance (Warden)', 'Approved for 7 days.', '2024-08-29');

-- 8. Complaints & Maintenance
INSERT INTO complaints (complaint_id, student_id, student_name, room_number, category, description, priority, filed_date, assigned_to, status, resolution_notes, resolved_date) VALUES
('CMP-2024-041', 1, 'Aarav Sharma', 'A-204', 'PLUMBING', 'Washroom sink tap has a persistent leak causing water wastage.', 'High', '2024-09-05', 'Ramesh (Plumber)', 'RESOLVED', 'Replaced worn rubber washer and cartridge. Leak stopped completely.', '2024-09-06'),
('CMP-2024-042', 2, 'Rohan Iyer', 'A-204', 'ELECTRICAL', 'Ceiling fan makes humming noise at speed 3 and above.', 'Medium', '2024-09-06', 'Kishore (Electrician)', 'IN_PROGRESS', 'Capacitor tested, replacing bearing assembly today.', NULL),
('CMP-2024-043', 3, 'Priya Menon', 'B-102', 'INTERNET_WIFI', 'Wi-Fi access point in 1st floor corridor drops connection frequently.', 'High', '2024-09-07', 'IT Support Desk', 'PENDING', NULL, NULL),
('CMP-2024-044', 5, 'Vikramaditya Rao', 'A-102', 'FURNITURE', 'Study desk drawer lock is jammed.', 'Low', '2024-09-07', 'Unassigned', 'PENDING', NULL, NULL);

-- 9. Compliance
INSERT INTO compliance_records (student_id, student_name, student_roll_no, room_number, rule_violated, violation_date, description, action_taken, fine_amount, recorded_by, fine_paid) VALUES
(2, 'Rohan Iyer', 'STU-2024-002', 'A-204', 'Unauthorized Electrical Heating Coil', '2024-08-25', 'High-wattage immersion coil discovered during routine safety audit in violation of safety rules.', 'Item confiscated, formal warning notice issued.', 500.00, 'Prof. Marcus Vance (Warden)', 1),
(5, 'Vikramaditya Rao', 'STU-2024-005', 'A-102', 'Late Entry Post 10:00 PM Curfew', '2024-08-30', 'Returned to hostel gate at 11:20 PM without prior gate pass.', 'Counseling and entry recorded in gate register.', 200.00, 'Prof. Marcus Vance (Warden)', 0);

-- 10. Electricity & Water Manual Sustainability Data
-- Note: Anomaly jump (>15%) in May 2024 (heatwave AC surge) and July 2024 (water tank overflow issue)
INSERT INTO sustainability_usage (year_month, month_label, electricity_kwh, water_kilolitres, prev_month_electricity, prev_month_water, electricity_change_pct, water_change_pct, electricity_anomaly, water_anomaly, remarks, recorded_date, recorded_by) VALUES
('2024-03', 'Mar 2024', 12400.00, 310.00, 12000.00, 300.00, 3.33, 3.33, 0, 0, 'Normal seasonal consumption.', '2024-03-31', 'Hostel Admin'),
('2024-04', 'Apr 2024', 13200.00, 335.00, 12400.00, 310.00, 6.45, 8.06, 0, 0, 'Summer onset; moderate rise in fan/cooler usage.', '2024-04-30', 'Hostel Admin'),
('2024-05', 'May 2024', 16100.00, 360.00, 13200.00, 335.00, 21.97, 7.46, 1, 0, 'Unusual surge (+22%) due to peak summer heatwave & exam preparation.', '2024-05-31', 'Hostel Admin'),
('2024-06', 'Jun 2024', 11500.00, 280.00, 16100.00, 360.00, -28.57, -22.22, 0, 0, 'Semester break; reduced student occupancy in blocks.', '2024-06-30', 'Hostel Admin'),
('2024-07', 'Jul 2024', 13800.00, 345.00, 11500.00, 280.00, 20.00, 23.21, 1, 1, 'Reopening surge & overhead tank float valve overflow glitch (repaired).', '2024-07-31', 'Hostel Admin'),
('2024-08', 'Aug 2024', 14100.00, 325.00, 13800.00, 345.00, 2.17, -5.80, 0, 0, 'Stabilized usage after LED retrofitting in corridor blocks.', '2024-08-31', 'Hostel Admin');

-- 11. Assets
INSERT INTO assets (asset_code, name, category, quantity, location, condition_status, status, estimated_value, last_inspected_date, damage_notes) VALUES
('AST-FUR-001', 'Wooden Study Table & Bookshelf Set', 'Furniture', 120, 'Blocks A, B, C Resident Rooms', 'GOOD', 'In Use', 480000.00, '2024-08-15', 'Regular maintenance completed'),
('AST-FUR-002', 'Dining Hall Stainless Steel Table-Bench Sets', 'Furniture', 24, 'Main Dining Hall', 'GOOD', 'In Use', 192000.00, '2024-08-20', 'Good condition'),
('AST-ELE-001', 'Commercial Water Purifier (RO + UV, 100 LPH)', 'Plumbing', 4, 'Ground Floor Pantries', 'GOOD', 'In Use', 160000.00, '2024-09-01', 'Filters replaced on Sept 1'),
('AST-ELE-002', 'Daikin 1.5 Ton Split AC Unit', 'Electrical', 30, 'A Block AC Rooms & Warden Office', 'UNDER_REPAIR', 'In Use', 1050000.00, '2024-09-03', 'Unit in Room A-202 scheduled for gas refill'),
('AST-GYM-001', 'Multi-Station Gym Workout Rig', 'Sports & Gym', 1, 'Hostel Clubhouse Gym', 'GOOD', 'In Use', 85000.00, '2024-08-10', 'Cables lubricated'),
('AST-ELE-003', 'Solar Water Heater 500 LPD Panel System', 'Electrical', 2, 'Rooftop Block A & B', 'GOOD', 'In Use', 240000.00, '2024-08-25', 'Working efficiently');

-- 12. Notices
INSERT INTO notices (title, content, category, priority, posted_by, posted_role, posted_date, target_audience, is_published) VALUES
('Hostel Curfew & Safety Timings Reminder', 'All residents are reminded that the main hostel gates close strictly at 10:00 PM. Late entries without prior approved warden gate pass will incur compliance fines.', 'Curfew / Security', 'HIGH', 'Prof. Marcus Vance', 'WARDEN', '2024-09-06', 'STUDENTS', 1),
('Water Conservation Drive & Tap Inspections', 'As part of our Green Hostel Initiative, routine plumbing inspections will occur this Saturday. Please report any dripping taps via the Complaints module.', 'Maintenance', 'NORMAL', 'Dr. Sarah Jenkins', 'ADMIN', '2024-09-05', 'ALL', 1),
('Special Feast Menu - Ganesh Chaturthi', 'A special traditional festive lunch will be served this Sunday. Mess timings: 12:00 PM to 03:00 PM. Vegetarian festive delicacies included.', 'Mess', 'NORMAL', 'Mess Committee', 'WARDEN', '2024-09-04', 'ALL', 1),
('Semester Hostel Fee Settlement Notice', 'Students with pending fee balances are advised to clear dues before September 15, 2024 to avoid late administrative surcharges.', 'Hostel Dues', 'URGENT', 'Admin Finance Office', 'ADMIN', '2024-09-01', 'STUDENTS', 1);

-- 13. Mess Menu (Sample Days)
INSERT INTO mess_menu (day_of_week, meal_type, menu_items, special_item, timings, dietary_type) VALUES
('Monday', 'BREAKFAST', 'Idli, Medu Vada, Sambar, Coconut Chutney, Bread & Jam, Tea/Coffee', 'Medu Vada', '07:30 AM - 09:15 AM', 'Pure Veg'),
('Monday', 'LUNCH', 'Steamed Rice, Dal Tadka, Paneer Butter Masala, Chapati, Curd, Salad', 'Paneer Butter Masala', '12:30 PM - 02:30 PM', 'Pure Veg'),
('Monday', 'SNACKS', 'Veg Samosa, Mint Chutney, Ginger Tea / Filter Coffee', 'Hot Samosa', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Monday', 'DINNER', 'Jeera Rice, Mixed Veg Curry, Phulka, Rasam, Ice Cream', 'Vanilla Ice Cream', '07:45 PM - 09:45 PM', 'Pure Veg'),
('Tuesday', 'BREAKFAST', 'Poha, Boiled Eggs / Sprouted Moong, Banana, Tea/Coffee', 'Sprouted Salad', '07:30 AM - 09:15 AM', 'Veg & Non-Veg'),
('Tuesday', 'LUNCH', 'Vegetable Biryani / Chicken Curry, Raita, Mirchi Ka Salan, Gulab Jamun', 'Chicken Curry / Paneer Biryani', '12:30 PM - 02:30 PM', 'Veg & Non-Veg'),
('Tuesday', 'SNACKS', 'Onion Pakoda, Masala Chai, Biscuits', 'Crispy Pakoda', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Tuesday', 'DINNER', 'South Indian Meals: Rice, Sambar, Poriyal, Appalam, Buttermilk', 'Carrot Halwa', '07:45 PM - 09:45 PM', 'Pure Veg'),
('Wednesday', 'BREAKFAST', 'Aloo Paratha, Curd, Pickle, Boiled Eggs, Tea/Coffee', 'Stuffed Aloo Paratha', '07:30 AM - 09:15 AM', 'Veg & Non-Veg'),
('Wednesday', 'LUNCH', 'Rajma Chawal, Roti, Aloo Gobi Dry, Fresh Curd, Papad', 'Punjabi Rajma', '12:30 PM - 02:30 PM', 'Pure Veg'),
('Wednesday', 'SNACKS', 'Bhel Puri, Sweet & Green Chutney, Cardamom Tea', 'Mumbai Bhel', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Wednesday', 'DINNER', 'Fried Rice, Veg Manchurian / Chilli Chicken, Sweet Corn Soup', 'Chilli Chicken / Veg Manchurian', '07:45 PM - 09:45 PM', 'Veg & Non-Veg'),
('Thursday', 'BREAKFAST', 'Masala Dosa, Tomato Chutney, Sambar, Boiled Eggs, Tea/Coffee', 'Crispy Masala Dosa', '07:30 AM - 09:15 AM', 'Veg & Non-Veg'),
('Thursday', 'LUNCH', 'Rice, Lemon Rasam, Bhindi Masala, Chapati, Curd, Pickle', 'Lemon Rice & Curd', '12:30 PM - 02:30 PM', 'Pure Veg'),
('Thursday', 'SNACKS', 'Corn Chat / French Fries, Hot Coffee / Tea', 'Crispy French Fries', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Thursday', 'DINNER', 'Khichdi, Kadhi, Batata Vada, Papad, Fruit Custard', 'Fruit Custard', '07:45 PM - 09:45 PM', 'Pure Veg'),
('Friday', 'BREAKFAST', 'Poori Bhaji, Semolina Kesari, Tea/Coffee', 'Pineapple Kesari', '07:30 AM - 09:15 AM', 'Pure Veg'),
('Friday', 'LUNCH', 'Ghee Rice, Dal Makhani, Paneer Tikka Masala / Egg Curry, Naan, Salad', 'Dal Makhani & Naan', '12:30 PM - 02:30 PM', 'Veg & Non-Veg'),
('Friday', 'SNACKS', 'Veg Cutlet, Tomato Sauce, Ginger Tea', 'Veg Cutlet', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Friday', 'DINNER', 'Chapati, Malai Kofta, Veg Pulao, Curd, Rasgulla', 'Kolkata Rasgulla', '07:45 PM - 09:45 PM', 'Pure Veg'),
('Saturday', 'BREAKFAST', 'Rava Idli, Saagu, Chutney, Tea/Coffee', 'Rava Idli', '07:30 AM - 09:15 AM', 'Pure Veg'),
('Saturday', 'LUNCH', 'Sambhar Rice, Curd Rice, Potato Fry, Pickle, Fryums', 'South Special Combo', '12:30 PM - 02:30 PM', 'Pure Veg'),
('Saturday', 'SNACKS', 'Bread Pakoda, Mint Chutney, Hot Tea', 'Bread Pakoda', '05:00 PM - 06:15 PM', 'Pure Veg'),
('Saturday', 'DINNER', 'Veg Hakka Noodles, Spring Rolls, Hot & Sour Soup, Pastry', 'Chocolate Pastry', '07:45 PM - 09:45 PM', 'Pure Veg'),
('Sunday', 'BREAKFAST', 'Chole Bhature, Sweet Lassi, Tea/Coffee', 'Deluxe Chole Bhature', '08:00 AM - 10:00 AM', 'Pure Veg'),
('Sunday', 'LUNCH', 'Hyderabadi Dum Biryani (Chicken/Mutton/Veg), Mirchi Salan, Raita, Double Ka Meetha', 'Special Feast Biryani', '12:00 PM - 03:00 PM', 'Veg & Non-Veg'),
('Sunday', 'SNACKS', 'Biscuits & Filter Coffee', 'Coffee Time', '05:00 PM - 06:00 PM', 'Pure Veg'),
('Sunday', 'DINNER', 'Light Khichdi, Toast Bread, Boiled Milk, Seasonal Fruits', 'Health & Detox Dinner', '07:45 PM - 09:30 PM', 'Pure Veg');

-- 14. Mess Feedback
INSERT INTO mess_feedback (student_id, student_name, room_number, meal_type, rating, category, comments, is_food_complaint, status, date_submitted) VALUES
(1, 'Aarav Sharma', 'A-204', 'Lunch', 5, 'Taste & Quality', 'Sunday Biryani was exceptional. Great quality!', 0, 'Reviewed', '2024-09-01'),
(2, 'Rohan Iyer', 'A-204', 'Breakfast', 3, 'Portion Size', 'Sambar was slightly cold on Thursday morning.', 1, 'Action Taken', '2024-09-05'),
(3, 'Priya Menon', 'B-102', 'Dinner', 4, 'Hygiene & Cleanliness', 'Dining hall tables are kept very clean. Appreciate the staff.', 0, 'Reviewed', '2024-09-06');

-- 15. Notifications
INSERT INTO notifications (target_user_id, target_role, title, message, type, timestamp, is_read, action_link) VALUES
(1, 'STUDENT', 'Leave Request Approved', 'Your leave request LR-2024-101 for Sep 13 - Sep 16 has been approved by Warden.', 'LEAVE', '2024-09-06 10:30:00', 0, 'MODULE_LEAVE'),
(1, 'STUDENT', 'Fee Payment Acknowledged', 'Payment of Rs. 67,500.00 received. Receipt #RCP-2024-0891 generated.', 'FEE', '2024-08-20 11:30:00', 1, 'MODULE_FEES'),
(0, 'WARDEN', 'New Leave Application', 'Student Rohan Iyer (STU-2024-002) submitted leave request LR-2024-102 for Hackathon.', 'LEAVE', '2024-09-07 08:45:00', 0, 'MODULE_LEAVE'),
(0, 'WARDEN', 'New Maintenance Ticket', 'Priya Menon filed Wi-Fi connectivity complaint CMP-2024-043 (Block B).', 'COMPLAINT', '2024-09-07 09:15:00', 0, 'MODULE_COMPLIANCE'),
(0, 'ADMIN', 'Sustainability Alert', 'Electricity usage spiked +20% in July 2024. Review cooling & pump logs.', 'SUSTAINABILITY', '2024-08-01 09:00:00', 0, 'MODULE_SUSTAINABILITY'),
(0, 'ADMIN', 'Visitor Check-In', 'Visitor Sunil Sharma checked in to meet Aarav Sharma (Room A-204).', 'VISITOR', '2024-09-07 10:30:00', 0, 'MODULE_VISITOR');

-- 16. Activity Logs
INSERT INTO activity_logs (timestamp, actor_name, actor_role, action_type, description, entity_id) VALUES
('2024-09-07 10:30:00', 'Officer Balraj', 'WARDEN', 'VISITOR_CHECKED_IN', 'Visitor Sunil Sharma checked in for Aarav Sharma (Room A-204)', 'VIS-001'),
('2024-09-07 09:15:00', 'Priya Menon', 'STUDENT', 'COMPLAINT_FILED', 'Submitted Wi-Fi connectivity ticket CMP-2024-043', 'CMP-2024-043'),
('2024-09-07 08:45:00', 'Rohan Iyer', 'STUDENT', 'LEAVE_SUBMITTED', 'Submitted academic leave request LR-2024-102 for IIT Madras Hackathon', 'LR-2024-102'),
('2024-09-06 14:20:00', 'Prof. Marcus Vance', 'WARDEN', 'LEAVE_APPROVED', 'Approved leave request LR-2024-101 for Aarav Sharma', 'LR-2024-101'),
('2024-09-06 11:00:00', 'Ramesh (Plumber)', 'ADMIN', 'COMPLAINT_RESOLVED', 'Resolved plumbing tap leak ticket CMP-2024-041 in Room A-204', 'CMP-2024-041'),
('2024-09-05 16:30:00', 'Admin Office', 'ADMIN', 'ROOM_ALLOCATED', 'Allocated Room A-204 Bed 2 to Rohan Iyer', 'A-204'),
('2024-08-28 16:45:00', 'Admin Finance', 'ADMIN', 'PAYMENT_RECORDED', 'Recorded installment payment of Rs. 30,000 for Vikramaditya Rao', 'RCP-2024-0912'),
('2024-08-20 11:30:00', 'Admin Finance', 'ADMIN', 'PAYMENT_RECORDED', 'Recorded full payment of Rs. 67,500 for Aarav Sharma', 'RCP-2024-0891');
