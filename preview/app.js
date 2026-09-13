/**
 * Smart Sustainable Hostel Management System - Interactive Verification & Preview Suite
 * Faithful web implementation of the Java Swing Desktop Architecture & DatabaseManager
 */

// ==========================================
// 1. DATA STORE (Synchronized with seed.sql & LocalStorage)
// ==========================================
const DEFAULT_USERS = [
    { id: "USR-001", username: "admin", role: "ADMIN", fullName: "Dr. Sarah Jenkins", email: "admin@hostel.edu", pass: "admin123" },
    { id: "USR-002", username: "warden", role: "WARDEN", fullName: "Prof. Arthur Pendelton", email: "warden@hostel.edu", pass: "warden123" },
    { id: "USR-003", username: "student", role: "STUDENT", fullName: "Aarav Sharma", email: "aarav.sharma@hostel.edu", pass: "student123", studentId: "STU-1001" }
];

const DEFAULT_STUDENTS = [
    { id: "STU-1001", roll: "2024CS101", name: "Aarav Sharma", dept: "Computer Science", year: 3, roomNo: "A-101", bed: "Bed-A", contact: "+91 98765 43210", email: "aarav.sharma@hostel.edu", status: "ACTIVE" },
    { id: "STU-1002", roll: "2024EC102", name: "Priya Patel", dept: "Electronics", year: 2, roomNo: "B-201", bed: "Bed-A", contact: "+91 98765 43211", email: "priya.p@hostel.edu", status: "ACTIVE" }
];

function loadStoredUsers() {
    try {
        const stored = localStorage.getItem("hostel_users");
        if (stored) {
            const parsed = JSON.parse(stored);
            if (Array.isArray(parsed) && parsed.length > 0) return parsed;
        }
    } catch (e) {
        console.warn("Could not load users from localStorage", e);
    }
    return [...DEFAULT_USERS];
}

function saveUsersToStorage() {
    try {
        localStorage.setItem("hostel_users", JSON.stringify(STORE.users));
    } catch (e) {
        console.warn("Could not save users to localStorage", e);
    }
}

function loadStoredStudents() {
    try {
        const stored = localStorage.getItem("hostel_students");
        if (stored) {
            const parsed = JSON.parse(stored);
            if (Array.isArray(parsed) && parsed.length > 0) return parsed;
        }
    } catch (e) {
        console.warn("Could not load students from localStorage", e);
    }
    return [...DEFAULT_STUDENTS];
}

function saveStudentsToStorage() {
    try {
        localStorage.setItem("hostel_students", JSON.stringify(STORE.students));
    } catch (e) {
        console.warn("Could not save students to localStorage", e);
    }
}

const STORE = {
    currentUser: null,
    users: loadStoredUsers(),
    students: loadStoredStudents(),

    rooms: [
        { id: "RM-101", roomNo: "A-101", block: "A-Block", floor: 1, capacity: 2, occupied: 2, type: "DOUBLE_AC", status: "OCCUPIED" },
        { id: "RM-201", roomNo: "B-201", block: "B-Block", floor: 2, capacity: 2, occupied: 1, type: "DOUBLE_NON_AC", status: "PARTIALLY_OCCUPIED" }
    ],

    fees: [
        { id: "FEE-01", studentId: "STU-1001", studentName: "Aarav Sharma", roomNo: "A-101", amount: 45000, paid: 25000, due: 20000, status: "PARTIAL", term: "Fall 2026", dueDate: "2026-08-30" },
        { id: "FEE-02", studentId: "STU-1002", studentName: "Priya Patel", roomNo: "B-201", amount: 45000, paid: 45000, due: 0, status: "PAID", term: "Fall 2026", dueDate: "2026-08-30" }
    ],

    visitors: [
        { id: "VIS-01", name: "Rajesh Sharma", relation: "Father", studentName: "Aarav Sharma", contact: "+91 98220 11223", checkIn: "2026-09-07 10:15", checkOut: null, status: "INSIDE", passNo: "VP-901" },
        { id: "VIS-02", name: "Sunita Patel", relation: "Mother", studentName: "Priya Patel", contact: "+91 98220 44556", checkIn: "2026-09-07 11:30", checkOut: "2026-09-07 12:45", status: "CHECKED_OUT", passNo: "VP-902" }
    ],

    leaves: [
        { id: "LV-101", studentId: "STU-1001", studentName: "Aarav Sharma", roomNo: "A-101", fromDate: "2026-09-12", toDate: "2026-09-15", reason: "Attending sister's wedding in Jaipur", status: "PENDING", appliedOn: "2026-09-07 09:20", remarks: "" },
        { id: "LV-102", studentId: "STU-1002", studentName: "Priya Patel", roomNo: "B-201", fromDate: "2026-09-08", toDate: "2026-09-10", reason: "Medical checkup at hometown", status: "APPROVED", appliedOn: "2026-09-05 14:10", remarks: "Approved. Take medical certificate on return." }
    ],

    complaints: [
        { id: "CMP-01", studentName: "Aarav Sharma", roomNo: "A-101", category: "PLUMBING", desc: "Bathroom tap is leaking continuously in Room A-101.", priority: "HIGH", status: "IN_PROGRESS", reportedOn: "2026-09-06", assignedTo: "Ramesh (Plumber)" },
        { id: "CMP-02", studentName: "Priya Patel", roomNo: "B-201", category: "ELECTRICAL", desc: "Ceiling fan making clicking noise at medium speed.", priority: "MEDIUM", status: "PENDING", reportedOn: "2026-09-07", assignedTo: "Unassigned" }
    ],

    sustainability: [
        { month: "Jul 2026", electricityKwh: 14500, waterKl: 355, eChange: 4.32, wChange: 4.41, isAnomaly: false },
        { month: "Aug 2026", electricityKwh: 17800, waterKl: 420, eChange: 22.76, wChange: 18.31, isAnomaly: true }
    ],

    assets: [
        { id: "AST-01", name: "Air Conditioner 1.5 Ton", category: "APPLIANCE", roomNo: "A-101", condition: "GOOD", lastAudit: "2026-08-15" },
        { id: "AST-02", name: "Study Table & Ergonomic Chair", category: "FURNITURE", roomNo: "A-101", condition: "GOOD", lastAudit: "2026-08-15" }
    ],

    notices: [
        { id: "NOT-01", title: "Green Hostel Energy Conservation Drive", category: "SUSTAINABILITY", priority: "HIGH", date: "2026-09-05", content: "All residents are requested to turn off AC and lights when leaving rooms. Solar backup test scheduled this Saturday.", author: "Warden Office" },
        { id: "NOT-02", title: "Mess Committee Monthly Review Meeting", category: "GENERAL", priority: "MEDIUM", date: "2026-09-06", content: "Student representatives from all wings are invited to the dining hall at 7:00 PM for September menu updates.", author: "Mess Secretary" }
    ],

    messMenu: {
        todayDate: "Monday, Sep 7",
        breakfast: "Idli Sambar, Chutney, Fresh Fruits & Masala Chai / Coffee",
        lunch: "Paneer Butter Masala, Dal Tadka, Jeera Rice, Phulka, Curd & Salad",
        snacks: "Vegetable Samosa with Mint Dip & Hot Tea",
        dinner: "Methi Roti, Aloo Gobi, Yellow Dal, Steamed Rice, Gulab Jamun"
    },

    notifications: [
        { id: "NT-01", title: "Electricity Surge Warning", desc: "Aug 2026 energy consumption jumped by +22.8% MoM. Anomaly threshold (>15%) triggered.", time: "10 mins ago", read: false, type: "DANGER" },
        { id: "NT-02", title: "New Leave Application", desc: "Aarav Sharma submitted a leave request for Jaipur trip (Sep 12-15).", time: "1 hour ago", read: false, type: "INFO" }
    ],

    activityLogs: [
        { action: "LEAVE_APPLY", details: "Aarav Sharma applied for leave (Sep 12 to Sep 15)", user: "aarav", time: "Today 09:20" },
        { action: "FEE_PAYMENT", details: "Recorded ₹25,000 fee payment for Aarav Sharma", user: "admin", time: "Today 08:30" }
    ]
};

// ==========================================
// HELPERS: HTML ESCAPE, ENUMS, SVG ICONS
// ==========================================
function escapeHtml(str) {
    if (str === null || str === undefined) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

const ENUM_LABEL_MAP = {
    // Roles
    ADMIN: "Administrator",
    WARDEN: "Hostel Warden",
    STUDENT: "Hostel Resident",

    // Room types & statuses
    DOUBLE_AC: "Double AC",
    DOUBLE_NON_AC: "Double Non-AC",
    SINGLE_AC: "Single AC",
    SINGLE_NON_AC: "Single Non-AC",
    TRIPLE_NON_AC: "Triple Non-AC",
    OCCUPIED: "Occupied",
    PARTIALLY_OCCUPIED: "Partially Occupied",
    VACANT: "Vacant",
    MAINTENANCE: "Maintenance",

    // Fee statuses & actions
    PAID: "Paid",
    PARTIAL: "Partial Payment",
    PENDING: "Pending",
    OVERDUE: "Overdue",
    fees_payment_payment: "Fee Payment",
    FEE_PAYMENT: "Fee Payment",
    FEE_PAID: "Fee Payment Recorded",

    // Visitor statuses
    INSIDE: "Active / Inside",
    CHECKED_OUT: "Checked Out",
    VISITOR_CHECKIN: "Visitor Entry",
    VISITOR_CHECKOUT: "Visitor Exit",

    // Leave statuses & actions
    APPROVED: "Approved",
    REJECTED: "Rejected",
    LEAVE_APPLY: "Leave Application",
    LEAVE_APPLIED: "Leave Application",
    LEAVE_TRIAGE: "Leave Reviewed",
    LEAVE_STATUS_CHANGED: "Leave Status Updated",

    // Maintenance / Complaints
    PLUMBING: "Plumbing",
    ELECTRICAL: "Electrical",
    CARPENTRY: "Carpentry",
    INTERNET: "Wi-Fi & Internet",
    APPLIANCE: "Appliance",
    IN_PROGRESS: "In Progress",
    RESOLVED: "Resolved",
    COMPLAINT_LOGGED: "Issue Reported",
    COMPLAINT_STATUS_CHANGED: "Ticket Updated",

    // Inventory / Assets
    FURNITURE: "Furniture",
    GOOD: "Good Condition",
    NEEDS_SERVICE: "Needs Service",
    DAMAGED: "Damaged",
    ASSET_DAMAGED: "Asset Flagged",

    // Sustainability
    SUSTAINABILITY: "Sustainability",
    SUSTAINABILITY_ENTRY: "Utility Meter Logged",
    USAGE_LOGGED: "Meter Reading Logged",

    // Priority & Category
    HIGH: "High Priority",
    MEDIUM: "Medium Priority",
    LOW: "Low Priority",
    GENERAL: "General",
    DISCIPLINE: "Discipline"
};

function formatEnumLabel(val) {
    if (!val || typeof val !== "string") return val || "";
    let cleaned = val.replace(/(_payment)+$/i, "_payment");
    if (cleaned === "fees_payment_payment") cleaned = "fees_payment";

    if (ENUM_LABEL_MAP[cleaned]) return ENUM_LABEL_MAP[cleaned];
    if (ENUM_LABEL_MAP[val]) return ENUM_LABEL_MAP[val];

    return cleaned
        .toLowerCase()
        .split("_")
        .filter(Boolean)
        .map(w => w.charAt(0).toUpperCase() + w.slice(1))
        .join(" ");
}

const SVG_ICONS = {
    dashboard: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>`,
    users: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
    user: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="5"/><path d="M20 21a8 8 0 0 0-16 0"/></svg>`,
    building: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="16" height="20" x="4" y="2" rx="2" ry="2"/><path d="M9 22v-4h6v4"/><path d="M8 6h.01"/><path d="M16 6h.01"/><path d="M8 10h.01"/><path d="M16 10h.01"/><path d="M8 14h.01"/><path d="M16 14h.01"/></svg>`,
    bed: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 4v16"/><path d="M2 8h18a2 2 0 0 1 2 2v10"/><path d="M2 17h20"/><circle cx="6" cy="8" r="2"/></svg>`,
    creditCard: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" x2="22" y1="10" y2="10"/></svg>`,
    shield: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10"/></svg>`,
    fileText: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z"/><path d="M14 2v4a2 2 0 0 0 2 2h4"/><path d="M10 9H8"/><path d="M16 13H8"/><path d="M16 17H8"/></svg>`,
    wrench: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>`,
    leaf: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></svg>`,
    package: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m7.5 4.27 9 5.15"/><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>`,
    megaphone: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 11 18-5v12L3 13v-2z"/><path d="M11.6 16.8a3 3 0 1 1-5.8-1.6"/></svg>`,
    utensils: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 2v6a3 3 0 0 1-3 3 3 3 0 0 1-3-3V2"/><path d="M15 11v11"/><path d="M5 2v10a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2V2"/><path d="M7 2v6"/><path d="M9 2v6"/><path d="M7 14v8"/></svg>`,
    bell: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>`,
    barChart: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="20" y2="10"/><line x1="18" x2="18" y1="20" y2="4"/><line x1="6" x2="6" y1="20" y2="16"/></svg>`,
    settings: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>`,
    home: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
    check: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>`,
    x: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>`,
    edit: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>`,
    printer: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><rect width="12" height="8" x="6" y="14"/></svg>`,
    download: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" x2="12" y1="15" y2="3"/></svg>`,
    sun: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
    moon: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>`,
    alertTriangle: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><line x1="12" x2="12" y1="9" y2="13"/><line x1="12" x2="12.01" y1="17" y2="17"/></svg>`,
    search: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>`,
    eye: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>`,
    eyeOff: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/><path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/><path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/><line x1="2" x2="22" y1="2" y2="22"/></svg>`,
    sparkles: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 3-1.9 5.8a2 2 0 0 1-1.3 1.3L3 12l5.8 1.9a2 2 0 0 1 1.3 1.3L12 21l1.9-5.8a2 2 0 0 1 1.3-1.3L21 12l-5.8-1.9a2 2 0 0 1-1.3-1.3Z"/></svg>`,
    plus: `<svg width="{s}" height="{s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5v14"/></svg>`
};

function getIconSvg(name, size = 16) {
    const template = SVG_ICONS[name] || SVG_ICONS.sparkles;
    return template.replace(/\{s\}/g, String(size));
}

// ==========================================
// 2. NAVIGATION DEFINITION BY ROLE
// ==========================================
const NAV_CONFIG = {
    ADMIN: [
        { id: "ADMIN_DASHBOARD", label: "Executive Dashboard", icon: "dashboard", sub: "Campus hostel operations & metrics overview" },
        { id: "STUDENT_MGMT", label: "Student Directory", icon: "users", sub: "Manage registered student profiles and status" },
        { id: "ROOM_ALLOCATION", label: "Room Allocation", icon: "building", sub: "Monitor capacity, assign beds and room transfers" },
        { id: "HOSTEL_FEES", label: "Hostel Fee Management", icon: "creditCard", sub: "Track dues, record payments & issue receipts" },
        { id: "VISITOR_MGMT", label: "Visitor Management", icon: "shield", sub: "Security gate logs, active visitors & passes" },
        { id: "LEAVE_MGMT", label: "Leave Requests", icon: "fileText", sub: "Review and approve student out-station leaves" },
        { id: "MAINTENANCE", label: "Maintenance & Compliance", icon: "wrench", sub: "Complaint tickets, work orders & disciplinary records" },
        { id: "SUSTAINABILITY", label: "Sustainability Analytics", icon: "leaf", sub: "Manual utility entries, MoM variances & anomaly triggers" },
        { id: "INVENTORY", label: "Inventory & Assets", icon: "package", sub: "Hostel asset registry, health and condition audits" },
        { id: "NOTICES", label: "Notice Board", icon: "megaphone", sub: "Campus circulars, announcements & priority alerts" },
        { id: "MESS", label: "Mess Operations", icon: "utensils", sub: "Meal schedule, nutrition balance and student feedback" },
        { id: "NOTIFICATIONS", label: "Notification Center", icon: "bell", sub: "System events, approvals & security alerts" },
        { id: "REPORTS", label: "Reports & Analytics", icon: "barChart", sub: "Cross-module data aggregation & CSV export" },
        { id: "SETTINGS", label: "System Settings", icon: "settings", sub: "Hostel configuration, DB connections & thresholds" }
    ],

    WARDEN: [
        { id: "WARDEN_DASHBOARD", label: "Warden Operations Desk", icon: "shield", sub: "Daily actions, triage approvals & hostel safety" },
        { id: "ROOM_ALLOCATION", label: "Room & Occupancy", icon: "building", sub: "Monitor capacity and student bed occupancy" },
        { id: "VISITOR_MGMT", label: "Visitor Security Gate", icon: "fileText", sub: "Check in guests, issue passes and check-outs" },
        { id: "LEAVE_MGMT", label: "Leave Approvals", icon: "fileText", sub: "Triage pending student leave applications" },
        { id: "MAINTENANCE", label: "Maintenance & Triage", icon: "wrench", sub: "Resolve student complaints and assign technicians" },
        { id: "SUSTAINABILITY", label: "Utility Meter Logging", icon: "leaf", sub: "Record manual meter reads & watch consumption spikes" },
        { id: "INVENTORY", label: "Asset Condition Audit", icon: "package", sub: "Flag room damages and request replacements" },
        { id: "NOTICES", label: "Publish Notice", icon: "megaphone", sub: "Broadcast circulars to hostel students" },
        { id: "MESS", label: "Mess Menu & Ratings", icon: "utensils", sub: "Daily menu supervision and food quality feedback" },
        { id: "NOTIFICATIONS", label: "Warden Alerts", icon: "bell", sub: "Security, leave and maintenance alerts" },
        { id: "REPORTS", label: "Operational Reports", icon: "barChart", sub: "Occupancy, visitor and leave statistics" }
    ],

    STUDENT: [
        { id: "STUDENT_DASHBOARD", label: "Resident Portal", icon: "home", sub: "My hostel room, fee status & campus notices" },
        { id: "STUDENT_PROFILE", label: "My Profile & Room", icon: "user", sub: "Personal records, roommate details & bed info" },
        { id: "STUDENT_FEES", label: "My Fee Receipts", icon: "creditCard", sub: "View fee breakdown, payment history & receipts" },
        { id: "LEAVE_MGMT", label: "My Leave Requests", icon: "fileText", sub: "Submit outstation leave and track status" },
        { id: "MAINTENANCE", label: "My Maintenance Requests", icon: "wrench", sub: "Report room repairs or plumbing/electrical issues" },
        { id: "SUSTAINABILITY", label: "Eco-Campus Insights", icon: "leaf", sub: "Hostel conservation metrics & sustainability tips" },
        { id: "NOTICES", label: "Hostel Circulars", icon: "megaphone", sub: "Official notices and emergency broadcasts" },
        { id: "MESS", label: "Today's Mess & Feedback", icon: "utensils", sub: "Today's dining menu & rate food quality" },
        { id: "NOTIFICATIONS", label: "My Notifications", icon: "bell", sub: "Leave approval updates & hostel alerts" }
    ]
};

// Active state
let currentModuleId = "ADMIN_DASHBOARD";
let activeModalType = null;
let activeStudentQuery = "";

// ==========================================
// 3. AUTH & SESSION CONTROLLER
// ==========================================
function handleLogin(e) {
    e.preventDefault();
    const role = document.getElementById("login-role").value;
    const user = (document.getElementById("login-username").value || "").trim();
    const pass = (document.getElementById("login-password").value || "").trim();

    const matched = STORE.users.find(u => u.username.toLowerCase() === user.toLowerCase() && u.pass === pass && u.role === role);

    if (matched) {
        document.getElementById("login-error-banner").style.display = "none";
        loginUser(matched);
    } else {
        const errorBanner = document.getElementById("login-error-banner");
        const errorText = document.getElementById("login-error-text");
        errorText.innerText = "Invalid credentials. Please verify your role, username, and password.";
        errorBanner.style.display = "flex";
    }
}

function switchAuthMode(mode) {
    const signInForm = document.getElementById("auth-signin-form");
    const signUpForm = document.getElementById("auth-signup-form");
    const signInTab = document.getElementById("auth-tab-signin");
    const signUpTab = document.getElementById("auth-tab-signup");
    const errorBanner = document.getElementById("login-error-banner");
    const role = (document.getElementById("login-role") && document.getElementById("login-role").value) || "STUDENT";
    const studentFields = document.getElementById("signup-extra-fields");

    if (errorBanner) errorBanner.style.display = "none";

    if (mode === "signup") {
        if (signInForm) signInForm.style.display = "none";
        if (signUpForm) signUpForm.style.display = "block";
        if (signInTab) signInTab.classList.remove("active");
        if (signUpTab) signUpTab.classList.add("active");
        if (studentFields) {
            studentFields.style.display = role === "STUDENT" ? "block" : "none";
        }
    } else {
        if (signInForm) signInForm.style.display = "block";
        if (signUpForm) signUpForm.style.display = "none";
        if (signInTab) signInTab.classList.add("active");
        if (signUpTab) signUpTab.classList.remove("active");
    }
}

function handleSignUp(e) {
    e.preventDefault();
    const role = (document.getElementById("login-role") && document.getElementById("login-role").value) || "STUDENT";
    const fullName = (document.getElementById("signup-fullname").value || "").trim();
    const email = (document.getElementById("signup-email").value || "").trim().toLowerCase();
    const username = (document.getElementById("signup-username").value || "").trim().toLowerCase();
    const pass = (document.getElementById("signup-password").value || "").trim();
    const confirmPass = (document.getElementById("signup-confirm-password").value || "").trim();
    const errorBanner = document.getElementById("login-error-banner");
    const errorText = document.getElementById("login-error-text");

    if (!fullName || !email || !username || !pass) {
        errorText.innerText = "Please fill in all required fields.";
        errorBanner.style.display = "flex";
        return;
    }

    if (pass !== confirmPass) {
        errorText.innerText = "Passwords do not match. Please verify and re-enter.";
        errorBanner.style.display = "flex";
        return;
    }

    if (pass.length < 4) {
        errorText.innerText = "Password must be at least 4 characters long.";
        errorBanner.style.display = "flex";
        return;
    }

    if (STORE.users.some(u => u.username.toLowerCase() === username)) {
        errorText.innerText = "This username is already taken. Please choose another.";
        errorBanner.style.display = "flex";
        return;
    }

    let studentId = null;
    if (role === "STUDENT") {
        const roll = (document.getElementById("signup-roll").value || "").trim() || `2024CS${100 + STORE.students.length + 1}`;
        const roomNo = (document.getElementById("signup-room").value || "").trim() || "B-202";
        studentId = `STU-${1000 + STORE.students.length + 1}`;

        const newStudent = {
            id: studentId,
            roll: roll,
            name: fullName,
            dept: "Computer Science",
            year: 1,
            roomNo: roomNo,
            bed: "Bed-A",
            contact: "+91 98000 12345",
            email: email,
            status: "ACTIVE"
        };
        STORE.students.push(newStudent);
        saveStudentsToStorage();

        STORE.fees.push({
            id: `FEE-0${STORE.fees.length + 1}`,
            studentId: studentId,
            studentName: fullName,
            roomNo: roomNo,
            amount: 45000,
            paid: 0,
            due: 45000,
            status: "PENDING",
            term: "Fall 2026",
            dueDate: "2026-09-30"
        });
    }

    const newUser = {
        id: `USR-00${STORE.users.length + 1}`,
        username: username,
        role: role,
        fullName: fullName,
        email: email,
        pass: pass,
        studentId: studentId
    };

    STORE.users.push(newUser);
    saveUsersToStorage();

    if (errorBanner) errorBanner.style.display = "none";
    showToast(`Account created successfully for ${fullName}!`, "success");
    loginUser(newUser);
}

function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    if (!input) return;
    const isPass = input.type === "password";
    input.type = isPass ? "text" : "password";
    btn.innerHTML = isPass ? getIconSvg("eyeOff", 16) : getIconSvg("eye", 16);
    btn.setAttribute("aria-label", isPass ? "Hide password" : "Show password");
}

function initTheme() {
    const saved = localStorage.getItem("hostel_theme") || "light";
    document.documentElement.setAttribute("data-theme", saved);
    updateThemeToggleIcons(saved);
}

function toggleTheme() {
    const current = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
    const next = current === "dark" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", next);
    localStorage.setItem("hostel_theme", next);
    updateThemeToggleIcons(next);
}

function updateThemeToggleIcons(theme) {
    const landingBtn = document.getElementById("landing-theme-toggle");
    const appBtn = document.getElementById("app-theme-toggle");
    const iconName = theme === "dark" ? "sun" : "moon";
    const svgHtml = getIconSvg(iconName, 18);
    if (landingBtn) landingBtn.innerHTML = svgHtml;
    if (appBtn) appBtn.innerHTML = svgHtml;
}

function showLandingView() {
    document.getElementById("login-view").classList.remove("active");
    document.getElementById("shell-view").classList.remove("active");
    document.getElementById("landing-view").classList.add("active");
    window.scrollTo(0, 0);
}

function showLoginView() {
    document.getElementById("landing-view").classList.remove("active");
    document.getElementById("shell-view").classList.remove("active");
    document.getElementById("login-view").classList.add("active");
    backToRoleSelect();
    window.scrollTo(0, 0);
}

function selectLoginRole(role) {
    document.getElementById("login-role").value = role;
    const badge = document.getElementById("selected-role-badge");
    const sub = document.getElementById("selected-role-sub");
    const userLabel = document.getElementById("login-username-label");
    const userInput = document.getElementById("login-username");
    const passInput = document.getElementById("login-password");
    const studentFields = document.getElementById("signup-extra-fields");

    document.getElementById("login-error-banner").style.display = "none";

    if (role === "ADMIN") {
        badge.innerText = "ADMINISTRATOR";
        sub.innerText = "Access full system governance, analytics and settings";
        userLabel.innerText = "Username";
        userInput.value = "admin";
        passInput.value = "admin123";
    } else if (role === "WARDEN") {
        badge.innerText = "HOSTEL WARDEN";
        sub.innerText = "Operations desk, visitor log and leave approvals";
        userLabel.innerText = "Username";
        userInput.value = "warden";
        passInput.value = "warden123";
    } else {
        badge.innerText = "HOSTEL RESIDENT";
        sub.innerText = "Student portal, room allocation, leaves and digital fees";
        userLabel.innerText = "Roll No / Username";
        userInput.value = "student";
        passInput.value = "student123";
    }

    if (studentFields) {
        studentFields.style.display = role === "STUDENT" ? "block" : "none";
    }

    switchAuthMode("signin");

    document.getElementById("login-role-selection").style.display = "none";
    document.getElementById("login-credentials-step").style.display = "block";
}

function backToRoleSelect() {
    document.getElementById("login-credentials-step").style.display = "none";
    document.getElementById("login-role-selection").style.display = "block";
    document.getElementById("login-error-banner").style.display = "none";
}

function handleForgotPassword() {
    showToast("Password reset instructions sent to system administrator.", "info");
}

function loginUser(userObj) {
    STORE.currentUser = userObj;
    
    document.getElementById("login-view").classList.remove("active");
    document.getElementById("landing-view").classList.remove("active");
    document.getElementById("shell-view").classList.add("active");

    document.getElementById("user-display-name").innerText = userObj.fullName;
    document.getElementById("user-display-role").innerText = formatEnumLabel(userObj.role);
    document.getElementById("user-avatar-initial").innerText = userObj.fullName.charAt(0);
    document.getElementById("sidebar-role-badge").innerText = formatEnumLabel(userObj.role);

    document.getElementById("drop-user-name").innerText = userObj.fullName;
    document.getElementById("drop-user-email").innerText = userObj.email;

    renderSidebarNav();

    if (userObj.role === "ADMIN") {
        navigateTo("ADMIN_DASHBOARD");
    } else if (userObj.role === "WARDEN") {
        navigateTo("WARDEN_DASHBOARD");
    } else {
        navigateTo("STUDENT_DASHBOARD");
    }

    updateNotificationBadge();
    showToast(`Welcome back, ${userObj.fullName}!`, "success");
}

function handleLogout() {
    STORE.currentUser = null;
    document.getElementById("shell-view").classList.remove("active");
    showLandingView();
    document.getElementById("profile-dropdown").style.display = "none";
    document.getElementById("notif-dropdown").style.display = "none";
    showToast("Signed out successfully.", "info");
}

// ==========================================
// 4. SIDEBAR & NAVIGATION CONTROLLER
// ==========================================
function renderSidebarNav() {
    const navContainer = document.getElementById("sidebar-nav");
    const navItems = NAV_CONFIG[STORE.currentUser.role] || [];

    let html = "";
    navItems.forEach(item => {
        const isActive = item.id === currentModuleId ? "active" : "";
        html += `
            <button class="nav-item ${isActive}" onclick="navigateTo('${item.id}')">
                <span class="nav-icon">${getIconSvg(item.icon, 18)}</span>
                <span class="nav-label">${escapeHtml(item.label)}</span>
            </button>
        `;
    });

    navContainer.innerHTML = html;
}

function navigateTo(moduleId) {
    currentModuleId = moduleId;
    renderSidebarNav();

    const allNavs = [...NAV_CONFIG.ADMIN, ...NAV_CONFIG.WARDEN, ...NAV_CONFIG.STUDENT];
    const meta = allNavs.find(n => n.id === moduleId) || { label: "Module", sub: "Hostel Management Suite" };

    document.getElementById("header-page-title").innerText = meta.label;
    document.getElementById("header-page-sub").innerText = meta.sub;

    const canvas = document.getElementById("content-area");
    canvas.innerHTML = getModuleHtml(moduleId);

    document.getElementById("notif-dropdown").style.display = "none";
    document.getElementById("profile-dropdown").style.display = "none";
}

// ==========================================
// 5. REACTIVE EVENT BUS & DISPATCHER
// ==========================================
function dispatchAppEvent(eventType, payload) {
    console.log(`[AppEventBus] Dispatched: ${eventType}`, payload);

    if (eventType === "FEE_PAID") {
        STORE.notifications.unshift({
            id: `NT-${Date.now()}`,
            title: "Fee Payment Recorded",
            desc: `Payment of ₹${payload.amount} recorded for ${payload.studentName}.`,
            time: "Just now",
            read: false,
            type: "SUCCESS"
        });
        STORE.activityLogs.unshift({
            action: "FEE_PAYMENT",
            details: `Recorded ₹${payload.amount} payment for ${payload.studentName}`,
            user: STORE.currentUser.username,
            time: "Just now"
        });
    } else if (eventType === "LEAVE_STATUS_CHANGED") {
        STORE.notifications.unshift({
            id: `NT-${Date.now()}`,
            title: `Leave ${formatEnumLabel(payload.status)}`,
            desc: `Leave request for ${payload.studentName} was ${payload.status.toLowerCase()}.`,
            time: "Just now",
            read: false,
            type: payload.status === "APPROVED" ? "SUCCESS" : "DANGER"
        });
        STORE.activityLogs.unshift({
            action: "LEAVE_TRIAGE",
            details: `Warden updated leave for ${payload.studentName} to ${formatEnumLabel(payload.status)}`,
            user: STORE.currentUser.username,
            time: "Just now"
        });
    } else if (eventType === "USAGE_LOGGED") {
        if (payload.isAnomaly) {
            STORE.notifications.unshift({
                id: `NT-${Date.now()}`,
                title: "Sustainability Anomaly Jump Detected",
                desc: `${payload.month} consumption jumped by +${payload.eChange.toFixed(1)}% MoM. Immediate audit required.`,
                time: "Just now",
                read: false,
                type: "DANGER"
            });
        }
        STORE.activityLogs.unshift({
            action: "SUSTAINABILITY_ENTRY",
            details: `Logged meter read for ${payload.month}: ${payload.electricityKwh.toLocaleString()} kWh, ${payload.waterKl.toLocaleString()} kL`,
            user: STORE.currentUser.username,
            time: "Just now"
        });
    } else if (eventType === "COMPLAINT_STATUS_CHANGED") {
        STORE.notifications.unshift({
            id: `NT-${Date.now()}`,
            title: "Maintenance Ticket Updated",
            desc: `Complaint #${payload.id} in ${payload.roomNo} is now ${formatEnumLabel(payload.status)}.`,
            time: "Just now",
            read: false,
            type: "INFO"
        });
    }

    updateNotificationBadge();

    if (document.getElementById("content-area")) {
        document.getElementById("content-area").innerHTML = getModuleHtml(currentModuleId);
    }
}

// ==========================================
// 6. MODULE VIEW RENDERERS
// ==========================================
function getModuleHtml(moduleId) {
    switch (moduleId) {
        case "ADMIN_DASHBOARD": return renderAdminDashboard();
        case "WARDEN_DASHBOARD": return renderWardenDashboard();
        case "STUDENT_DASHBOARD": return renderStudentDashboard();
        case "STUDENT_MGMT": return renderStudentManagement();
        case "ROOM_ALLOCATION": return renderRoomAllocation();
        case "HOSTEL_FEES": return renderHostelFees();
        case "VISITOR_MGMT": return renderVisitorManagement();
        case "LEAVE_MGMT": return renderLeaveManagement();
        case "MAINTENANCE": return renderMaintenanceCompliance();
        case "SUSTAINABILITY": return renderSustainabilityPanel();
        case "INVENTORY": return renderInventoryPanel();
        case "NOTICES": return renderNoticePanel();
        case "MESS": return renderMessPanel();
        case "NOTIFICATIONS": return renderNotificationsPanel();
        case "REPORTS": return renderReportsPanel();
        case "SETTINGS": return renderSettingsPanel();
        case "STUDENT_PROFILE": return renderStudentProfile();
        case "STUDENT_FEES": return renderStudentPersonalFees();
        default: return `<div class="card-panel"><h3>${escapeHtml(moduleId)}</h3><p>Module content loaded successfully.</p></div>`;
    }
}

// 6.1 ADMIN DASHBOARD
function renderAdminDashboard() {
    const totalStudents = STORE.students.length;
    const totalRooms = STORE.rooms.length;
    const totalBeds = STORE.rooms.reduce((acc, r) => acc + r.capacity, 0);
    const occupiedBeds = STORE.rooms.reduce((acc, r) => acc + r.occupied, 0);
    const occRate = Math.round((occupiedBeds / totalBeds) * 100);
    const totalFeeRevenue = STORE.fees.reduce((acc, f) => acc + f.paid, 0);
    const pendingDues = STORE.fees.reduce((acc, f) => acc + f.due, 0);
    const pendingLeaves = STORE.leaves.filter(l => l.status === "PENDING").length;
    const activeVisitors = STORE.visitors.filter(v => v.status === "INSIDE").length;

    return `
        <!-- KPI Metrics Row -->
        <div class="kpi-grid">
            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Total Residents</span>
                    <span class="stat-icon primary">${getIconSvg('users', 16)}</span>
                </div>
                <div class="stat-value">${totalStudents}</div>
                <span class="stat-sub positive">100% capacity in A-Block</span>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Bed Occupancy Rate</span>
                    <span class="stat-icon success">${getIconSvg('building', 16)}</span>
                </div>
                <div class="stat-value">${occRate}%</div>
                <span class="stat-sub">${occupiedBeds} of ${totalBeds} beds assigned</span>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Fee Collection</span>
                    <span class="stat-icon primary">${getIconSvg('creditCard', 16)}</span>
                </div>
                <div class="stat-value">₹${(totalFeeRevenue/1000).toFixed(1)}k</div>
                <span class="stat-sub negative">₹${(pendingDues/1000).toFixed(1)}k pending dues</span>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Visitors Inside</span>
                    <span class="stat-icon warning">${getIconSvg('shield', 16)}</span>
                </div>
                <div class="stat-value">${activeVisitors}</div>
                <span class="stat-sub">${pendingLeaves} leaves pending triage</span>
            </div>
        </div>

        <!-- 2-Column Split: Sustainability Anomaly Widget & Recent Activity -->
        <div class="dual-layout">
            <div class="card-panel">
                <div class="card-header-row">
                    <div>
                        <h3 class="card-title">Campus Sustainability Pulse</h3>
                        <p class="card-subtitle">Zero-hardware monthly manual utility tracker</p>
                    </div>
                    <button class="btn btn-primary btn-sm" onclick="openModal('ADD_UTILITY')">${getIconSvg('plus', 13)} Log Meter Read</button>
                </div>
                ${renderSustainabilityWidget()}
            </div>

            <div class="card-panel">
                <div class="card-header-row">
                    <div>
                        <h3 class="card-title">Real-time Activity Stream</h3>
                        <p class="card-subtitle">Cross-module event bus audit trail</p>
                    </div>
                    <button class="btn btn-ghost btn-sm" onclick="navigateTo('REPORTS')">Audit Logs &rarr;</button>
                </div>
                <div class="dropdown-list" style="max-height: 260px;">
                    ${STORE.activityLogs.map(log => `
                        <div class="notif-item">
                            <strong style="color: var(--color-primary);">${formatEnumLabel(log.action)}</strong>
                            <p style="margin: 3px 0; color: var(--text-primary);">${escapeHtml(log.details)}</p>
                            <div class="notif-meta">
                                <span>Actor: <b>${escapeHtml(log.user)}</b></span>
                                <span>${escapeHtml(log.time)}</span>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        </div>

        <!-- Quick Action Shortcuts -->
        <div class="card-panel">
            <div class="card-header-row">
                <h3 class="card-title">Administrative Quick Actions</h3>
            </div>
            <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                <button class="btn btn-primary" onclick="openModal('ADD_STUDENT')">${getIconSvg('users', 14)} Register Student</button>
                <button class="btn btn-secondary" onclick="openModal('ALLOCATE_ROOM')">${getIconSvg('building', 14)} Allocate Bed</button>
                <button class="btn btn-secondary" onclick="openModal('RECORD_FEE')">${getIconSvg('creditCard', 14)} Record Fee Payment</button>
                <button class="btn btn-secondary" onclick="openModal('ADD_NOTICE')">${getIconSvg('megaphone', 14)} Post Circular</button>
                <button class="btn btn-secondary" onclick="openModal('ADD_UTILITY')">${getIconSvg('leaf', 14)} Log Utility Read</button>
            </div>
        </div>
    `;
}

// 6.2 WARDEN DASHBOARD
function renderWardenDashboard() {
    const pendingLeaves = STORE.leaves.filter(l => l.status === "PENDING");
    const activeComplaints = STORE.complaints.filter(c => c.status !== "RESOLVED");
    const insideVisitors = STORE.visitors.filter(v => v.status === "INSIDE");

    return `
        <!-- Action Banner -->
        <div class="alert-banner" style="background: rgba(139, 90, 60, 0.12); border: 1px solid var(--color-primary); color: var(--color-primary-dark);">
            <span>${getIconSvg('shield', 20)}</span>
            <div><strong>Warden Daily Action Center:</strong> You have <b>${pendingLeaves.length} pending leave requests</b> and <b>${activeComplaints.length} active maintenance tickets</b> requiring action today.</div>
        </div>

        <div class="kpi-grid">
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Pending Leaves</span><span class="stat-icon warning">${getIconSvg('fileText', 16)}</span></div>
                <div class="stat-value">${pendingLeaves.length}</div>
                <span class="stat-sub">Requires warden approval</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Active Complaints</span><span class="stat-icon danger">${getIconSvg('wrench', 16)}</span></div>
                <div class="stat-value">${activeComplaints.length}</div>
                <span class="stat-sub">Plumbing and electrical tickets</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Visitors On-Campus</span><span class="stat-icon primary">${getIconSvg('shield', 16)}</span></div>
                <div class="stat-value">${insideVisitors.length}</div>
                <span class="stat-sub">Checked in via main gate</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Damaged Assets</span><span class="stat-icon warning">${getIconSvg('package', 16)}</span></div>
                <div class="stat-value">${STORE.assets.filter(a => a.condition === "NEEDS_SERVICE").length}</div>
                <span class="stat-sub">Requires technician audit</span>
            </div>
        </div>

        <!-- Triage Queues -->
        <div class="dual-layout">
            <div class="card-panel">
                <div class="card-header-row">
                    <h3 class="card-title">Pending Leave Approvals</h3>
                    <button class="btn btn-ghost btn-sm" onclick="navigateTo('LEAVE_MGMT')">All Leaves &rarr;</button>
                </div>
                ${pendingLeaves.length === 0 ? '<p style="color: var(--text-muted);">No pending leave requests.</p>' : `
                    <div style="display: flex; flex-direction: column; gap: 10px;">
                        ${pendingLeaves.map(lv => `
                            <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                                <div style="display: flex; justify-content: space-between;">
                                    <strong>${escapeHtml(lv.studentName)} (${escapeHtml(lv.roomNo)})</strong>
                                    <span class="badge-pill warning">${formatEnumLabel(lv.status)}</span>
                                </div>
                                <p style="font-size: 12px; color: var(--text-secondary); margin: 4px 0;">Reason: ${escapeHtml(lv.reason)}</p>
                                <div style="font-size: 11px; color: var(--text-muted); margin-bottom: 8px;">Dates: ${escapeHtml(lv.fromDate)} to ${escapeHtml(lv.toDate)}</div>
                                <div style="display: flex; gap: 8px;">
                                    <button class="btn btn-success btn-sm" onclick="triageLeave('${lv.id}', 'APPROVED')">${getIconSvg('check', 13)} Approve</button>
                                    <button class="btn btn-danger btn-sm" onclick="triageLeave('${lv.id}', 'REJECTED')">${getIconSvg('x', 13)} Reject</button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                `}
            </div>

            <div class="card-panel">
                <div class="card-header-row">
                    <h3 class="card-title">Active Maintenance Complaints</h3>
                    <button class="btn btn-ghost btn-sm" onclick="navigateTo('MAINTENANCE')">All Tickets &rarr;</button>
                </div>
                <div style="display: flex; flex-direction: column; gap: 10px;">
                    ${activeComplaints.map(cmp => `
                        <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                            <div style="display: flex; justify-content: space-between;">
                                <strong>${formatEnumLabel(cmp.category)} - ${escapeHtml(cmp.roomNo)}</strong>
                                <span class="badge-pill ${cmp.priority === 'HIGH' ? 'danger' : 'warning'}">${formatEnumLabel(cmp.priority)}</span>
                            </div>
                            <p style="font-size: 12px; color: var(--text-primary); margin: 4px 0;">${escapeHtml(cmp.desc)}</p>
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 6px;">
                                <small style="color: var(--text-muted);">Assigned: ${escapeHtml(cmp.assignedTo)}</small>
                                <button class="btn btn-secondary btn-sm" onclick="resolveComplaint('${cmp.id}')">${getIconSvg('check', 13)} Resolve</button>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        </div>
    `;
}

// 6.3 STUDENT DASHBOARD
function renderStudentDashboard() {
    const sId = (STORE.currentUser && STORE.currentUser.studentId) ? STORE.currentUser.studentId : "STU-1001";
    const s = STORE.students.find(stu => stu.id === sId) || STORE.students[0];
    const myFee = STORE.fees.find(f => f.studentId === s.id) || { amount: 45000, paid: 45000, due: 0, status: "PAID" };
    const myLeaves = STORE.leaves.filter(l => l.studentId === s.id);
    const myComplaints = STORE.complaints.filter(c => c.studentName === s.name);

    return `
        <!-- Resident Card -->
        <div class="card-panel" style="background: linear-gradient(135deg, var(--bg-surface), rgba(139, 90, 60, 0.08));">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
                <div>
                    <span class="badge-pill success">Resident Active</span>
                    <h2 style="font-size: 24px; font-weight: 800; margin-top: 6px;">${escapeHtml(s.name)}</h2>
                    <p style="color: var(--text-secondary); font-size: 13px;">${escapeHtml(s.roll)} &bull; ${escapeHtml(s.dept)} (Year ${s.year}) &bull; Room: <b>${escapeHtml(s.roomNo)} (${escapeHtml(s.bed)})</b></p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <button class="btn btn-primary" onclick="openModal('APPLY_LEAVE')">${getIconSvg('fileText', 14)} Apply Outstation Leave</button>
                    <button class="btn btn-secondary" onclick="openModal('LOG_COMPLAINT')">${getIconSvg('wrench', 14)} Report Room Issue</button>
                </div>
            </div>
        </div>

        <div class="kpi-grid">
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">My Room</span><span class="stat-icon primary">${getIconSvg('building', 16)}</span></div>
                <div class="stat-value">${escapeHtml(s.roomNo)}</div>
                <span class="stat-sub">Double AC &bull; Floor 1</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Fee Status</span><span class="stat-icon success">${getIconSvg('creditCard', 16)}</span></div>
                <div class="stat-value">${formatEnumLabel(myFee.status)}</div>
                <span class="stat-sub positive">₹${myFee.paid.toLocaleString()} Paid &bull; ${myFee.due === 0 ? 'Zero Due' : '₹' + myFee.due.toLocaleString() + ' Due'}</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Leave Status</span><span class="stat-icon warning">${getIconSvg('fileText', 16)}</span></div>
                <div class="stat-value">${myLeaves[0] ? formatEnumLabel(myLeaves[0].status) : 'None'}</div>
                <span class="stat-sub">${myLeaves.length} Total Requests</span>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Active Tickets</span><span class="stat-icon primary">${getIconSvg('wrench', 16)}</span></div>
                <div class="stat-value">${myComplaints.length}</div>
                <span class="stat-sub">${myComplaints.filter(c => c.status !== 'RESOLVED').length} In-Progress</span>
            </div>
        </div>

        <div class="dual-layout">
            <div class="card-panel">
                <div class="card-header-row">
                    <h3 class="card-title">Today's Hostel Mess Menu</h3>
                    <button class="btn btn-ghost btn-sm" onclick="navigateTo('MESS')">Full Menu &rarr;</button>
                </div>
                <div style="display: flex; flex-direction: column; gap: 10px; font-size: 13px;">
                    <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-sm); border: 1px solid var(--border-color);"><b>Breakfast (7:30 AM - 9:30 AM):</b> ${escapeHtml(STORE.messMenu.breakfast)}</div>
                    <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-sm); border: 1px solid var(--border-color);"><b>Lunch (12:30 PM - 2:30 PM):</b> ${escapeHtml(STORE.messMenu.lunch)}</div>
                    <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-sm); border: 1px solid var(--border-color);"><b>Dinner (7:30 PM - 9:30 PM):</b> ${escapeHtml(STORE.messMenu.dinner)}</div>
                </div>
            </div>

            <div class="card-panel">
                <div class="card-header-row">
                    <h3 class="card-title">Latest Hostel Circular</h3>
                    <button class="btn btn-ghost btn-sm" onclick="navigateTo('NOTICES')">All Notices &rarr;</button>
                </div>
                ${STORE.notices.slice(0, 2).map(n => `
                    <div style="background: var(--bg-primary); padding: 12px; border-radius: var(--radius-md); margin-bottom: 10px; border: 1px solid var(--border-color);">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <strong>${escapeHtml(n.title)}</strong>
                            <span class="badge-pill ${n.priority === 'HIGH' ? 'danger' : 'primary'}">${formatEnumLabel(n.category)}</span>
                        </div>
                        <p style="font-size: 12px; color: var(--text-secondary); margin-top: 4px;">${escapeHtml(n.content)}</p>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 6.4 SUSTAINABILITY WIDGET & PANEL
function renderSustainabilityWidget() {
    const maxKwh = 22000;
    const maxKl = 500;

    return `
        <div class="sustainability-chart-container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid var(--border-color); font-size: 12px;">
                <div style="display: flex; gap: 16px; align-items: center;">
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <span style="width: 12px; height: 12px; background: #8B5A3C; border-radius: 3px; display: inline-block;"></span>
                        <span style="font-weight: 600; color: var(--text-primary);">Electricity (kWh)</span>
                    </div>
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <span style="width: 12px; height: 12px; background: #0E7490; border-radius: 3px; display: inline-block;"></span>
                        <span style="font-weight: 600; color: var(--text-primary);">Water (kL)</span>
                    </div>
                </div>
                <div style="font-size: 11px; color: var(--text-muted);">
                    Flagged threshold: <b>&gt; 15% MoM surge</b>
                </div>
            </div>

            <div class="chart-bars-wrap">
                ${STORE.sustainability.map(item => {
                    const ePct = Math.min(100, Math.max(16, Math.round((item.electricityKwh / maxKwh) * 100)));
                    const wPct = Math.min(100, Math.max(16, Math.round((item.waterKl / maxKl) * 100)));
                    return `
                        <div class="chart-month-row">
                            <span class="chart-month-label">${escapeHtml(item.month)}</span>
                            <div class="bar-track" title="Electricity: ${item.electricityKwh.toLocaleString()} kWh">
                                <div class="bar-fill electricity" style="width: ${ePct}%;">
                                    ${item.electricityKwh.toLocaleString()} kWh
                                </div>
                            </div>
                            <div class="bar-track" title="Water: ${item.waterKl.toLocaleString()} kL">
                                <div class="bar-fill water" style="width: ${wPct}%;">
                                    ${item.waterKl.toLocaleString()} kL
                                </div>
                            </div>
                            <div>
                                ${item.isAnomaly ? `
                                    <span class="anomaly-tag">${getIconSvg('alertTriangle', 12)} +${item.eChange.toFixed(1)}% Jump</span>
                                ` : `
                                    <span class="normal-tag">${item.eChange >= 0 ? '+' : ''}${item.eChange.toFixed(1)}% MoM</span>
                                `}
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        </div>
    `;
}

function renderSustainabilityPanel() {
    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Campus Resource Analytics</h3>
                    <p class="card-subtitle">Manual electricity and water logging with automated MoM % variance and jump anomaly detection (&gt;15%)</p>
                </div>
                <button class="btn btn-primary" onclick="openModal('ADD_UTILITY')">${getIconSvg('plus', 14)} Log Month Utility Read</button>
            </div>
            ${renderSustainabilityWidget()}
        </div>

        <div class="card-panel">
            <div class="card-header-row">
                <h3 class="card-title">Monthly Utility Log Records</h3>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Billing Month</th>
                            <th>Electricity (kWh)</th>
                            <th>MoM Elec Change</th>
                            <th>Water (kL)</th>
                            <th>MoM Water Change</th>
                            <th>Status / Anomaly Alert</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.sustainability.map(s => `
                            <tr>
                                <td><b>${escapeHtml(s.month)}</b></td>
                                <td>${s.electricityKwh.toLocaleString()} kWh</td>
                                <td>${s.eChange >= 0 ? '+' : ''}${s.eChange.toFixed(2)}%</td>
                                <td>${s.waterKl.toLocaleString()} kL</td>
                                <td>${s.wChange >= 0 ? '+' : ''}${s.wChange.toFixed(2)}%</td>
                                <td>
                                    ${s.isAnomaly 
                                        ? `<span class="badge-pill danger">${getIconSvg('alertTriangle', 12)} Anomaly Jump (&gt;15%)</span>` 
                                        : `<span class="badge-pill success">${getIconSvg('check', 12)} Normal Usage</span>`}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.5 STUDENT MANAGEMENT & SEARCH
function renderStudentManagement() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div class="search-box" style="flex: 1; max-width: 380px;">
                    <input type="text" id="student-search-input" class="form-control" placeholder="Search by name, roll no, room, dept..." value="${escapeHtml(activeStudentQuery)}" oninput="handleStudentSearch(this.value)">
                </div>
                <button class="btn btn-primary" onclick="openModal('ADD_STUDENT')">${getIconSvg('plus', 14)} Register Student</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table" id="student-table">
                    <thead>
                        <tr>
                            <th>Roll Number</th>
                            <th>Student Name</th>
                            <th>Department</th>
                            <th>Year</th>
                            <th>Room / Bed</th>
                            <th>Contact</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${getStudentTableRowsHtml()}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function getStudentTableRowsHtml() {
    const q = activeStudentQuery;
    const filtered = STORE.students.filter(s => {
        if (!q) return true;
        return (
            (s.name && s.name.toLowerCase().includes(q)) ||
            (s.roll && s.roll.toLowerCase().includes(q)) ||
            (s.roomNo && s.roomNo.toLowerCase().includes(q)) ||
            (s.dept && s.dept.toLowerCase().includes(q))
        );
    });

    if (filtered.length === 0) {
        return `
            <tr>
                <td colspan="8" style="text-align: center; padding: 36px 16px; color: var(--text-secondary);">
                    <div style="display: flex; justify-content: center; margin-bottom: 8px; opacity: 0.6;">${getIconSvg('search', 28)}</div>
                    <div style="font-weight: 600; font-size: 14px; color: var(--text-primary);">No students found matching "${escapeHtml(q)}"</div>
                    <div style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">Search filters by name, roll number, department, or room number.</div>
                </td>
            </tr>
        `;
    }

    return filtered.map(s => `
        <tr>
            <td><b>${escapeHtml(s.roll)}</b></td>
            <td>${escapeHtml(s.name)}</td>
            <td>${escapeHtml(s.dept)}</td>
            <td>Year ${s.year}</td>
            <td>${escapeHtml(s.roomNo)} (${escapeHtml(s.bed)})</td>
            <td>${escapeHtml(s.contact)}</td>
            <td><span class="badge-pill success">${formatEnumLabel(s.status)}</span></td>
            <td>
                <button class="btn btn-ghost btn-sm" onclick="editStudent('${s.id}')">${getIconSvg('edit', 13)} Edit</button>
            </td>
        </tr>
    `).join('');
}

function handleStudentSearch(query) {
    activeStudentQuery = (query || "").trim().toLowerCase();
    const tbody = document.querySelector("#student-table tbody");
    if (tbody) {
        tbody.innerHTML = getStudentTableRowsHtml();
    }
}

function editStudent(id) {
    const stu = STORE.students.find(s => s.id === id);
    if (!stu) return;
    showToast(`Editing profile records for ${stu.name} (${stu.roll})...`, "info");
}

// 6.6 ROOM ALLOCATION
function renderRoomAllocation() {
    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Hostel Wings & Bed Matrix</h3>
                    <p class="card-subtitle">Real-time room occupancy and allocation management</p>
                </div>
                <button class="btn btn-primary" onclick="openModal('ALLOCATE_ROOM')">${getIconSvg('plus', 14)} Allocate Bed</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Room No</th>
                            <th>Block / Wing</th>
                            <th>Room Type</th>
                            <th>Capacity</th>
                            <th>Occupied Beds</th>
                            <th>Availability Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.rooms.map(r => {
                            let badge = "success";
                            if (r.status === "OCCUPIED") badge = "danger";
                            if (r.status === "PARTIALLY_OCCUPIED") badge = "warning";
                            return `
                                <tr>
                                    <td><b>${escapeHtml(r.roomNo)}</b></td>
                                    <td>${escapeHtml(r.block)} (Floor ${r.floor})</td>
                                    <td>${formatEnumLabel(r.type)}</td>
                                    <td>${r.capacity} Beds</td>
                                    <td>${r.occupied} / ${r.capacity}</td>
                                    <td><span class="badge-pill ${badge}">${formatEnumLabel(r.status)}</span></td>
                                    <td>
                                        <button class="btn btn-secondary btn-sm" onclick="openModal('TRANSFER_ROOM', '${r.roomNo}')">Transfer / Reassign</button>
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.7 HOSTEL FEES
function renderHostelFees() {
    const totalCollected = STORE.fees.reduce((acc, f) => acc + f.paid, 0);
    const totalDue = STORE.fees.reduce((acc, f) => acc + f.due, 0);

    return `
        <div class="kpi-grid">
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Total Fee Invoiced</span><span class="stat-icon primary">${getIconSvg('creditCard', 16)}</span></div>
                <div class="stat-value">₹${((totalCollected+totalDue)/1000).toFixed(1)}k</div>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Collected Amount</span><span class="stat-icon success">${getIconSvg('check', 16)}</span></div>
                <div class="stat-value">₹${(totalCollected/1000).toFixed(1)}k</div>
            </div>
            <div class="stat-card">
                <div class="stat-header"><span class="stat-label">Pending Dues</span><span class="stat-icon danger">${getIconSvg('alertTriangle', 16)}</span></div>
                <div class="stat-value">₹${(totalDue/1000).toFixed(1)}k</div>
            </div>
        </div>

        <div class="card-panel">
            <div class="action-toolbar">
                <h3 class="card-title">Fee Ledgers & Payment Records</h3>
                <button class="btn btn-primary" onclick="openModal('RECORD_FEE')">${getIconSvg('plus', 14)} Record Fee Payment</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Student</th>
                            <th>Room</th>
                            <th>Term</th>
                            <th>Total Fee</th>
                            <th>Paid Amount</th>
                            <th>Due Amount</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.fees.map(f => {
                            let badge = "success";
                            if (f.status === "PENDING") badge = "warning";
                            if (f.status === "OVERDUE") badge = "danger";
                            if (f.status === "PARTIAL") badge = "primary";
                            return `
                                <tr>
                                    <td><b>${escapeHtml(f.studentName)}</b></td>
                                    <td>${escapeHtml(f.roomNo)}</td>
                                    <td>${escapeHtml(f.term)}</td>
                                    <td>₹${f.amount.toLocaleString()}</td>
                                    <td>₹${f.paid.toLocaleString()}</td>
                                    <td>₹${f.due.toLocaleString()}</td>
                                    <td><span class="badge-pill ${badge}">${formatEnumLabel(f.status)}</span></td>
                                    <td>
                                        <button class="btn btn-secondary btn-sm" onclick="printReceipt('${f.id}')">${getIconSvg('printer', 13)} Receipt</button>
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.8 VISITOR MANAGEMENT
function renderVisitorManagement() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">Security Gate Visitor Register</h3>
                    <p class="card-subtitle">Real-time check-in/out tracking and visitor pass issuance</p>
                </div>
                <button class="btn btn-primary" onclick="openModal('VISITOR_CHECKIN')">${getIconSvg('plus', 14)} New Visitor Check-In</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Pass No</th>
                            <th>Visitor Name</th>
                            <th>Relation</th>
                            <th>Visiting Student</th>
                            <th>Contact</th>
                            <th>Check-In</th>
                            <th>Check-Out</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.visitors.map(v => `
                            <tr>
                                <td><b>${escapeHtml(v.passNo)}</b></td>
                                <td>${escapeHtml(v.name)}</td>
                                <td>${escapeHtml(v.relation)}</td>
                                <td>${escapeHtml(v.studentName)}</td>
                                <td>${escapeHtml(v.contact)}</td>
                                <td>${escapeHtml(v.checkIn)}</td>
                                <td>${v.checkOut ? escapeHtml(v.checkOut) : '--'}</td>
                                <td><span class="badge-pill ${v.status === 'INSIDE' ? 'warning' : 'neutral'}">${formatEnumLabel(v.status)}</span></td>
                                <td>
                                    ${v.status === 'INSIDE' 
                                        ? `<button class="btn btn-danger btn-sm" onclick="checkoutVisitor('${v.id}')">${getIconSvg('x', 13)} Check Out</button>` 
                                        : `<span style="color: var(--text-muted); font-size: 11px;">Completed</span>`}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.9 LEAVE MANAGEMENT
function renderLeaveManagement() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">Outstation Leave Applications</h3>
                    <p class="card-subtitle">Student out-pass requests, warden review & parent notification</p>
                </div>
                <button class="btn btn-primary" onclick="openModal('APPLY_LEAVE')">${getIconSvg('plus', 14)} Apply For Leave</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Student</th>
                            <th>Room</th>
                            <th>From Date</th>
                            <th>To Date</th>
                            <th>Reason</th>
                            <th>Applied On</th>
                            <th>Status</th>
                            <th>Warden Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.leaves.map(lv => `
                            <tr>
                                <td><b>${escapeHtml(lv.studentName)}</b></td>
                                <td>${escapeHtml(lv.roomNo)}</td>
                                <td>${escapeHtml(lv.fromDate)}</td>
                                <td>${escapeHtml(lv.toDate)}</td>
                                <td>${escapeHtml(lv.reason)}</td>
                                <td>${escapeHtml(lv.appliedOn)}</td>
                                <td>
                                    <span class="badge-pill ${lv.status === 'APPROVED' ? 'success' : (lv.status === 'REJECTED' ? 'danger' : 'warning')}">
                                        ${formatEnumLabel(lv.status)}
                                    </span>
                                </td>
                                <td>
                                    ${lv.status === 'PENDING' && (STORE.currentUser.role === 'ADMIN' || STORE.currentUser.role === 'WARDEN') ? `
                                        <div style="display: flex; gap: 6px;">
                                            <button class="btn btn-success btn-sm" onclick="triageLeave('${lv.id}', 'APPROVED')">${getIconSvg('check', 13)} Approve</button>
                                            <button class="btn btn-danger btn-sm" onclick="triageLeave('${lv.id}', 'REJECTED')">${getIconSvg('x', 13)} Reject</button>
                                        </div>
                                    ` : `<small style="color: var(--text-muted);">${escapeHtml(lv.remarks) || 'No remarks'}</small>`}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.10 MAINTENANCE & COMPLIANCE
function renderMaintenanceCompliance() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">Hostel Maintenance Tickets</h3>
                    <p class="card-subtitle">Plumbing, electrical, appliance repairs & technician dispatch</p>
                </div>
                <button class="btn btn-primary" onclick="openModal('LOG_COMPLAINT')">${getIconSvg('plus', 14)} Report Issue</button>
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Category</th>
                            <th>Room No</th>
                            <th>Issue Description</th>
                            <th>Priority</th>
                            <th>Assigned Tech</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.complaints.map(c => `
                            <tr>
                                <td><b>${escapeHtml(c.id)}</b></td>
                                <td>${formatEnumLabel(c.category)}</td>
                                <td>${escapeHtml(c.roomNo)}</td>
                                <td>${escapeHtml(c.desc)}</td>
                                <td><span class="badge-pill ${c.priority === 'HIGH' ? 'danger' : 'warning'}">${formatEnumLabel(c.priority)}</span></td>
                                <td>${escapeHtml(c.assignedTo)}</td>
                                <td><span class="badge-pill ${c.status === 'RESOLVED' ? 'success' : (c.status === 'IN_PROGRESS' ? 'primary' : 'warning')}">${formatEnumLabel(c.status)}</span></td>
                                <td>
                                    ${c.status !== 'RESOLVED' 
                                        ? `<button class="btn btn-success btn-sm" onclick="resolveComplaint('${c.id}')">${getIconSvg('check', 13)} Resolve</button>` 
                                        : `<span style="color: var(--text-muted); font-size: 11px;">Closed</span>`}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.11 INVENTORY & ASSETS
function renderInventoryPanel() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">Hostel Asset Registry</h3>
                    <p class="card-subtitle">Room appliances, electrical installations & physical health audits</p>
                </div>
                ${STORE.currentUser.role === 'ADMIN' ? `<button class="btn btn-primary" onclick="openModal('ADD_ASSET')">${getIconSvg('plus', 14)} Add Asset</button>` : ''}
            </div>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Asset Tag</th>
                            <th>Item Name</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Condition</th>
                            <th>Last Audit</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.assets.map(a => `
                            <tr>
                                <td><b>${escapeHtml(a.id)}</b></td>
                                <td>${escapeHtml(a.name)}</td>
                                <td>${formatEnumLabel(a.category)}</td>
                                <td>${escapeHtml(a.roomNo)}</td>
                                <td><span class="badge-pill ${a.condition === 'GOOD' ? 'success' : 'danger'}">${formatEnumLabel(a.condition)}</span></td>
                                <td>${escapeHtml(a.lastAudit)}</td>
                                <td>
                                    <button class="btn btn-secondary btn-sm" onclick="flagAsset('${a.id}')">${getIconSvg('alertTriangle', 13)} Flag Damage</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// 6.12 NOTICE BOARD
function renderNoticePanel() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">Hostel Circulars & Announcements</h3>
                    <p class="card-subtitle">Official communications from Warden and Administration</p>
                </div>
                ${STORE.currentUser.role !== 'STUDENT' ? `<button class="btn btn-primary" onclick="openModal('ADD_NOTICE')">${getIconSvg('plus', 14)} Post Announcement</button>` : ''}
            </div>

            <div style="display: flex; flex-direction: column; gap: 16px;">
                ${STORE.notices.map(n => `
                    <div style="background: var(--bg-primary); padding: 18px; border-radius: var(--radius-lg); border: 1px solid var(--border-color);">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <span class="badge-pill ${n.priority === 'HIGH' ? 'danger' : 'primary'}">${formatEnumLabel(n.category)}</span>
                                <h3 style="font-size: 15px; font-weight: 700;">${escapeHtml(n.title)}</h3>
                            </div>
                            <small style="color: var(--text-muted);">${escapeHtml(n.date)} &bull; By ${escapeHtml(n.author)}</small>
                        </div>
                        <p style="font-size: 13px; color: var(--text-primary); line-height: 1.6;">${escapeHtml(n.content)}</p>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 6.13 MESS OPERATIONS
function renderMessPanel() {
    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Dining Menu Schedule</h3>
                    <p class="card-subtitle">${escapeHtml(STORE.messMenu.todayDate)}</p>
                </div>
                ${STORE.currentUser.role !== 'STUDENT' ? `<button class="btn btn-secondary" onclick="openModal('EDIT_MESS')">${getIconSvg('edit', 14)} Edit Weekly Menu</button>` : ''}
            </div>

            <div class="dual-layout">
                <div style="background: var(--bg-primary); padding: 16px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                    <h4 style="color: var(--color-primary); margin-bottom: 6px;">Breakfast (7:30 AM - 9:30 AM)</h4>
                    <p style="font-size: 13px; line-height: 1.5;">${escapeHtml(STORE.messMenu.breakfast)}</p>
                </div>
                <div style="background: var(--bg-primary); padding: 16px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                    <h4 style="color: var(--color-success); margin-bottom: 6px;">Lunch (12:30 PM - 2:30 PM)</h4>
                    <p style="font-size: 13px; line-height: 1.5;">${escapeHtml(STORE.messMenu.lunch)}</p>
                </div>
                <div style="background: var(--bg-primary); padding: 16px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                    <h4 style="color: var(--color-primary-dark); margin-bottom: 6px;">Evening Snacks (5:00 PM - 6:00 PM)</h4>
                    <p style="font-size: 13px; line-height: 1.5;">${escapeHtml(STORE.messMenu.snacks)}</p>
                </div>
                <div style="background: var(--bg-primary); padding: 16px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                    <h4 style="color: var(--color-primary); margin-bottom: 6px;">Dinner (7:30 PM - 9:30 PM)</h4>
                    <p style="font-size: 13px; line-height: 1.5;">${escapeHtml(STORE.messMenu.dinner)}</p>
                </div>
            </div>
        </div>

        <div class="card-panel">
            <div class="card-header-row">
                <h3 class="card-title">Student Food Quality Ratings</h3>
                <button class="btn btn-primary btn-sm" onclick="showToast('Thank you for rating today meal!', 'success')">Submit Meal Rating</button>
            </div>
            <p style="font-size: 13px; color: var(--text-secondary); line-height: 1.6;">Average satisfaction score for this week: <b>4.6 / 5.0</b> (Based on 142 student ratings).</p>
        </div>
    `;
}

// 6.14 NOTIFICATIONS
function renderNotificationsPanel() {
    return `
        <div class="card-panel">
            <div class="action-toolbar">
                <div>
                    <h3 class="card-title">System Notifications & Alerts</h3>
                    <p class="card-subtitle">Real-time dispatches from reactive event bus</p>
                </div>
                <button class="btn btn-secondary btn-sm" onclick="markAllNotifsRead()">Mark All Read</button>
            </div>

            <div style="display: flex; flex-direction: column; gap: 10px;">
                ${STORE.notifications.map(n => `
                    <div style="background: var(--bg-primary); padding: 14px; border-radius: var(--radius-md); border-left: 4px solid ${n.type === 'DANGER' ? 'var(--color-danger)' : (n.type === 'SUCCESS' ? 'var(--color-success)' : 'var(--color-primary)')}; border-top: 1px solid var(--border-color); border-right: 1px solid var(--border-color); border-bottom: 1px solid var(--border-color);">
                        <div style="display: flex; justify-content: space-between;">
                            <strong>${escapeHtml(n.title)}</strong>
                            <small style="color: var(--text-muted);">${escapeHtml(n.time)}</small>
                        </div>
                        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">${escapeHtml(n.desc)}</p>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 6.15 REPORTS & ANALYTICS
function renderReportsPanel() {
    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Cross-Module Data Intelligence</h3>
                    <p class="card-subtitle">Aggregate data metrics and reporting export suite</p>
                </div>
                <button class="btn btn-primary" onclick="exportDataCSV()">${getIconSvg('download', 14)} Export Comprehensive CSV</button>
            </div>

            <div class="kpi-grid" style="margin-bottom: 20px;">
                <div class="stat-card">
                    <span class="stat-label">Total Student Records</span>
                    <div class="stat-value">${STORE.students.length}</div>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Total Revenue</span>
                    <div class="stat-value">₹${(STORE.fees.reduce((a,b)=>a+b.paid,0)/1000).toFixed(1)}k</div>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Avg Water Usage</span>
                    <div class="stat-value">388 kL</div>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Resolved Complaints</span>
                    <div class="stat-value">92%</div>
                </div>
            </div>

            <p style="font-size: 13px; color: var(--text-secondary); line-height: 1.6;">Clicking <b>Export Comprehensive CSV</b> compiles all records across Student Demographics, Fee Collections, Outstation Leaves, Maintenance Logs, and Sustainability Resource Usages into a clean spreadsheet.</p>
        </div>
    `;
}

// 6.16 SETTINGS
function renderSettingsPanel() {
    return `
        <!-- Multi-Account User Management (Admin Exclusive) -->
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Manage Users & System Accounts</h3>
                    <p class="card-subtitle">Role-based administrative and operational access control</p>
                </div>
                <button class="btn btn-primary btn-sm" onclick="openModal('ADD_USER')">${getIconSvg('plus', 13)} Add New Account</button>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${STORE.users.map(u => `
                            <tr>
                                <td><code>${escapeHtml(u.id)}</code></td>
                                <td><b>${escapeHtml(u.fullName)}</b></td>
                                <td>${escapeHtml(u.username)}</td>
                                <td>
                                    <span class="badge-pill ${u.role === 'ADMIN' ? 'primary' : (u.role === 'WARDEN' ? 'warning' : 'success')}">
                                        ${formatEnumLabel(u.role)}
                                    </span>
                                </td>
                                <td>${escapeHtml(u.email)}</td>
                                <td>
                                    ${u.id === 'USR-001' ? '<span class="text-muted" style="font-size: 11px;">System Root</span>' : `<button class="btn btn-ghost btn-sm" style="color: var(--color-danger); padding: 2px 8px;" onclick="deleteUser('${u.id}')">${getIconSvg('x', 12)} Delete</button>`}
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">MySQL Database Configuration</h3>
                    <p class="card-subtitle">Connect to live enterprise database or continue with embedded seed store</p>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; max-width: 600px;">
                <div class="form-group">
                    <label class="form-label">Database Host</label>
                    <input type="text" class="form-control" value="localhost:3306">
                </div>
                <div class="form-group">
                    <label class="form-label">Database Name</label>
                    <input type="text" class="form-control" value="hostel_management_db">
                </div>
                <div class="form-group">
                    <label class="form-label">Username</label>
                    <input type="text" class="form-control" value="root">
                </div>
                <div class="form-group">
                    <label class="form-label">Password</label>
                    <input type="password" class="form-control" value="••••••••">
                </div>
            </div>

            <div style="margin-top: 14px; display: flex; gap: 10px;">
                <button class="btn btn-primary" onclick="showToast('Connection verified successfully (Zero latency fallback active)', 'success')">Test Connection</button>
                <button class="btn btn-secondary" onclick="showToast('Settings saved.', 'success')">Save Configuration</button>
            </div>
        </div>

        <div class="card-panel">
            <h3 class="card-title">Sustainability Anomaly Jump Threshold</h3>
            <p class="card-subtitle" style="margin-bottom: 14px;">Define percentage threshold for highlighting sudden energy and water spikes</p>
            <div style="display: flex; align-items: center; gap: 14px;">
                <input type="number" class="form-control" value="15" style="width: 120px;">
                <span>% MoM Increase</span>
                <button class="btn btn-secondary btn-sm" onclick="showToast('Threshold updated to 15%', 'success')">Update</button>
            </div>
        </div>
    `;
}

// 6.17 STUDENT PROFILE & FEES
function renderStudentProfile() {
    const sId = (STORE.currentUser && STORE.currentUser.studentId) ? STORE.currentUser.studentId : "STU-1001";
    const s = STORE.students.find(stu => stu.id === sId) || STORE.students[0];
    const sFee = STORE.fees.find(f => f.studentId === s.id) || STORE.fees[0];
    const hasDue = sFee && sFee.due > 0;

    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Personal Student Record</h3>
                    <p class="card-subtitle">Verified campus enrollment details and allocated quarters</p>
                </div>
                ${hasDue ? `<button class="btn btn-primary btn-sm" onclick="openRazorpayCheckout('${sFee.id}')">${getIconSvg('creditCard', 14)} Pay Fee Due (₹${sFee.due.toLocaleString()})</button>` : ''}
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 16px; font-size: 13px; line-height: 1.6;">
                <div><b>Full Name:</b> ${escapeHtml(s.name)}</div>
                <div><b>Roll No:</b> ${escapeHtml(s.roll)}</div>
                <div><b>Department:</b> ${escapeHtml(s.dept)}</div>
                <div><b>Year of Study:</b> Year ${s.year}</div>
                <div><b>Allocated Room:</b> ${escapeHtml(s.roomNo)}</div>
                <div><b>Bed Position:</b> ${escapeHtml(s.bed)}</div>
                <div><b>Emergency Contact:</b> ${escapeHtml(s.contact)}</div>
                <div><b>Email Address:</b> ${escapeHtml(s.email)}</div>
            </div>
        </div>
    `;
}

function renderStudentPersonalFees() {
    const sId = (STORE.currentUser && STORE.currentUser.studentId) ? STORE.currentUser.studentId : "STU-1001";
    const f = STORE.fees.find(fee => fee.studentId === sId) || STORE.fees[0];
    const isPaid = f.status === "PAID" || f.due === 0;

    return `
        <div class="card-panel">
            <div class="card-header-row">
                <div>
                    <h3 class="card-title">Fee Invoice & Payment Ledger</h3>
                    <p class="card-subtitle">Transparent academic term billing & real-time payment reconciliation</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    ${!isPaid ? `<button class="btn btn-primary" onclick="openRazorpayCheckout('${f.id}')">${getIconSvg('creditCard', 14)} Pay via Razorpay (Test Mode)</button>` : ''}
                    <button class="btn btn-secondary" onclick="printReceipt('${f.id}')">${getIconSvg('printer', 14)} Download Official Receipt</button>
                </div>
            </div>
            <div style="background: var(--bg-surface-elevated); border: 1px solid var(--border-color); padding: 20px; border-radius: var(--radius-lg); font-size: 13px;">
                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">
                    <div>
                        <strong style="font-size: 15px;">Academic Term: ${escapeHtml(f.term)}</strong>
                        <div style="color: var(--text-muted); font-size: 12px; margin-top: 2px;">Due Date: ${escapeHtml(f.dueDate)}</div>
                    </div>
                    <span class="badge-pill ${isPaid ? 'success' : 'warning'}">${formatEnumLabel(f.status)}</span>
                </div>
                <div style="margin-top: 14px; display: flex; flex-direction: column; gap: 8px; line-height: 1.6;">
                    <div style="display: flex; justify-content: space-between;">
                        <span>Hostel Accommodation & Utilities:</span>
                        <b>₹30,000</b>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                        <span>Mess & Dining Charges:</span>
                        <b>₹15,000</b>
                    </div>
                    <div style="border-top: 1px solid var(--border-color); padding-top: 10px; display: flex; justify-content: space-between; font-size: 14px;">
                        <span>Total Assessment:</span>
                        <b>₹${f.amount.toLocaleString()}</b>
                    </div>
                    <div style="display: flex; justify-content: space-between; color: var(--color-success);">
                        <span>Amount Paid so far:</span>
                        <b>₹${f.paid.toLocaleString()}</b>
                    </div>
                    <div style="border-top: 2px solid var(--border-color); padding-top: 10px; display: flex; justify-content: space-between; font-size: 16px; font-weight: 700; color: ${isPaid ? 'var(--color-success)' : 'var(--color-primary)'};">
                        <span>${isPaid ? 'Zero Balance Outstanding' : 'Outstanding Due Balance'}:</span>
                        <span>₹${f.due.toLocaleString()}</span>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// ==========================================
// 7. ACTIONS, MODALS & DISPATCH TRIGGERS
// ==========================================
function openModal(type, extraArg) {
    activeModalType = type;
    const overlay = document.getElementById("modal-overlay");
    const title = document.getElementById("modal-title");
    const sub = document.getElementById("modal-sub");
    const body = document.getElementById("modal-body");
    const primaryBtn = document.getElementById("modal-primary-btn");

    overlay.style.display = "flex";

    if (type === "ADD_UTILITY") {
        title.innerText = "Log Manual Utility Meter Read";
        sub.innerText = "Enter manual billing reads for electricity and water";
        primaryBtn.innerText = "Calculate & Log";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Billing Month</label>
                <input type="text" id="m-month" class="form-control" value="Sep 2026" required>
            </div>
            <div class="form-group">
                <label class="form-label">Electricity Consumption (kWh)</label>
                <input type="number" id="m-kwh" class="form-control" placeholder="e.g. 15200" value="18500" required>
            </div>
            <div class="form-group">
                <label class="form-label">Water Consumption (kL)</label>
                <input type="number" id="m-kl" class="form-control" placeholder="e.g. 380" value="440" required>
            </div>
        `;
    } else if (type === "ADD_STUDENT") {
        title.innerText = "Register New Student";
        sub.innerText = "Add resident details to student directory";
        primaryBtn.innerText = "Register Student";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Full Name</label>
                <input type="text" id="m-sname" class="form-control" placeholder="Student Name" value="Neha Sharma" required>
            </div>
            <div class="form-group">
                <label class="form-label">Roll Number</label>
                <input type="text" id="m-sroll" class="form-control" placeholder="e.g. 2024CS109" value="2024CS109" required>
            </div>
            <div class="form-group">
                <label class="form-label">Department & Year</label>
                <input type="text" id="m-sdept" class="form-control" value="Computer Science - Year 1" required>
            </div>
            <div class="form-group">
                <label class="form-label">Contact Phone</label>
                <input type="text" id="m-sphone" class="form-control" value="+91 98111 22334" required>
            </div>
        `;
    } else if (type === "RECORD_FEE") {
        title.innerText = "Record Fee Payment";
        sub.innerText = "Issue receipt and update balance";
        primaryBtn.innerText = "Record Payment";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Select Student</label>
                <select id="m-fstudent" class="form-control">
                    ${STORE.students.map(s => `<option value="${s.id}">${escapeHtml(s.name)} (${escapeHtml(s.roll)} - ${escapeHtml(s.roomNo)})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Payment Amount (₹)</label>
                <input type="number" id="m-famount" class="form-control" value="20000" required>
            </div>
            <div class="form-group">
                <label class="form-label">Payment Mode</label>
                <select id="m-fmode" class="form-control">
                    <option>UPI / Online Transfer</option>
                    <option>Bank Demand Draft</option>
                    <option>Cash Receipt</option>
                </select>
            </div>
        `;
    } else if (type === "APPLY_LEAVE") {
        title.innerText = "Apply for Outstation Leave";
        sub.innerText = "Submit leave request to hostel warden";
        primaryBtn.innerText = "Submit Application";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Departure Date</label>
                <input type="date" id="m-lfrom" class="form-control" value="2026-09-18" required>
            </div>
            <div class="form-group">
                <label class="form-label">Return Date</label>
                <input type="date" id="m-lto" class="form-control" value="2026-09-21" required>
            </div>
            <div class="form-group">
                <label class="form-label">Reason for Outstation Leave</label>
                <textarea id="m-lreason" class="form-control" rows="3" required>Family function in hometown.</textarea>
            </div>
        `;
    } else if (type === "LOG_COMPLAINT") {
        title.innerText = "Report Maintenance Issue";
        sub.innerText = "Submit room repair ticket";
        primaryBtn.innerText = "Log Ticket";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Category</label>
                <select id="m-ccat" class="form-control">
                    <option value="PLUMBING">Plumbing / Water Supply</option>
                    <option value="ELECTRICAL">Electrical & Lighting</option>
                    <option value="CARPENTRY">Carpentry & Furniture</option>
                    <option value="INTERNET">Wi-Fi & Network</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Room Number</label>
                <input type="text" id="m-croom" class="form-control" value="A-101" required>
            </div>
            <div class="form-group">
                <label class="form-label">Issue Details</label>
                <textarea id="m-cdesc" class="form-control" rows="3" required>Water pressure in washroom faucet is low.</textarea>
            </div>
        `;
    } else if (type === "VISITOR_CHECKIN") {
        title.innerText = "New Visitor Security Check-In";
        sub.innerText = "Issue visitor pass and entry time";
        primaryBtn.innerText = "Check-In Visitor";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Visitor Full Name</label>
                <input type="text" id="m-vname" class="form-control" value="Manish Mehta" required>
            </div>
            <div class="form-group">
                <label class="form-label">Relationship to Student</label>
                <input type="text" id="m-vrel" class="form-control" value="Father" required>
            </div>
            <div class="form-group">
                <label class="form-label">Visiting Student</label>
                <select id="m-vstu" class="form-control">
                    ${STORE.students.map(s => `<option value="${escapeHtml(s.name)}">${escapeHtml(s.name)} (${escapeHtml(s.roomNo)})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Contact Mobile</label>
                <input type="text" id="m-vphone" class="form-control" value="+91 98333 44556" required>
            </div>
        `;
    } else if (type === "ADD_NOTICE") {
        title.innerText = "Publish Notice / Circular";
        sub.innerText = "Broadcast to all hostel resident portals";
        primaryBtn.innerText = "Publish Notice";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Notice Title</label>
                <input type="text" id="m-ntitle" class="form-control" value="Hostel Cleanliness Inspection" required>
            </div>
            <div class="form-group">
                <label class="form-label">Category</label>
                <select id="m-ncat" class="form-control">
                    <option value="GENERAL">General Notice</option>
                    <option value="SUSTAINABILITY">Sustainability & Energy</option>
                    <option value="DISCIPLINE">Discipline & Security</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Notice Body</label>
                <textarea id="m-nbody" class="form-control" rows="3" required>Monthly cleanliness and safety audit will be conducted by warden team on Sunday.</textarea>
            </div>
        `;
    } else if (type === "ADD_USER") {
        title.innerText = "Add New System User Account";
        sub.innerText = "Create credentials for Administrator or Hostel Warden";
        primaryBtn.innerText = "Create Account";
        body.innerHTML = `
            <div class="form-group">
                <label class="form-label">Full Name</label>
                <input type="text" id="m-u-name" class="form-control" placeholder="e.g. Prof. Emily Watson" value="Prof. Emily Watson" required>
            </div>
            <div class="form-group">
                <label class="form-label">Email Address</label>
                <input type="email" id="m-u-email" class="form-control" placeholder="e.g. emily.w@hostel.edu" value="emily.w@hostel.edu" required>
            </div>
            <div class="form-group">
                <label class="form-label">Role</label>
                <select id="m-u-role" class="form-control">
                    <option value="WARDEN">Hostel Warden (Operations & Approvals)</option>
                    <option value="ADMIN">Administrator (Full System Access)</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label">Username</label>
                <input type="text" id="m-u-user" class="form-control" placeholder="e.g. emily_w" value="emily_w" required>
            </div>
            <div class="form-group">
                <label class="form-label">Password</label>
                <input type="password" id="m-u-pass" class="form-control" value="pass123" required>
            </div>
        `;
    }
}

function closeModal() {
    document.getElementById("modal-overlay").style.display = "none";
    activeModalType = null;
}

function submitActiveModal() {
    if (activeModalType === "ADD_USER") {
        const uName = document.getElementById("m-u-name").value.trim();
        const uEmail = document.getElementById("m-u-email").value.trim();
        const uRole = document.getElementById("m-u-role").value;
        const uUser = document.getElementById("m-u-user").value.trim().toLowerCase();
        const uPass = document.getElementById("m-u-pass").value.trim();

        if (STORE.users.some(u => u.username.toLowerCase() === uUser)) {
            showToast("Username already taken. Please choose another.", "danger");
            return;
        }

        const newUser = {
            id: `USR-00${STORE.users.length + 1}`,
            username: uUser,
            role: uRole,
            fullName: uName,
            email: uEmail,
            pass: uPass
        };
        STORE.users.push(newUser);
        saveUsersToStorage();

        closeModal();
        if (document.getElementById("content-area") && currentModuleId === "SETTINGS") {
            document.getElementById("content-area").innerHTML = getModuleHtml("SETTINGS");
        }
        showToast(`Account created for ${uName} (${formatEnumLabel(uRole)})!`, "success");
    } else if (activeModalType === "ADD_UTILITY") {
        const month = document.getElementById("m-month").value;
        const kwh = parseFloat(document.getElementById("m-kwh").value);
        const kl = parseFloat(document.getElementById("m-kl").value);

        const last = STORE.sustainability[STORE.sustainability.length - 1];
        const eChange = ((kwh - last.electricityKwh) / last.electricityKwh) * 100;
        const wChange = ((kl - last.waterKl) / last.waterKl) * 100;
        const isAnomaly = eChange > 15.0 || wChange > 15.0;

        const newRecord = { month, electricityKwh: kwh, waterKl: kl, eChange, wChange, isAnomaly };
        STORE.sustainability.push(newRecord);

        dispatchAppEvent("USAGE_LOGGED", newRecord);
        closeModal();
        showToast(isAnomaly ? "Logged! Spikes exceeded 15% anomaly threshold." : "Utility log recorded successfully.", isAnomaly ? "warning" : "success");
    } else if (activeModalType === "ADD_STUDENT") {
        const name = document.getElementById("m-sname").value;
        const roll = document.getElementById("m-sroll").value;
        const dept = document.getElementById("m-sdept").value;
        const contact = document.getElementById("m-sphone").value;

        const newStudent = {
            id: `STU-${1000 + STORE.students.length + 1}`,
            roll, name, dept, year: 1, roomNo: "B-201", bed: "Bed-B", contact, email: `${name.toLowerCase().replace(/\s+/g, '.')}@hostel.edu`, status: "ACTIVE"
        };
        STORE.students.push(newStudent);
        saveStudentsToStorage();

        const autoUsername = roll.toLowerCase().replace(/[^a-z0-9]/g, '');
        const studentUser = {
            id: `USR-00${STORE.users.length + 1}`,
            username: autoUsername,
            role: "STUDENT",
            fullName: name,
            email: newStudent.email,
            pass: "student123",
            studentId: newStudent.id
        };
        STORE.users.push(studentUser);
        saveUsersToStorage();

        dispatchAppEvent("STUDENT_REGISTERED", newStudent);
        closeModal();
        showToast(`Registered ${name}! Login created (User: ${autoUsername}, Pass: student123)`, "success");
    } else if (activeModalType === "RECORD_FEE") {
        const stuId = document.getElementById("m-fstudent").value;
        const amount = parseFloat(document.getElementById("m-famount").value);
        const stu = STORE.students.find(s => s.id === stuId);

        const fee = STORE.fees.find(f => f.studentId === stuId);
        if (fee) {
            fee.paid += amount;
            fee.due = Math.max(0, fee.amount - fee.paid);
            fee.status = fee.due === 0 ? "PAID" : "PARTIAL";
        }

        dispatchAppEvent("FEE_PAID", { studentName: stu.name, amount });
        closeModal();
        showToast(`Recorded payment of ₹${amount.toLocaleString()} for ${stu.name}.`, "success");
    } else if (activeModalType === "APPLY_LEAVE") {
        const fromDate = document.getElementById("m-lfrom").value;
        const toDate = document.getElementById("m-lto").value;
        const reason = document.getElementById("m-lreason").value;

        const newLeave = {
            id: `LV-${100 + STORE.leaves.length + 1}`,
            studentId: "STU-1001",
            studentName: "Aarav Sharma",
            roomNo: "A-101",
            fromDate, toDate, reason,
            status: "PENDING",
            appliedOn: "Just now",
            remarks: ""
        };
        STORE.leaves.unshift(newLeave);

        dispatchAppEvent("LEAVE_APPLIED", newLeave);
        closeModal();
        showToast("Leave application submitted to warden.", "success");
    } else if (activeModalType === "LOG_COMPLAINT") {
        const category = document.getElementById("m-ccat").value;
        const roomNo = document.getElementById("m-croom").value;
        const desc = document.getElementById("m-cdesc").value;

        const newCmp = {
            id: `CMP-0${STORE.complaints.length + 1}`,
            studentName: STORE.currentUser.fullName,
            roomNo, category, desc,
            priority: "HIGH",
            status: "PENDING",
            reportedOn: "Today",
            assignedTo: "Unassigned"
        };
        STORE.complaints.unshift(newCmp);

        dispatchAppEvent("COMPLAINT_LOGGED", newCmp);
        closeModal();
        showToast("Maintenance ticket logged.", "success");
    } else if (activeModalType === "VISITOR_CHECKIN") {
        const name = document.getElementById("m-vname").value;
        const relation = document.getElementById("m-vrel").value;
        const studentName = document.getElementById("m-vstu").value;
        const contact = document.getElementById("m-vphone").value;

        const newVis = {
            id: `VIS-0${STORE.visitors.length + 1}`,
            passNo: `VP-${900 + STORE.visitors.length + 1}`,
            name, relation, studentName, contact,
            checkIn: "Just now",
            checkOut: null,
            status: "INSIDE"
        };
        STORE.visitors.unshift(newVis);

        dispatchAppEvent("VISITOR_CHECKIN", newVis);
        closeModal();
        showToast(`Visitor pass ${newVis.passNo} issued for ${name}.`, "success");
    } else if (activeModalType === "ADD_NOTICE") {
        const title = document.getElementById("m-ntitle").value;
        const category = document.getElementById("m-ncat").value;
        const content = document.getElementById("m-nbody").value;

        const newNotice = {
            id: `NOT-0${STORE.notices.length + 1}`,
            title, category, priority: "MEDIUM",
            date: "Today",
            content,
            author: STORE.currentUser.fullName
        };
        STORE.notices.unshift(newNotice);

        dispatchAppEvent("NOTICE_POSTED", newNotice);
        closeModal();
        showToast("Notice circular published.", "success");
    }
}

// 7.1 Specific quick actions
function triageLeave(id, newStatus) {
    const lv = STORE.leaves.find(l => l.id === id);
    if (lv) {
        lv.status = newStatus;
        lv.remarks = newStatus === "APPROVED" ? "Approved by warden on duty." : "Rejected due to academic schedule clash.";
        dispatchAppEvent("LEAVE_STATUS_CHANGED", lv);
        showToast(`Leave application marked ${formatEnumLabel(newStatus)}.`, newStatus === "APPROVED" ? "success" : "danger");
    }
}

function resolveComplaint(id) {
    const c = STORE.complaints.find(cmp => cmp.id === id);
    if (c) {
        c.status = "RESOLVED";
        dispatchAppEvent("COMPLAINT_STATUS_CHANGED", c);
        showToast(`Complaint #${c.id} marked Resolved.`, "success");
    }
}

function checkoutVisitor(id) {
    const v = STORE.visitors.find(vis => vis.id === id);
    if (v) {
        v.status = "CHECKED_OUT";
        v.checkOut = "Just now";
        dispatchAppEvent("VISITOR_CHECKOUT", v);
        showToast(`Visitor ${v.name} checked out.`, "info");
    }
}

function flagAsset(id) {
    const a = STORE.assets.find(ast => ast.id === id);
    if (a) {
        a.condition = "NEEDS_SERVICE";
        dispatchAppEvent("ASSET_DAMAGED", a);
        showToast(`Damage reported for asset #${a.id}.`, "warning");
    }
}

function printReceipt(feeId) {
    const f = STORE.fees.find(fee => fee.id === feeId);
    showToast(`Generating official PDF receipt for ${f.studentName} (₹${f.paid.toLocaleString()})...`, "success");
}

function exportDataCSV() {
    showToast("Compiling cross-module records into hostel_management_export.csv...", "success");
}

// ==========================================
// 8. HEADER DROPDOWNS & TOAST UTILITIES
// ==========================================
function toggleNotifDropdown() {
    const dd = document.getElementById("notif-dropdown");
    const isShowing = dd.style.display === "block";
    dd.style.display = isShowing ? "none" : "block";

    if (!isShowing) {
        document.getElementById("profile-dropdown").style.display = "none";
        renderNotifDropdownList();
    }
}

function renderNotifDropdownList() {
    const list = document.getElementById("notif-dropdown-list");
    list.innerHTML = STORE.notifications.slice(0, 5).map(n => `
        <div class="notif-item ${!n.read ? 'unread' : ''}">
            <strong style="color: ${n.type === 'DANGER' ? 'var(--color-danger)' : 'var(--text-primary)'};">${escapeHtml(n.title)}</strong>
            <p style="margin: 2px 0; color: var(--text-secondary);">${escapeHtml(n.desc)}</p>
            <div class="notif-meta"><span>${escapeHtml(n.time)}</span></div>
        </div>
    `).join('');
}

function updateNotificationBadge() {
    const unreadCount = STORE.notifications.filter(n => !n.read).length;
    const badge = document.getElementById("header-notif-badge");
    if (badge) {
        if (unreadCount > 0) {
            badge.innerText = unreadCount;
            badge.style.display = "block";
        } else {
            badge.style.display = "none";
        }
    }
}

function markAllNotifsRead() {
    STORE.notifications.forEach(n => n.read = true);
    updateNotificationBadge();
    if (document.getElementById("content-area")) {
        document.getElementById("content-area").innerHTML = getModuleHtml(currentModuleId);
    }
    showToast("All notifications marked as read.", "info");
}

function toggleProfileDropdown() {
    const dd = document.getElementById("profile-dropdown");
    const isShowing = dd.style.display === "block";
    dd.style.display = isShowing ? "none" : "block";
    if (!isShowing) {
        document.getElementById("notif-dropdown").style.display = "none";
    }
}

function showToast(msg, type = "success") {
    const toast = document.getElementById("toast");
    const icon = document.getElementById("toast-icon");
    const message = document.getElementById("toast-message");

    let iconSvg = getIconSvg('check', 16);
    if (type === "danger") iconSvg = getIconSvg('x', 16);
    else if (type === "warning") iconSvg = getIconSvg('alertTriangle', 16);
    else if (type === "info") iconSvg = getIconSvg('bell', 16);

    icon.innerHTML = iconSvg;
    message.innerText = msg;
    toast.style.borderColor = type === "danger" ? "var(--color-danger)" : (type === "warning" ? "var(--color-warning)" : "var(--color-success)");
    toast.style.display = "flex";

    setTimeout(() => {
        toast.style.display = "none";
    }, 3500);
}

// ==========================================
// 9. USER ACCOUNT MANAGEMENT (ADMIN)
// ==========================================
function deleteUser(userId) {
    if (userId === "USR-001") {
        showToast("Cannot delete primary system administrator.", "danger");
        return;
    }
    if (STORE.currentUser && STORE.currentUser.id === userId) {
        showToast("Cannot delete your own active session account.", "danger");
        return;
    }
    const idx = STORE.users.findIndex(u => u.id === userId);
    if (idx !== -1) {
        const removed = STORE.users.splice(idx, 1)[0];
        saveUsersToStorage();
        if (document.getElementById("content-area") && currentModuleId === "SETTINGS") {
            document.getElementById("content-area").innerHTML = getModuleHtml("SETTINGS");
        }
        showToast(`Account deleted: ${removed.fullName} (${removed.username})`, "info");
    }
}

// ==========================================
// 10. SIMULATED RAZORPAY PAYMENT GATEWAY
// ==========================================
let currentPayingFeeId = null;
let currentRzpTab = "UPI";

function openRazorpayCheckout(feeId) {
    const fee = STORE.fees.find(f => f.id === feeId);
    if (!fee) return;
    currentPayingFeeId = feeId;
    currentRzpTab = "UPI";

    const overlay = document.getElementById("razorpay-modal-overlay");
    const amountVal = document.getElementById("rzp-amount-value");
    const submitBtn = document.getElementById("rzp-pay-submit-btn");

    amountVal.innerText = `₹${fee.due.toLocaleString()}`;
    submitBtn.innerText = `Pay ₹${fee.due.toLocaleString()} (Test Mode) \u2192`;
    submitBtn.disabled = false;

    document.getElementById("rzp-body-content").style.display = "block";
    document.getElementById("rzp-receipt-content").style.display = "none";

    switchRzpTab("UPI");
    overlay.style.display = "flex";
}

function closeRazorpayModal() {
    const overlay = document.getElementById("razorpay-modal-overlay");
    if (overlay) overlay.style.display = "none";
    currentPayingFeeId = null;
}

function switchRzpTab(tab) {
    currentRzpTab = tab;
    const tabs = document.querySelectorAll(".rzp-tab-btn");
    tabs.forEach(t => t.classList.remove("active"));

    const upiPanel = document.getElementById("rzp-tab-upi");
    const cardPanel = document.getElementById("rzp-tab-card");
    const nbPanel = document.getElementById("rzp-tab-netbanking");

    if (upiPanel) upiPanel.style.display = "none";
    if (cardPanel) cardPanel.style.display = "none";
    if (nbPanel) nbPanel.style.display = "none";

    if (tab === "UPI") {
        if (upiPanel) upiPanel.style.display = "block";
        if (tabs[0]) tabs[0].classList.add("active");
    } else if (tab === "CARD") {
        if (cardPanel) cardPanel.style.display = "block";
        if (tabs[1]) tabs[1].classList.add("active");
    } else {
        if (nbPanel) nbPanel.style.display = "block";
        if (tabs[2]) tabs[2].classList.add("active");
    }
}

function processRazorpayPayment() {
    const fee = STORE.fees.find(f => f.id === currentPayingFeeId);
    if (!fee) return;

    const btn = document.getElementById("rzp-pay-submit-btn");
    btn.disabled = true;
    btn.innerHTML = `<span style="display: inline-block;">Processing Test Payment...</span>`;

    setTimeout(() => {
        const payAmount = fee.due;
        const txnId = "pay_test_" + Math.random().toString(36).substring(2, 11).toUpperCase();

        fee.paid += payAmount;
        fee.due = 0;
        fee.status = "PAID";

        dispatchAppEvent("FEE_PAID", { studentName: fee.studentName, amount: payAmount });

        document.getElementById("rzp-body-content").style.display = "none";
        const rcpt = document.getElementById("rzp-receipt-content");
        document.getElementById("rcpt-pay-id").innerText = txnId;
        document.getElementById("rcpt-amount").innerText = `₹${payAmount.toLocaleString()}`;
        document.getElementById("rcpt-method").innerText = currentRzpTab === "UPI" ? "UPI (student@okhdfcbank)" : (currentRzpTab === "CARD" ? "Visa Card ending in 4444" : "NetBanking (HDFC Bank)");
        document.getElementById("rcpt-timestamp").innerText = new Date().toLocaleString();
        rcpt.style.display = "block";
    }, 1200);
}

function finishRazorpaySuccess() {
    closeRazorpayModal();
    if (document.getElementById("content-area")) {
        document.getElementById("content-area").innerHTML = getModuleHtml(currentModuleId);
    }
    showToast("Fee payment verified & settled via Razorpay!", "success");
}

// Global initialization
window.addEventListener("DOMContentLoaded", () => {
    initTheme();
});

// Close modals & dropdowns on escape
window.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        closeModal();
        closeRazorpayModal();
        document.getElementById("notif-dropdown").style.display = "none";
        document.getElementById("profile-dropdown").style.display = "none";
    }
});

// Run theme init immediately in case DOM is already loaded
initTheme();
