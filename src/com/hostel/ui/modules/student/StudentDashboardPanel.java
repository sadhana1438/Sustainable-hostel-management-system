package com.hostel.ui.modules.student;

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

public class StudentDashboardPanel extends JPanel {
    private final User currentUser;
    private final Consumer<String> onNavigate;
    private final Runnable onApplyLeave;
    private final Runnable onFileComplaint;
    private final Runnable onPayFees;

    private StatCard statMyRoom;
    private StatCard statFeeStatus;
    private StatCard statLeaves;
    private StatCard statComplaints;

    private JPanel noticesContainer;
    private JPanel notifsContainer;
    private JPanel roomDetailsBanner;

    public StudentDashboardPanel(User user, Consumer<String> onNavigate,
                                Runnable onApplyLeave, Runnable onFileComplaint,
                                Runnable onPayFees) {
        this.currentUser = user;
        this.onNavigate = onNavigate;
        this.onApplyLeave = onApplyLeave;
        this.onFileComplaint = onFileComplaint;
        this.onPayFees = onPayFees;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UITheme.APP_BG);
        mainContent.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Welcome & Quick Action Banner
        mainContent.add(createStudentBanner());
        mainContent.add(Box.createVerticalStrut(18));

        // Top 4 Personal Metric Cards
        mainContent.add(createPersonalStatsRow());
        mainContent.add(Box.createVerticalStrut(20));

        // Two Column Grid: Notices & Notifications
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        // Left Column: Latest Notices
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);
        leftCol.add(createLatestNoticesCard());

        // Right Column: Personal Notifications & Alerts
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);
        rightCol.add(createNotificationsCard());

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

    private JPanel createStudentBanner() {
        roomDetailsBanner = new JPanel(new BorderLayout(16, 0)) {
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
        roomDetailsBanner.setOpaque(false);
        roomDetailsBanner.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getFullName() + " 👋");
        welcome.setFont(UITheme.FONT_HEADER);
        welcome.setForeground(UITheme.PRIMARY);

        JLabel details = new JLabel("Roll No: " + (currentUser.getStudentId() != null ? currentUser.getStudentId() : "STU-2024-001") + " • College Resident");
        details.setFont(UITheme.FONT_SMALL);
        details.setForeground(UITheme.TEXT_SECONDARY);

        text.add(welcome);
        text.add(Box.createVerticalStrut(2));
        text.add(details);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        CustomButton applyLeaveBtn = new CustomButton("🗓 Apply for Leave", CustomButton.Variant.PRIMARY);
        applyLeaveBtn.addActionListener(e -> { if (onApplyLeave != null) onApplyLeave.run(); });

        CustomButton fileCompBtn = new CustomButton("🛠 File Complaint", CustomButton.Variant.SECONDARY);
        fileCompBtn.addActionListener(e -> { if (onFileComplaint != null) onFileComplaint.run(); });

        btnGroup.add(applyLeaveBtn);
        btnGroup.add(fileCompBtn);

        roomDetailsBanner.add(text, BorderLayout.CENTER);
        roomDetailsBanner.add(btnGroup, BorderLayout.EAST);
        return roomDetailsBanner;
    }

    private JPanel createPersonalStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statMyRoom = new StatCard("My Room", "A-204", "A Block (North Wing)", UITheme.PRIMARY, "🏢");
        statFeeStatus = new StatCard("Fee Status", "PAID", "No balance due", UITheme.SUCCESS, "💳");
        statLeaves = new StatCard("Leave Requests", "1 Approved", "0 pending", UITheme.INFO, "🗓");
        statComplaints = new StatCard("Complaints", "0 Active", "1 resolved", UITheme.WARNING, "🛠");

        row.add(statMyRoom);
        row.add(statFeeStatus);
        row.add(statLeaves);
        row.add(statComplaints);
        return row;
    }

    private JPanel createLatestNoticesCard() {
        JPanel card = createBaseCard("Latest Hostel Announcements", "STUDENT_NOTICES", "View Board →");
        noticesContainer = new JPanel();
        noticesContainer.setLayout(new BoxLayout(noticesContainer, BoxLayout.Y_AXIS));
        noticesContainer.setOpaque(false);
        card.add(noticesContainer, BorderLayout.CENTER);
        return card;
    }

    private JPanel createNotificationsCard() {
        JPanel card = createBaseCard("Recent Notifications & Alerts", "NOTIFICATIONS", "View All →");
        notifsContainer = new JPanel();
        notifsContainer.setLayout(new BoxLayout(notifsContainer, BoxLayout.Y_AXIS));
        notifsContainer.setOpaque(false);
        card.add(notifsContainer, BorderLayout.CENTER);
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
        Student student = db.getStudentByStudentId(currentUser.getStudentId());
        int studentId = student != null ? student.getId() : 1;

        // Room Info
        if (student != null) {
            statMyRoom.setValue(student.getRoomNumber() != null ? student.getRoomNumber() : "Unassigned");
            statMyRoom.setSubtext(student.getBlock() != null ? student.getBlock() : "Room Allocated");
        }

        // Fee Info
        FeeRecord fee = db.getFeeRecordForStudent(studentId);
        if (fee != null) {
            if (fee.getBalance() <= 0) {
                statFeeStatus.setValue("PAID");
                statFeeStatus.setSubtext("Zero balance due");
            } else {
                statFeeStatus.setValue("DUE ₹" + String.format("%,.0f", fee.getBalance()));
                statFeeStatus.setSubtext("Due: " + fee.getDueDate());
            }
        }

        // Leave Requests Info
        List<LeaveRequest> myLeaves = db.getLeaveRequestsForStudent(studentId);
        long pendingLeaves = myLeaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING).count();
        long approvedLeaves = myLeaves.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.APPROVED).count();
        statLeaves.setValue(approvedLeaves + " Approved");
        statLeaves.setSubtext(pendingLeaves + " pending review");

        // Complaints Info
        List<Complaint> myComplaints = db.getComplaintsForStudent(studentId);
        long activeComp = myComplaints.stream().filter(c -> c.getStatus() != Complaint.Status.RESOLVED).count();
        long resolvedComp = myComplaints.stream().filter(c -> c.getStatus() == Complaint.Status.RESOLVED).count();
        statComplaints.setValue(activeComp + " Active");
        statComplaints.setSubtext(resolvedComp + " resolved tickets");

        // Top 3 Notices
        noticesContainer.removeAll();
        List<Notice> notices = db.getAllNotices().stream().limit(3).toList();
        for (Notice n : notices) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(6, 0, 6, 0));

            JLabel title = new JLabel("<html><b>" + n.getTitle() + "</b><br><font color='#64748b' size='2'>" + (n.getContent().length() > 50 ? n.getContent().substring(0, 50) + "..." : n.getContent()) + " • " + n.getPostedDate() + "</font></html>");
            title.setFont(UITheme.FONT_BODY);

            Color pColor = n.getPriority() == Notice.Priority.URGENT ? UITheme.DANGER : (n.getPriority() == Notice.Priority.HIGH ? UITheme.WARNING : UITheme.PRIMARY);
            StatusBadge badge = new StatusBadge(n.getPriority().getLabel(), pColor);

            row.add(title, BorderLayout.CENTER);
            row.add(badge, BorderLayout.EAST);
            noticesContainer.add(row);
        }

        // Recent Notifications
        notifsContainer.removeAll();
        List<Notification> notifs = db.getNotificationsForUser(currentUser).stream().limit(4).toList();
        if (notifs.isEmpty()) {
            notifsContainer.add(createEmptyLabel("No notifications right now"));
        } else {
            for (Notification notif : notifs) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel msg = new JLabel("<html><b>" + notif.getTitle() + "</b><br><font color='#64748b' size='2'>" + notif.getMessage() + "</font></html>");
                msg.setFont(UITheme.FONT_BODY);

                row.add(msg, BorderLayout.CENTER);
                notifsContainer.add(row);
            }
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
