package com.hostel.ui.shell;

import com.hostel.db.DatabaseManager;
import com.hostel.model.User;
import com.hostel.ui.modules.admin.*;
import com.hostel.ui.modules.compliance.ComplianceMaintenancePanel;
import com.hostel.ui.modules.fee.HostelFeeManagementPanel;
import com.hostel.ui.modules.inventory.InventoryAssetPanel;
import com.hostel.ui.modules.leave.LeaveManagementPanel;
import com.hostel.ui.modules.mess.MessManagementPanel;
import com.hostel.ui.modules.notice.NoticeManagementPanel;
import com.hostel.ui.modules.notification.NotificationsPanel;
import com.hostel.ui.modules.reports.ReportsPanel;
import com.hostel.ui.modules.room.RoomAllocationPanel;
import com.hostel.ui.modules.student.*;
import com.hostel.ui.modules.studentmgmt.StudentManagementPanel;
import com.hostel.ui.modules.sustainability.SustainabilityPanel;
import com.hostel.ui.modules.visitor.VisitorManagementPanel;
import com.hostel.ui.modules.warden.WardenDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainAppShell extends JPanel {
    private final User currentUser;
    private final Runnable onLogout;
    private final Consumer<User.Role> onQuickSwitchRole;

    private AppHeader header;
    private AppSidebar sidebar;
    private JPanel contentContainer;
    private CardLayout cardLayout;

    // Module References
    private StudentManagementPanel studentMgmtPanel;
    private RoomAllocationPanel roomAllocPanel;
    private HostelFeeManagementPanel feeMgmtPanel;
    private VisitorManagementPanel visitorMgmtPanel;
    private LeaveManagementPanel leaveMgmtPanel;
    private ComplianceMaintenancePanel compliancePanel;
    private SustainabilityPanel sustainabilityPanel;
    private InventoryAssetPanel inventoryPanel;
    private NoticeManagementPanel noticePanel;
    private MessManagementPanel messPanel;
    private NotificationsPanel notifsPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;

    public MainAppShell(User user, Runnable onLogout, Consumer<User.Role> onQuickSwitchRole) {
        this.currentUser = user;
        this.onLogout = onLogout;
        this.onQuickSwitchRole = onQuickSwitchRole;

        setLayout(new BorderLayout());

        // Header
        header = new AppHeader(user, this::navigateTo, onLogout, onQuickSwitchRole);

        // Sidebar
        sidebar = new AppSidebar(user, this::navigateTo, onLogout);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);

        initModules();

        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(contentContainer, BorderLayout.CENTER);

        navigateTo("DASHBOARD");
    }

    private void initModules() {
        // Dashboards per role
        if (currentUser.getRole() == User.Role.ADMIN) {
            AdminDashboardPanel adminDash = new AdminDashboardPanel(currentUser, this::navigateTo,
                    () -> { if (studentMgmtPanel != null) { navigateTo("STUDENTS"); studentMgmtPanel.openAddStudentDialog(); } },
                    () -> { if (roomAllocPanel != null) { navigateTo("ROOMS"); roomAllocPanel.openAllocateDialog(); } },
                    () -> { if (visitorMgmtPanel != null) { navigateTo("VISITORS"); visitorMgmtPanel.openAddVisitorDialog(); } },
                    () -> { if (sustainabilityPanel != null) { navigateTo("SUSTAINABILITY"); sustainabilityPanel.openAddUsageDialog(); } },
                    () -> { if (noticePanel != null) { navigateTo("NOTICES"); } }
            );
            contentContainer.add(adminDash, "DASHBOARD");
        } else if (currentUser.getRole() == User.Role.WARDEN) {
            WardenDashboardPanel wardenDash = new WardenDashboardPanel(currentUser, this::navigateTo,
                    lr -> { if (leaveMgmtPanel != null) { navigateTo("LEAVES"); leaveMgmtPanel.openLeaveReviewDialog(lr); } },
                    comp -> { if (compliancePanel != null) { navigateTo("COMPLIANCE"); compliancePanel.openManageComplaintDialog(comp); } }
            );
            contentContainer.add(wardenDash, "DASHBOARD");
        } else {
            StudentDashboardPanel studentDash = new StudentDashboardPanel(currentUser, this::navigateTo,
                    () -> { if (leaveMgmtPanel != null) { navigateTo("STUDENT_LEAVES"); leaveMgmtPanel.openApplyLeaveDialog(); } },
                    () -> { if (compliancePanel != null) { navigateTo("STUDENT_COMPLAINTS"); compliancePanel.openFileComplaintDialog(); } },
                    () -> { navigateTo("STUDENT_FEES"); }
            );
            contentContainer.add(studentDash, "DASHBOARD");
        }

        // Shared / Admin / Warden Modules
        studentMgmtPanel = new StudentManagementPanel(currentUser);
        contentContainer.add(studentMgmtPanel, "STUDENTS");

        roomAllocPanel = new RoomAllocationPanel(currentUser);
        contentContainer.add(roomAllocPanel, "ROOMS");

        feeMgmtPanel = new HostelFeeManagementPanel(currentUser);
        contentContainer.add(feeMgmtPanel, "FEES");

        visitorMgmtPanel = new VisitorManagementPanel(currentUser);
        contentContainer.add(visitorMgmtPanel, "VISITORS");

        leaveMgmtPanel = new LeaveManagementPanel(currentUser);
        contentContainer.add(leaveMgmtPanel, "LEAVES");

        compliancePanel = new ComplianceMaintenancePanel(currentUser);
        contentContainer.add(compliancePanel, "COMPLIANCE");

        sustainabilityPanel = new SustainabilityPanel(currentUser);
        contentContainer.add(sustainabilityPanel, "SUSTAINABILITY");

        inventoryPanel = new InventoryAssetPanel(currentUser);
        contentContainer.add(inventoryPanel, "INVENTORY");

        noticePanel = new NoticeManagementPanel(currentUser);
        contentContainer.add(noticePanel, "NOTICES");

        messPanel = new MessManagementPanel(currentUser);
        contentContainer.add(messPanel, "MESS");

        notifsPanel = new NotificationsPanel(currentUser);
        contentContainer.add(notifsPanel, "NOTIFICATIONS");

        reportsPanel = new ReportsPanel(currentUser);
        contentContainer.add(reportsPanel, "REPORTS");

        settingsPanel = new SettingsPanel(currentUser);
        contentContainer.add(settingsPanel, "SETTINGS");

        // Student Scoped Views
        contentContainer.add(new StudentProfilePanel(currentUser, StudentProfilePanel.Mode.PROFILE_ONLY), "STUDENT_PROFILE");
        contentContainer.add(new StudentProfilePanel(currentUser, StudentProfilePanel.Mode.ROOM_ONLY), "STUDENT_ROOM");
        contentContainer.add(new StudentProfilePanel(currentUser, StudentProfilePanel.Mode.FEES_ONLY), "STUDENT_FEES");
        contentContainer.add(leaveMgmtPanel, "STUDENT_LEAVES");
        contentContainer.add(compliancePanel, "STUDENT_COMPLAINTS");
        contentContainer.add(visitorMgmtPanel, "STUDENT_VISITORS");
        contentContainer.add(noticePanel, "STUDENT_NOTICES");
        contentContainer.add(messPanel, "STUDENT_MESS");
    }

    public void navigateTo(String moduleKey) {
        if (moduleKey == null) return;
        cardLayout.show(contentContainer, moduleKey);
        sidebar.setActiveNav(moduleKey);

        String title = "Dashboard Overview";
        String sub = "Smart Sustainable Hostel Management System";

        switch (moduleKey) {
            case "DASHBOARD":
                title = currentUser.getRole().getDisplayName() + " Executive Dashboard";
                sub = "Campus hostel operations, metrics overview, and today's priority actions";
                break;
            case "STUDENTS":
                title = currentUser.getRole() == User.Role.ADMIN ? "Student Management & Enrollment" : "Student Directory";
                sub = "Resident database, academic details, room mapping, and enrollment profiles";
                break;
            case "ROOMS":
                title = "Bed Allocation & Wing Management";
                sub = "Real-time room occupancy, block allocations, vacant beds and transfer requests";
                break;
            case "FEES":
                title = "Hostel Fee & Finance Management";
                sub = "Semester fee billing, payment receipts, installment schedules, and collection analytics";
                break;
            case "VISITORS":
            case "STUDENT_VISITORS":
                title = "Visitor & Gate Security Log";
                sub = "Live gate entry registration, visitor presence tracking, and check-out management";
                break;
            case "LEAVES":
            case "STUDENT_LEAVES":
                title = "Leave Pass & Outstation Approvals";
                sub = "Student leave applications, parent verification records, and approved gate passes";
                break;
            case "COMPLIANCE":
            case "STUDENT_COMPLAINTS":
                title = "Compliance & Facility Maintenance Hub";
                sub = "Maintenance repair tickets (Pending → In Progress → Resolved) and disciplinary audits";
                break;
            case "SUSTAINABILITY":
                title = "⚡ Electricity & Water Sustainability Monitoring";
                sub = "Software-only manual resource tracking, consumption trends, and surge jump detection";
                break;
            case "INVENTORY":
                title = "Inventory & Physical Asset Registry";
                sub = "Hostel furniture, electrical equipment, gym items, and damage inspection reports";
                break;
            case "NOTICES":
            case "STUDENT_NOTICES":
                title = "Official Notice Board & Circulars";
                sub = "Published announcements, hostel circulars, curfew advisories, and emergency alerts";
                break;
            case "MESS":
            case "STUDENT_MESS":
                title = "Dining & Mess Management";
                sub = "Daily dining menus, weekly dietary rotation, and resident food quality ratings";
                break;
            case "NOTIFICATIONS":
                title = "Notification & Alert Center";
                sub = "System advisories, leave updates, payment receipts, and maintenance tickets";
                break;
            case "REPORTS":
                title = "Executive Hostel Analytics & Reports";
                sub = "Consolidated multi-module intelligence, occupancy meters, and sustainability audits";
                break;
            case "SETTINGS":
                title = "System Settings & Configuration";
                sub = "Hostel configuration, database connectivity endpoints, and anomaly thresholds";
                break;
            case "STUDENT_PROFILE":
                title = "My Resident Profile";
                sub = "Personal details, department info, blood group, and emergency parent contacts";
                break;
            case "STUDENT_ROOM":
                title = "My Room & Suite Allocation";
                sub = "Assigned room number, block wing, floor, room amenities, and roommates";
                break;
            case "STUDENT_FEES":
                title = "My Fees & Payment Receipts";
                sub = "Hostel rent, mess charges, utility deposit, and transaction payment history";
                break;
        }

        header.setPageTitle(title, sub);
    }
}
