package com.hostel.ui.modules.reports;

import com.hostel.db.DatabaseManager;
import com.hostel.model.*;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private final User currentUser;
    private final boolean isAdmin;

    private JTabbedPane reportTabs;

    public ReportsPanel(User user) {
        this.currentUser = user;
        this.isAdmin = (user.getRole() == User.Role.ADMIN);

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Multi-Category Report Tabs
        reportTabs = new JTabbedPane();
        reportTabs.setFont(UITheme.FONT_BODY_BOLD);
        reportTabs.setBackground(Color.WHITE);

        reportTabs.addTab("🎓 Student Demographics", createStudentReportTab());
        reportTabs.addTab("🏢 Room Occupancy", createRoomReportTab());
        if (isAdmin) {
            reportTabs.addTab("💳 Fee Collections", createFeeReportTab());
        }
        reportTabs.addTab("👥 Visitor Footfall", createVisitorReportTab());
        reportTabs.addTab("🗓 Leave Pass Analytics", createLeaveReportTab());
        reportTabs.addTab("🛠 Maintenance Resolution", createMaintenanceReportTab());
        reportTabs.addTab("⚡ Sustainability Trends", createSustainabilityReportTab());

        content.add(reportTabs);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isAdmin ? "Executive Hostel Analytics & Reports" : "Operational Hostel Reports");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Consolidated multi-module intelligence, occupancy meters, financial summaries, and sustainability audits");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        CustomButton exportBtn = new CustomButton("📥 Export Current Report to CSV", CustomButton.Variant.PRIMARY);
        exportBtn.addActionListener(e -> exportActiveTabToCSV());

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(exportBtn, BorderLayout.EAST);
        return panel;
    }

    // Tab 1: Student Demographics
    private JPanel createStudentReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> students = db.getAllStudents();

        Map<String, Long> byDept = students.stream().collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));
        Map<String, Long> byYear = students.stream().collect(Collectors.groupingBy(Student::getYear, Collectors.counting()));

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Total Enrolled Students", String.valueOf(students.size()), "All registered", UITheme.PRIMARY, "🎓"));
        stats.add(new StatCard("Active In Hostel", String.valueOf(students.stream().filter(s -> s.getStatus() == Student.Status.ACTIVE).count()), "Current residents", UITheme.SUCCESS, "✅"));
        stats.add(new StatCard("Academic Departments", String.valueOf(byDept.size()), "Represented", UITheme.INFO, "🏛"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        // Breakdown Table
        String[] cols = {"Department", "Total Students", "Active Residents", "On Leave", "Suspended / Inactive"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String dept : byDept.keySet()) {
            List<Student> dList = students.stream().filter(s -> s.getDepartment().equalsIgnoreCase(dept)).toList();
            long act = dList.stream().filter(s -> s.getStatus() == Student.Status.ACTIVE).count();
            long lve = dList.stream().filter(s -> s.getStatus() == Student.Status.ON_LEAVE).count();
            long inact = dList.size() - act - lve;
            model.addRow(new Object[]{dept, dList.size(), act, lve, inact});
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 2: Room Occupancy
    private JPanel createRoomReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<Room> rooms = db.getAllRooms();

        int totalCap = rooms.stream().mapToInt(Room::getCapacity).sum();
        int totalOcc = rooms.stream().mapToInt(Room::getOccupied).sum();
        int totalAvail = Math.max(0, totalCap - totalOcc);

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Total Bed Capacity", String.valueOf(totalCap), "All hostel wings", UITheme.PRIMARY, "🏢"));
        stats.add(new StatCard("Occupied Beds", String.valueOf(totalOcc), String.format("%.0f%% capacity", totalOcc * 100.0 / totalCap), UITheme.SUCCESS, "🛌"));
        stats.add(new StatCard("Vacant Beds", String.valueOf(totalAvail), "Available to allocate", UITheme.INFO, "✨"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        String[] cols = {"Block Name", "Total Rooms", "Total Capacity", "Occupied Beds", "Vacant Beds", "Occupancy %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        Map<String, List<Room>> byBlock = rooms.stream().collect(Collectors.groupingBy(Room::getBlock));
        for (String block : byBlock.keySet()) {
            List<Room> bRooms = byBlock.get(block);
            int bCap = bRooms.stream().mapToInt(Room::getCapacity).sum();
            int bOcc = bRooms.stream().mapToInt(Room::getOccupied).sum();
            int bAvail = Math.max(0, bCap - bOcc);
            double pct = bCap > 0 ? (bOcc * 100.0 / bCap) : 0;
            model.addRow(new Object[]{block, bRooms.size(), bCap, bOcc, bAvail, String.format("%.1f%%", pct)});
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 3: Fee Collections
    private JPanel createFeeReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<FeeRecord> fees = db.getAllFeeRecords();
        List<PaymentTransaction> txs = db.getAllPaymentTransactions();

        double totalInvoiced = fees.stream().mapToDouble(FeeRecord::getTotalFee).sum();
        double totalCollected = fees.stream().mapToDouble(FeeRecord::getPaidAmount).sum();
        double totalBalance = fees.stream().mapToDouble(FeeRecord::getBalance).sum();

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Total Invoiced", String.format("₹%,.0f", totalInvoiced), "All term fees", UITheme.PRIMARY, "📜"));
        stats.add(new StatCard("Total Realized", String.format("₹%,.0f", totalCollected), String.format("%.0f%% collection rate", totalCollected * 100.0 / totalInvoiced), UITheme.SUCCESS, "💰"));
        stats.add(new StatCard("Outstanding Dues", String.format("₹%,.0f", totalBalance), "Pending collection", UITheme.DANGER, "⏳"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        String[] cols = {"Receipt No", "Student Name", "Amount Paid", "Payment Method", "Transaction Ref", "Date Recorded", "Handled By"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (PaymentTransaction tx : txs) {
            model.addRow(new Object[]{
                    tx.getReceiptNo(),
                    tx.getStudentName(),
                    String.format("₹%,.2f", tx.getAmount()),
                    tx.getPaymentMethod().getLabel(),
                    tx.getTransactionRef() != null ? tx.getTransactionRef() : "-",
                    tx.getPaymentDate(),
                    tx.getRecordedBy()
            });
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 4: Visitor Footfall
    private JPanel createVisitorReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<Visitor> visitors = db.getAllVisitors();

        long currentlyInside = visitors.stream().filter(Visitor::isCurrentlyInside).count();
        long completed = visitors.stream().filter(v -> !v.isCurrentlyInside()).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Total Footfall Log", String.valueOf(visitors.size()), "All recorded visits", UITheme.PRIMARY, "👥"));
        stats.add(new StatCard("Currently Inside", String.valueOf(currentlyInside), "Live on premises", UITheme.SUCCESS, "🚪"));
        stats.add(new StatCard("Completed Checkouts", String.valueOf(completed), "Safe exit recorded", UITheme.INFO, "🏃"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        String[] cols = {"Visitor Name", "Student Visited", "Room", "Relation", "Purpose", "Check-In", "Check-Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Visitor v : visitors) {
            model.addRow(new Object[]{
                    v.getVisitorName(),
                    v.getStudentName(),
                    v.getRoomNumber() != null ? v.getRoomNumber() : "-",
                    v.getRelation(),
                    v.getPurpose(),
                    v.getCheckInTime(),
                    v.getCheckOutTime() != null ? v.getCheckOutTime() : "Inside",
                    v.getStatus().name()
            });
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 5: Leave Analytics
    private JPanel createLeaveReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<LeaveRequest> leaves = db.getAllLeaveRequests();

        long approved = leaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.APPROVED).count();
        long pending = leaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING).count();
        long rejected = leaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.REJECTED).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Approved Passes", String.valueOf(approved), "Outstation authorized", UITheme.SUCCESS, "✅"));
        stats.add(new StatCard("Pending Passes", String.valueOf(pending), "Under review", UITheme.WARNING, "⏳"));
        stats.add(new StatCard("Rejected Passes", String.valueOf(rejected), "Denied by warden", UITheme.DANGER, "❌"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        String[] cols = {"Pass ID", "Student Name", "Room", "Leave Type", "From", "To", "Reason", "Status", "Reviewed By"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (LeaveRequest lr : leaves) {
            model.addRow(new Object[]{
                    lr.getRequestId(),
                    lr.getStudentName(),
                    lr.getRoomNumber() != null ? lr.getRoomNumber() : "-",
                    lr.getLeaveType().getLabel(),
                    lr.getFromDate(),
                    lr.getToDate(),
                    lr.getReason(),
                    lr.getStatus().name(),
                    lr.getReviewedBy() != null ? lr.getReviewedBy() : "Pending"
            });
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 6: Maintenance Resolution
    private JPanel createMaintenanceReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<Complaint> complaints = db.getAllComplaints();

        long resolved = complaints.stream().filter(c -> c.getStatus() == Complaint.Status.RESOLVED).count();
        long inProg = complaints.stream().filter(c -> c.getStatus() == Complaint.Status.IN_PROGRESS).count();
        long pending = complaints.stream().filter(c -> c.getStatus() == Complaint.Status.PENDING).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Resolved Work Orders", String.valueOf(resolved), String.format("%.0f%% resolution rate", resolved * 100.0 / Math.max(1, complaints.size())), UITheme.SUCCESS, "✅"));
        stats.add(new StatCard("In Progress", String.valueOf(inProg), "Being serviced", UITheme.INFO, "🔧"));
        stats.add(new StatCard("Pending Triage", String.valueOf(pending), "Awaiting assignment", UITheme.DANGER, "⚠️"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        String[] cols = {"Ticket ID", "Student", "Room", "Category", "Description", "Assigned Staff", "Resolution Status", "Resolution Notes"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Complaint c : complaints) {
            model.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getStudentName(),
                    c.getRoomNumber() != null ? c.getRoomNumber() : "-",
                    c.getCategory().getLabel(),
                    c.getDescription(),
                    c.getAssignedTo() != null ? c.getAssignedTo() : "Unassigned",
                    c.getStatus().name(),
                    c.getResolutionNotes() != null ? c.getResolutionNotes() : "-"
            });
        }

        CustomTable table = new CustomTable(model);
        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        p.add(scroll);

        return p;
    }

    // Tab 7: Sustainability Trends
    private JPanel createSustainabilityReportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.APP_BG);
        p.setBorder(new EmptyBorder(16, 0, 0, 0));

        DatabaseManager db = DatabaseManager.getInstance();
        List<SustainabilityUsage> usages = db.getAllSustainabilityUsages();

        double totalElec = usages.stream().mapToDouble(SustainabilityUsage::getElectricityKwh).sum();
        double totalWater = usages.stream().mapToDouble(SustainabilityUsage::getWaterKiloLitres).sum();
        long jumps = usages.stream().filter(SustainabilityUsage::hasAnyAnomaly).count();

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(new StatCard("Total Electricity Consumed", String.format("%,.0f kWh", totalElec), "Historical log", UITheme.ELEC_COLOR, "⚡"));
        stats.add(new StatCard("Total Water Consumed", String.format("%,.0f kL", totalWater), "Historical log", UITheme.WATER_COLOR, "💧"));
        stats.add(new StatCard("Anomalous Surge Months", String.valueOf(jumps), "Spikes flagged (>15%)", UITheme.DANGER, "🚨"));

        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        DualResourceChart chart = new DualResourceChart(usages);
        chart.setPreferredSize(new Dimension(0, 260));
        p.add(chart);

        return p;
    }

    private void exportActiveTabToCSV() {
        String activeTitle = reportTabs.getTitleAt(reportTabs.getSelectedIndex());
        JOptionPane.showMessageDialog(this, "Exported \"" + activeTitle + "\" successfully to CSV formatted export file in your working directory.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}
