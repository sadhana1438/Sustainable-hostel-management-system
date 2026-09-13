package com.hostel.ui.modules.admin;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.*;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {
    private final User currentUser;
    private final Consumer<String> onNavigate;
    private final Runnable onOpenAddStudent;
    private final Runnable onOpenAllocateRoom;
    private final Runnable onOpenAddVisitor;
    private final Runnable onOpenAddResource;
    private final Runnable onOpenCreateNotice;

    // KPI Cards
    private StatCard statStudents;
    private StatCard statRooms;
    private StatCard statOccupied;
    private StatCard statAvailable;
    private StatCard statPendingFees;
    private StatCard statComplaints;

    // Content Containers
    private JPanel complaintsContainer;
    private JPanel leavesContainer;
    private JPanel visitorContainer;
    private JPanel resourceContainer;
    private JPanel activityContainer;

    public AdminDashboardPanel(User user, Consumer<String> onNavigate,
                               Runnable onOpenAddStudent, Runnable onOpenAllocateRoom,
                               Runnable onOpenAddVisitor, Runnable onOpenAddResource,
                               Runnable onOpenCreateNotice) {
        this.currentUser = user;
        this.onNavigate = onNavigate;
        this.onOpenAddStudent = onOpenAddStudent;
        this.onOpenAllocateRoom = onOpenAllocateRoom;
        this.onOpenAddVisitor = onOpenAddVisitor;
        this.onOpenAddResource = onOpenAddResource;
        this.onOpenCreateNotice = onOpenCreateNotice;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UITheme.APP_BG);
        mainContent.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Quick Actions Strip
        mainContent.add(createQuickActionsBar());
        mainContent.add(Box.createVerticalStrut(18));

        // Top Row KPI Stat Cards
        mainContent.add(createTopStatsRow());
        mainContent.add(Box.createVerticalStrut(20));

        // Two-Column Grid: Left (Queues & Sustainability) / Right (Activity & Visitors)
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        // Left Column: Pending Leaves, Complaints, Resource Overview
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        leftCol.add(createPendingLeavesCard());
        leftCol.add(Box.createVerticalStrut(18));
        leftCol.add(createComplaintsCard());
        leftCol.add(Box.createVerticalStrut(18));
        leftCol.add(createResourceOverviewCard());

        // Right Column: Visitor Live Overview, Activity Feed
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        rightCol.add(createVisitorOverviewCard());
        rightCol.add(Box.createVerticalStrut(18));
        rightCol.add(createActivityFeedCard());

        grid.add(leftCol);
        grid.add(rightCol);
        mainContent.add(grid);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        // Subscribe to real-time events
        AppEventBus.getInstance().subscribeAll(event -> refreshData());
    }

    private JPanel createQuickActionsBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        JLabel lbl = new JLabel("Quick Actions");
        lbl.setFont(UITheme.FONT_SUBHEADER);
        lbl.setForeground(UITheme.TEXT_MAIN);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        CustomButton btnStudent = new CustomButton("+ Add Student", CustomButton.Variant.PRIMARY);
        btnStudent.addActionListener(e -> { if (onOpenAddStudent != null) onOpenAddStudent.run(); });

        CustomButton btnRoom = new CustomButton("Allocate Room", CustomButton.Variant.SECONDARY);
        btnRoom.addActionListener(e -> { if (onOpenAllocateRoom != null) onOpenAllocateRoom.run(); });

        CustomButton btnVisitor = new CustomButton("+ Add Visitor", CustomButton.Variant.SECONDARY);
        btnVisitor.addActionListener(e -> { if (onOpenAddVisitor != null) onOpenAddVisitor.run(); });

        CustomButton btnResource = new CustomButton("⚡ Add Resource Data", CustomButton.Variant.SECONDARY);
        btnResource.addActionListener(e -> { if (onOpenAddResource != null) onOpenAddResource.run(); });

        CustomButton btnNotice = new CustomButton("📢 Create Notice", CustomButton.Variant.SECONDARY);
        btnNotice.addActionListener(e -> { if (onOpenCreateNotice != null) onOpenCreateNotice.run(); });

        btnPanel.add(btnStudent);
        btnPanel.add(btnRoom);
        btnPanel.add(btnVisitor);
        btnPanel.add(btnResource);
        btnPanel.add(btnNotice);

        bar.add(lbl, BorderLayout.WEST);
        bar.add(btnPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel createTopStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 6, 14, 0));
        row.setOpaque(false);

        statStudents = new StatCard("Total Students", "0", "Enrolled", UITheme.PRIMARY, "🎓");
        statRooms = new StatCard("Total Rooms", "0", "All blocks", UITheme.INFO, "🏢");
        statOccupied = new StatCard("Occupied Beds", "0", "0% capacity", UITheme.SUCCESS, "🛌");
        statAvailable = new StatCard("Available Beds", "0", "Ready to allocate", UITheme.SUCCESS, "✨");
        statPendingFees = new StatCard("Pending Fees", "₹0", "0 students", UITheme.DANGER, "💳");
        statComplaints = new StatCard("Complaints", "0", "Pending triage", UITheme.WARNING, "🛠");

        row.add(statStudents);
        row.add(statRooms);
        row.add(statOccupied);
        row.add(statAvailable);
        row.add(statPendingFees);
        row.add(statComplaints);

        return row;
    }

    private JPanel createPendingLeavesCard() {
        JPanel card = createBaseCard("Pending Leave Requests", "LEAVES", "Review All →");
        leavesContainer = new JPanel();
        leavesContainer.setLayout(new BoxLayout(leavesContainer, BoxLayout.Y_AXIS));
        leavesContainer.setOpaque(false);
        card.add(leavesContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createComplaintsCard() {
        JPanel card = createBaseCard("Active Maintenance Tickets", "COMPLIANCE", "Manage All →");
        complaintsContainer = new JPanel();
        complaintsContainer.setLayout(new BoxLayout(complaintsContainer, BoxLayout.Y_AXIS));
        complaintsContainer.setOpaque(false);
        card.add(complaintsContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createVisitorOverviewCard() {
        JPanel card = createBaseCard("Visitor Desk Overview", "VISITORS", "Visitor Log →");
        visitorContainer = new JPanel();
        visitorContainer.setLayout(new BoxLayout(visitorContainer, BoxLayout.Y_AXIS));
        visitorContainer.setOpaque(false);
        card.add(visitorContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createResourceOverviewCard() {
        JPanel card = createBaseCard("Resource Sustainability (This Month vs Last)", "SUSTAINABILITY", "Usage Analytics →");
        resourceContainer = new JPanel(new BorderLayout());
        resourceContainer.setOpaque(false);
        card.add(resourceContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActivityFeedCard() {
        JPanel card = createBaseCard("Live Activity Feed", "REPORTS", "Audit Trail →");
        activityContainer = new JPanel();
        activityContainer.setLayout(new BoxLayout(activityContainer, BoxLayout.Y_AXIS));
        activityContainer.setOpaque(false);
        card.add(activityContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBaseCard(String title, String navTarget, String actionText) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel linkLbl = new JLabel(actionText);
        linkLbl.setFont(UITheme.FONT_TINY);
        linkLbl.setForeground(UITheme.PRIMARY);
        linkLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onNavigate != null) onNavigate.accept(navTarget);
            }
        });

        header.add(titleLbl, BorderLayout.WEST);
        header.add(linkLbl, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> students = db.getAllStudents();
        List<Room> rooms = db.getAllRooms();
        List<FeeRecord> fees = db.getAllFeeRecords();
        List<Complaint> complaints = db.getAllComplaints();
        List<LeaveRequest> leaves = db.getAllLeaveRequests();
        List<Visitor> visitors = db.getAllVisitors();
        List<SustainabilityUsage> usages = db.getAllSustainabilityUsages();
        List<ActivityLog> activities = db.getRecentActivities(6);

        // Update KPIs
        statStudents.setValue(String.valueOf(students.size()));
        statRooms.setValue(String.valueOf(rooms.size()));

        int totalCap = rooms.stream().mapToInt(Room::getCapacity).sum();
        int totalOcc = rooms.stream().mapToInt(Room::getOccupied).sum();
        int totalAvail = Math.max(0, totalCap - totalOcc);
        double occPct = totalCap > 0 ? (totalOcc * 100.0 / totalCap) : 0;

        statOccupied.setValue(String.valueOf(totalOcc));
        statOccupied.setSubtext(String.format("%.0f%% capacity", occPct));

        statAvailable.setValue(String.valueOf(totalAvail));
        statAvailable.setSubtext(totalAvail + " beds vacant");

        double pendingAmount = fees.stream().mapToDouble(FeeRecord::getBalance).sum();
        long pendingCount = fees.stream().filter(f -> f.getBalance() > 0).count();
        statPendingFees.setValue(String.format("₹%,.0f", pendingAmount));
        statPendingFees.setSubtext(pendingCount + " students due");

        long activeComplaints = complaints.stream().filter(c -> c.getStatus() != Complaint.Status.RESOLVED).count();
        statComplaints.setValue(String.valueOf(activeComplaints));
        statComplaints.setSubtext(activeComplaints + " pending triage");

        // Refresh Pending Leaves
        leavesContainer.removeAll();
        List<LeaveRequest> pendingLeaves = leaves.stream()
                .filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING)
                .limit(3).toList();
        if (pendingLeaves.isEmpty()) {
            leavesContainer.add(createEmptyLabel("No pending leave requests"));
        } else {
            for (LeaveRequest lr : pendingLeaves) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel nameLbl = new JLabel("<html><b>" + lr.getStudentName() + "</b> (" + lr.getRoomNumber() + ")<br><font color='#64748b' size='2'>" + lr.getLeaveType().getLabel() + ": " + lr.getFromDate() + " to " + lr.getToDate() + "</font></html>");
                nameLbl.setFont(UITheme.FONT_BODY);

                StatusBadge badge = new StatusBadge("Pending", UITheme.WARNING);
                row.add(nameLbl, BorderLayout.CENTER);
                row.add(badge, BorderLayout.EAST);
                leavesContainer.add(row);
            }
        }

        // Refresh Complaints
        complaintsContainer.removeAll();
        List<Complaint> activeList = complaints.stream()
                .filter(c -> c.getStatus() != Complaint.Status.RESOLVED)
                .limit(3).toList();
        if (activeList.isEmpty()) {
            complaintsContainer.add(createEmptyLabel("All maintenance tickets resolved"));
        } else {
            for (Complaint c : activeList) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel title = new JLabel("<html><b>" + c.getComplaintId() + " - " + c.getCategory().getLabel() + "</b> (" + c.getRoomNumber() + ")<br><font color='#64748b' size='2'>" + (c.getDescription().length() > 45 ? c.getDescription().substring(0, 45) + "..." : c.getDescription()) + "</font></html>");
                title.setFont(UITheme.FONT_BODY);

                Color cColor = c.getStatus() == Complaint.Status.IN_PROGRESS ? UITheme.INFO : UITheme.DANGER;
                StatusBadge badge = new StatusBadge(c.getStatus().getLabel(), cColor);

                row.add(title, BorderLayout.CENTER);
                row.add(badge, BorderLayout.EAST);
                complaintsContainer.add(row);
            }
        }

        // Refresh Visitors
        visitorContainer.removeAll();
        long insideCount = visitors.stream().filter(Visitor::isCurrentlyInside).count();
        long todayCount = visitors.size();

        JPanel vStats = new JPanel(new GridLayout(1, 2, 10, 0));
        vStats.setOpaque(false);
        vStats.add(new StatCard("Currently Inside", String.valueOf(insideCount), "On premises now", UITheme.SUCCESS, "🚪"));
        vStats.add(new StatCard("Total Today", String.valueOf(todayCount), "Logged visits", UITheme.INFO, "📋"));
        visitorContainer.add(vStats);

        // Refresh Resource Overview
        resourceContainer.removeAll();
        if (!usages.isEmpty()) {
            SustainabilityUsage latest = usages.get(usages.size() - 1);
            JPanel rCard = new JPanel(new GridLayout(1, 2, 12, 0));
            rCard.setOpaque(false);

            String eleChg = (latest.getElectricityChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", latest.getElectricityChangePct());
            String watChg = (latest.getWaterChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", latest.getWaterChangePct());

            rCard.add(new StatCard("Electricity (" + latest.getMonthLabel() + ")", String.format("%,.0f kWh", latest.getElectricityKwh()), eleChg + " vs prev month", latest.isElectricityAnomaly() ? UITheme.DANGER : UITheme.ELEC_COLOR, "⚡"));
            rCard.add(new StatCard("Water (" + latest.getMonthLabel() + ")", String.format("%,.0f kL", latest.getWaterKiloLitres()), watChg + " vs prev month", latest.isWaterAnomaly() ? UITheme.DANGER : UITheme.WATER_COLOR, "💧"));

            resourceContainer.add(rCard, BorderLayout.CENTER);
        }

        // Refresh Activity Feed
        activityContainer.removeAll();
        for (ActivityLog act : activities) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel desc = new JLabel("<html><b>" + act.getActorName() + "</b> (" + act.getActorRole() + ")<br><font color='#475569'>" + act.getDescription() + "</font> <font color='#94a3b8' size='2'>• " + act.getTimestamp().substring(11, 16) + "</font></html>");
            desc.setFont(UITheme.FONT_SMALL);

            row.add(desc, BorderLayout.CENTER);
            activityContainer.add(row);
        }

        revalidate();
        repaint();
    }

    private JLabel createEmptyLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setBorder(new EmptyBorder(12, 0, 12, 0));
        return l;
    }
}
