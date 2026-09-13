package com.hostel.ui.modules.warden;

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

public class WardenDashboardPanel extends JPanel {
    private final User currentUser;
    private final Consumer<String> onNavigate;
    private final Consumer<LeaveRequest> onReviewLeave;
    private final Consumer<Complaint> onManageComplaint;

    // KPI Cards
    private StatCard statPendingLeaves;
    private StatCard statComplaints;
    private StatCard statVisitorsInside;
    private StatCard statOccupancy;

    private JPanel leavesContainer;
    private JPanel complaintsContainer;
    private JPanel occupancyCardContainer;
    private JPanel resourceContainer;
    private JPanel activityContainer;

    public WardenDashboardPanel(User user, Consumer<String> onNavigate, 
                               Consumer<LeaveRequest> onReviewLeave, 
                               Consumer<Complaint> onManageComplaint) {
        this.currentUser = user;
        this.onNavigate = onNavigate;
        this.onReviewLeave = onReviewLeave;
        this.onManageComplaint = onManageComplaint;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UITheme.APP_BG);
        mainContent.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Urgent Action Banner
        mainContent.add(createTodayPrioritiesBanner());
        mainContent.add(Box.createVerticalStrut(18));

        // Top Row Stats
        mainContent.add(createTopStatsRow());
        mainContent.add(Box.createVerticalStrut(20));

        // Grid of Action Cards
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        // Left Column: Pending Leaves to Approve/Reject & Complaints
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        leftCol.add(createPendingLeavesCard());
        leftCol.add(Box.createVerticalStrut(18));
        leftCol.add(createComplaintsTriageCard());

        // Right Column: Room Occupancy & Resource Consumption & Activity
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        rightCol.add(createOccupancyCard());
        rightCol.add(Box.createVerticalStrut(18));
        rightCol.add(createSustainabilityMetersCard());
        rightCol.add(Box.createVerticalStrut(18));
        rightCol.add(createActivityCard());

        grid.add(leftCol);
        grid.add(rightCol);
        mainContent.add(grid);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribeAll(event -> refreshData());
    }

    private JPanel createTodayPrioritiesBanner() {
        JPanel banner = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(new Color(238, 242, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(199, 210, 254));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel title = new JLabel("⚡ Warden Operations & Daily Action Board");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.PRIMARY);

        JLabel sub = new JLabel("Review pending leave passes, dispatch maintenance technicians, and verify visitor entry.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        text.add(title);
        text.add(Box.createVerticalStrut(2));
        text.add(sub);

        CustomButton postNoticeBtn = new CustomButton("+ Post Notice", CustomButton.Variant.PRIMARY);
        postNoticeBtn.addActionListener(e -> { if (onNavigate != null) onNavigate.accept("NOTICES"); });

        banner.add(text, BorderLayout.CENTER);
        banner.add(postNoticeBtn, BorderLayout.EAST);
        return banner;
    }

    private JPanel createTopStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statPendingLeaves = new StatCard("Pending Leaves", "0", "Action required", UITheme.WARNING, "🗓");
        statComplaints = new StatCard("Active Maintenance", "0", "In queue", UITheme.DANGER, "🛠");
        statVisitorsInside = new StatCard("Visitors Inside", "0", "At hostel now", UITheme.SUCCESS, "🚪");
        statOccupancy = new StatCard("Hostel Occupancy", "0%", "Bed capacity", UITheme.PRIMARY, "🏢");

        row.add(statPendingLeaves);
        row.add(statComplaints);
        row.add(statVisitorsInside);
        row.add(statOccupancy);
        return row;
    }

    private JPanel createPendingLeavesCard() {
        JPanel card = createBaseCard("Pending Leave Requests (Approve / Reject)", "LEAVES", "Manage All →");
        leavesContainer = new JPanel();
        leavesContainer.setLayout(new BoxLayout(leavesContainer, BoxLayout.Y_AXIS));
        leavesContainer.setOpaque(false);
        card.add(leavesContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createComplaintsTriageCard() {
        JPanel card = createBaseCard("Maintenance Tickets Needing Attention", "COMPLIANCE", "Open Tickets →");
        complaintsContainer = new JPanel();
        complaintsContainer.setLayout(new BoxLayout(complaintsContainer, BoxLayout.Y_AXIS));
        complaintsContainer.setOpaque(false);
        card.add(complaintsContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createOccupancyCard() {
        JPanel card = createBaseCard("Room Occupancy Status", "ROOMS", "Allocation Grid →");
        occupancyCardContainer = new JPanel(new BorderLayout(16, 0));
        occupancyCardContainer.setOpaque(false);
        card.add(occupancyCardContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSustainabilityMetersCard() {
        JPanel card = createBaseCard("Resource Usage & Sustainability", "SUSTAINABILITY", "View Charts →");
        resourceContainer = new JPanel(new GridLayout(1, 2, 12, 0));
        resourceContainer.setOpaque(false);
        card.add(resourceContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActivityCard() {
        JPanel card = createBaseCard("Recent Activity", "REPORTS", "Audit Trail →");
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
        List<LeaveRequest> leaves = db.getAllLeaveRequests();
        List<Complaint> complaints = db.getAllComplaints();
        List<Visitor> visitors = db.getAllVisitors();
        List<Room> rooms = db.getAllRooms();
        List<SustainabilityUsage> usages = db.getAllSustainabilityUsages();
        List<ActivityLog> activities = db.getRecentActivities(5);

        long pendingLeaveCount = leaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING).count();
        long activeComplaintCount = complaints.stream().filter(c -> c.getStatus() != Complaint.Status.RESOLVED).count();
        long insideVisitors = visitors.stream().filter(Visitor::isCurrentlyInside).count();

        int totalCap = rooms.stream().mapToInt(Room::getCapacity).sum();
        int totalOcc = rooms.stream().mapToInt(Room::getOccupied).sum();
        double occPct = totalCap > 0 ? (totalOcc * 100.0 / totalCap) : 0;

        statPendingLeaves.setValue(String.valueOf(pendingLeaveCount));
        statComplaints.setValue(String.valueOf(activeComplaintCount));
        statVisitorsInside.setValue(String.valueOf(insideVisitors));
        statOccupancy.setValue(String.format("%.0f%%", occPct));
        statOccupancy.setSubtext(totalOcc + " / " + totalCap + " Beds");

        // Leaves Container with 1-click review
        leavesContainer.removeAll();
        List<LeaveRequest> pendingList = leaves.stream()
                .filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING)
                .limit(4).toList();

        if (pendingList.isEmpty()) {
            leavesContainer.add(createEmptyLabel("No pending leave requests"));
        } else {
            for (LeaveRequest lr : pendingList) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel name = new JLabel("<html><b>" + lr.getStudentName() + "</b> (" + lr.getRoomNumber() + ") - <i>" + lr.getLeaveType().getLabel() + "</i><br><font color='#64748b' size='2'>" + lr.getFromDate() + " to " + lr.getToDate() + " | " + lr.getReason() + "</font></html>");
                name.setFont(UITheme.FONT_BODY);

                JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                btnGroup.setOpaque(false);

                CustomButton reviewBtn = new CustomButton("Review", CustomButton.Variant.PRIMARY);
                reviewBtn.setFont(UITheme.FONT_TINY);
                reviewBtn.addActionListener(e -> {
                    if (onReviewLeave != null) onReviewLeave.accept(lr);
                });

                btnGroup.add(reviewBtn);

                row.add(name, BorderLayout.CENTER);
                row.add(btnGroup, BorderLayout.EAST);
                leavesContainer.add(row);
            }
        }

        // Complaints Triage Container
        complaintsContainer.removeAll();
        List<Complaint> activeCompList = complaints.stream()
                .filter(c -> c.getStatus() != Complaint.Status.RESOLVED)
                .limit(4).toList();

        if (activeCompList.isEmpty()) {
            complaintsContainer.add(createEmptyLabel("All maintenance tickets resolved"));
        } else {
            for (Complaint c : activeCompList) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel title = new JLabel("<html><b>" + c.getComplaintId() + "</b> - " + c.getCategory().getLabel() + " (Room " + c.getRoomNumber() + ")<br><font color='#64748b' size='2'>" + c.getDescription() + " (Status: " + c.getStatus().getLabel() + ")</font></html>");
                title.setFont(UITheme.FONT_BODY);

                CustomButton manageBtn = new CustomButton("Update", CustomButton.Variant.SECONDARY);
                manageBtn.setFont(UITheme.FONT_TINY);
                manageBtn.addActionListener(e -> {
                    if (onManageComplaint != null) onManageComplaint.accept(c);
                });

                row.add(title, BorderLayout.CENTER);
                row.add(manageBtn, BorderLayout.EAST);
                complaintsContainer.add(row);
            }
        }

        // Occupancy Donut / Details
        occupancyCardContainer.removeAll();
        DonutChart donut = new DonutChart(totalOcc, totalCap, "Occupied", UITheme.PRIMARY);
        donut.setPreferredSize(new Dimension(140, 140));

        JPanel occText = new JPanel();
        occText.setLayout(new BoxLayout(occText, BoxLayout.Y_AXIS));
        occText.setOpaque(false);

        JLabel l1 = new JLabel("Total Capacity: " + totalCap + " beds");
        l1.setFont(UITheme.FONT_BODY_BOLD);

        JLabel l2 = new JLabel("Occupied Beds: " + totalOcc);
        l2.setFont(UITheme.FONT_BODY);
        l2.setForeground(UITheme.SUCCESS);

        JLabel l3 = new JLabel("Available Vacant: " + (totalCap - totalOcc));
        l3.setFont(UITheme.FONT_BODY);
        l3.setForeground(UITheme.TEXT_SECONDARY);

        occText.add(Box.createVerticalGlue());
        occText.add(l1);
        occText.add(Box.createVerticalStrut(4));
        occText.add(l2);
        occText.add(Box.createVerticalStrut(4));
        occText.add(l3);
        occText.add(Box.createVerticalGlue());

        occupancyCardContainer.add(donut, BorderLayout.WEST);
        occupancyCardContainer.add(occText, BorderLayout.CENTER);

        // Sustainability Meters
        resourceContainer.removeAll();
        if (!usages.isEmpty()) {
            SustainabilityUsage latest = usages.get(usages.size() - 1);
            resourceContainer.add(new StatCard("Electricity", String.format("%,.0f kWh", latest.getElectricityKwh()), latest.getMonthLabel(), UITheme.ELEC_COLOR, "⚡"));
            resourceContainer.add(new StatCard("Water Usage", String.format("%,.0f kL", latest.getWaterKiloLitres()), latest.getMonthLabel(), UITheme.WATER_COLOR, "💧"));
        }

        // Activity Container
        activityContainer.removeAll();
        for (ActivityLog act : activities) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(3, 0, 3, 0));

            JLabel desc = new JLabel("<html><b>" + act.getActorName() + "</b>: " + act.getDescription() + " <font color='#94a3b8' size='2'>(" + act.getTimestamp().substring(11, 16) + ")</font></html>");
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
