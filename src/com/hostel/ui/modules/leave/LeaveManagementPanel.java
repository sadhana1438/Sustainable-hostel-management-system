package com.hostel.ui.modules.leave;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.LeaveRequest;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class LeaveManagementPanel extends JPanel {
    private final User currentUser;
    private final boolean isStudent;

    private StatCard statPending;
    private StatCard statApproved;
    private StatCard statRejected;
    private StatCard statTotal;

    private CustomTextField searchField;
    private CustomComboBox<String> statusFilter;
    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<LeaveRequest> currentLeaveList;

    public LeaveManagementPanel(User user) {
        this.currentUser = user;
        this.isStudent = (user.getRole() == User.Role.STUDENT);

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.LEAVE_REQUESTED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.LEAVE_APPROVED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.LEAVE_REJECTED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isStudent ? "My Leave Requests & Gate Passes" : "Leave Management & Outstation Passes");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(isStudent ? "Apply for leave, medical permits, and track warden review status" : "Review, verify parental consent, and approve/reject resident outstation requests");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        if (isStudent) {
            CustomButton applyBtn = new CustomButton("🗓 Apply for Leave", CustomButton.Variant.PRIMARY);
            applyBtn.addActionListener(e -> openApplyLeaveDialog());
            panel.add(applyBtn, BorderLayout.EAST);
        }

        panel.add(titleGrp, BorderLayout.WEST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statPending = new StatCard("Pending Review", "0", "Awaiting decision", UITheme.WARNING, "⏳");
        statApproved = new StatCard("Approved Passes", "0", "Gate pass active", UITheme.SUCCESS, "✅");
        statRejected = new StatCard("Rejected / Closed", "0", "Not approved", UITheme.DANGER, "❌");
        statTotal = new StatCard("Total Requests", "0", "All submissions", UITheme.PRIMARY, "📋");

        row.add(statPending);
        row.add(statApproved);
        row.add(statRejected);
        row.add(statTotal);
        return row;
    }

    private JPanel createFilterToolbar() {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
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
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        searchField = new CustomTextField("Search by request ID, student name, reason...");
        searchField.setPreferredSize(new Dimension(320, 36));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { applyFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        statusFilter = new CustomComboBox<>(new String[]{"All Statuses", "PENDING", "APPROVED", "REJECTED"});
        statusFilter.addActionListener(e -> applyFilters());

        filters.add(new JLabel("Status:"));
        filters.add(statusFilter);

        card.add(searchField, BorderLayout.WEST);
        card.add(filters, BorderLayout.EAST);
        return card;
    }

    private JPanel createTableCard() {
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

        String[] cols = {"Request ID", "Student Name", "Room", "Leave Type", "From Date", "To Date", "Reason", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new CustomTable(tableModel);

        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "PENDING";
            Color c = UITheme.WARNING;
            if ("APPROVED".equalsIgnoreCase(val)) c = UITheme.SUCCESS;
            else if ("REJECTED".equalsIgnoreCase(val)) c = UITheme.DANGER;
            return new StatusBadge(val, c);
        });

        table.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton(isStudent ? "View 👁" : "Review / Action ⚖");
            btn.setFont(UITheme.FONT_TINY);
            btn.setFocusPainted(false);
            p.add(btn);
            return p;
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && currentLeaveList != null && row < currentLeaveList.size()) {
                    LeaveRequest lr = currentLeaveList.get(row);
                    if (col == 8 || e.getClickCount() == 2) {
                        openLeaveReviewDialog(lr);
                    }
                }
            }
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 420));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<LeaveRequest> list = isStudent 
                ? db.getLeaveRequestsForStudent(db.getStudentByStudentId(currentUser.getStudentId()) != null ? db.getStudentByStudentId(currentUser.getStudentId()).getId() : 1)
                : db.getAllLeaveRequests();

        long p = list.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.PENDING).count();
        long a = list.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.APPROVED).count();
        long r = list.stream().filter(l -> l.getStatus() == LeaveRequest.LeaveStatus.REJECTED).count();

        statPending.setValue(String.valueOf(p));
        statApproved.setValue(String.valueOf(a));
        statRejected.setValue(String.valueOf(r));
        statTotal.setValue(String.valueOf(list.size()));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<LeaveRequest> all = isStudent 
                ? db.getLeaveRequestsForStudent(db.getStudentByStudentId(currentUser.getStudentId()) != null ? db.getStudentByStudentId(currentUser.getStudentId()).getId() : 1)
                : db.getAllLeaveRequests();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String status = statusFilter != null ? (String) statusFilter.getSelectedItem() : "All Statuses";

        currentLeaveList = all.stream().filter(l -> {
            boolean matchQuery = query.isEmpty()
                    || l.getRequestId().toLowerCase().contains(query)
                    || l.getStudentName().toLowerCase().contains(query)
                    || l.getReason().toLowerCase().contains(query);

            boolean matchStatus = "All Statuses".equals(status) || l.getStatus().name().equalsIgnoreCase(status);
            return matchQuery && matchStatus;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (LeaveRequest lr : currentLeaveList) {
            tableModel.addRow(new Object[]{
                    lr.getRequestId(),
                    lr.getStudentName(),
                    lr.getRoomNumber() != null ? lr.getRoomNumber() : "-",
                    lr.getLeaveType().getLabel(),
                    lr.getFromDate(),
                    lr.getToDate(),
                    lr.getReason(),
                    lr.getStatus().name(),
                    "Action"
            });
        }
    }

    public void openApplyLeaveDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Apply for Hostel Outstation Leave", "Submit outstation gate pass request for warden approval", "Submit Leave Request");

        DatabaseManager db = DatabaseManager.getInstance();
        Student student = db.getStudentByStudentId(currentUser.getStudentId());
        if (student == null) student = db.getAllStudents().get(0);

        LeaveRequest.LeaveType[] types = LeaveRequest.LeaveType.values();
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) typeNames[i] = types[i].getLabel();
        CustomComboBox<String> typeC = new CustomComboBox<>(typeNames);

        CustomTextField fromF = new CustomTextField("YYYY-MM-DD (e.g. 2024-09-20)");
        CustomTextField toF = new CustomTextField("YYYY-MM-DD (e.g. 2024-09-23)");
        CustomTextField reasonF = new CustomTextField("Detailed reason for absence");
        CustomTextField destF = new CustomTextField("Destination address / City");
        CustomTextField parentPhoneF = new CustomTextField(student.getParentContact() != null ? student.getParentContact() : "+91 94432 00000");

        dialog.addFormField("Leave Type *", typeC);
        dialog.addTwoColumnRow("Departure Date (From) *", fromF, "Return Date (To) *", toF);
        dialog.addFormField("Purpose & Detailed Reason *", reasonF);
        dialog.addTwoColumnRow("Destination Address", destF, "Parent Consent Contact", parentPhoneF);

        Student finalStudent = student;
        dialog.getPrimaryButton().addActionListener(e -> {
            String from = fromF.getText().trim();
            String to = toF.getText().trim();
            String reason = reasonF.getText().trim();

            if (from.isEmpty() || to.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please complete required dates and reason (*).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LeaveRequest.LeaveType chosenType = types[typeC.getSelectedIndex()];
            LeaveRequest lr = new LeaveRequest(0, null, finalStudent.getId(), finalStudent.getName(),
                    finalStudent.getStudentId(), finalStudent.getRoomNumber(), chosenType, from, to,
                    reason, destF.getText().trim(), parentPhoneF.getText().trim(), true,
                    LeaveRequest.LeaveStatus.PENDING, null, null, null, null);

            db.submitLeaveRequest(lr, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(540, 460);
    }

    public void openLeaveReviewDialog(LeaveRequest lr) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String title = isStudent ? "Leave Pass: " + lr.getRequestId() : "Review Leave Pass: " + lr.getRequestId();
        AppDialog dialog = new AppDialog(parent, title, "Resident: " + lr.getStudentName() + " (" + lr.getRoomNumber() + ")", isStudent ? "Close" : "Approve Pass");

        dialog.addTwoColumnRow("Leave Type", new JLabel(lr.getLeaveType().getLabel()), "Status", new StatusBadge(lr.getStatus().name(), lr.getStatus() == LeaveRequest.LeaveStatus.APPROVED ? UITheme.SUCCESS : (lr.getStatus() == LeaveRequest.LeaveStatus.PENDING ? UITheme.WARNING : UITheme.DANGER)));
        dialog.addTwoColumnRow("From Date", new JLabel(lr.getFromDate()), "To Date", new JLabel(lr.getToDate()));
        dialog.addFormField("Reason for Absence", new JLabel("<html>" + lr.getReason() + "</html>"));
        dialog.addTwoColumnRow("Destination", new JLabel(lr.getDestinationAddress() != null ? lr.getDestinationAddress() : "-"), "Parent Phone", new JLabel(lr.getParentConsentPhone() != null ? lr.getParentConsentPhone() : "-"));

        CustomTextField commentField = new CustomTextField("Warden review comments / verification remarks");
        if (lr.getReviewComments() != null) {
            commentField.setText(lr.getReviewComments());
        }

        if (!isStudent) {
            dialog.addFormField("Warden Remarks / Parent Verification Notes", commentField);

            // Add Reject button in footer
            CustomButton rejectBtn = new CustomButton("Reject Pass ❌", CustomButton.Variant.DANGER);
            rejectBtn.addActionListener(e -> {
                DatabaseManager.getInstance().updateLeaveStatus(lr.getId(), LeaveRequest.LeaveStatus.REJECTED,
                        commentField.getText().trim(), currentUser.getFullName(), currentUser.getRole().name());
                dialog.dispose();
                refreshData();
            });
            dialog.getCancelButton().setText("Dismiss");

            dialog.getPrimaryButton().setText("Approve Pass ✅");
            dialog.getPrimaryButton().addActionListener(e -> {
                DatabaseManager.getInstance().updateLeaveStatus(lr.getId(), LeaveRequest.LeaveStatus.APPROVED,
                        commentField.getText().trim(), currentUser.getFullName(), currentUser.getRole().name());
                dialog.dispose();
                refreshData();
            });
        } else {
            dialog.getPrimaryButton().addActionListener(e -> dialog.dispose());
        }

        dialog.showModal(540, 480);
    }
}
