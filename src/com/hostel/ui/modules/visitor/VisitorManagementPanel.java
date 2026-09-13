package com.hostel.ui.modules.visitor;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.model.Visitor;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class VisitorManagementPanel extends JPanel {
    private final User currentUser;

    private StatCard statToday;
    private StatCard statInside;
    private StatCard statCheckedOut;
    private StatCard statTotal;

    private CustomTextField searchField;
    private CustomComboBox<String> statusFilter;
    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<Visitor> currentVisitorList;

    public VisitorManagementPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header Title & Action
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Key Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Filter Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Visitor Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.VISITOR_CHECKED_IN, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.VISITOR_CHECKED_OUT, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Visitor & Gate Security Log");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Live visitor check-in, real-time premises presence tracking, and visitor records");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        CustomButton addBtn = new CustomButton("+ Register Visitor Entry", CustomButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddVisitorDialog());

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(addBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statToday = new StatCard("Today's Visitors", "0", "Gate logs", UITheme.PRIMARY, "📋");
        statInside = new StatCard("Currently Inside", "0", "Live on campus", UITheme.SUCCESS, "🚪");
        statCheckedOut = new StatCard("Checked Out", "0", "Completed visits", UITheme.INFO, "🏃");
        statTotal = new StatCard("Total Visitor Log", "0", "All historical records", UITheme.WARNING, "👥");

        row.add(statToday);
        row.add(statInside);
        row.add(statCheckedOut);
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

        searchField = new CustomTextField("Search visitor, phone, student visited...");
        searchField.setPreferredSize(new Dimension(320, 36));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { applyFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        statusFilter = new CustomComboBox<>(new String[]{"All Visitors", "INSIDE (Currently On Premises)", "CHECKED_OUT"});
        statusFilter.addActionListener(e -> applyFilters());

        filters.add(new JLabel("Presence:"));
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

        String[] cols = {"Visitor Name", "Phone", "Student Visited", "Room", "Purpose", "Date", "Check-in", "Check-out", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new CustomTable(tableModel);

        // Highlight "INSIDE" vs "CHECKED_OUT" at a glance
        table.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "CHECKED_OUT";
            boolean isInside = "INSIDE".equalsIgnoreCase(val);
            return new StatusBadge(isInside ? "● INSIDE" : "CHECKED OUT", isInside ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
        });

        table.getColumnModel().getColumn(9).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            if ("Check Out".equals(value)) {
                JButton btn = new JButton("Check Out 🏃");
                btn.setFont(UITheme.FONT_TINY);
                btn.setForeground(UITheme.PRIMARY);
                btn.setFocusPainted(false);
                p.add(btn);
            } else {
                JLabel l = new JLabel("Completed");
                l.setFont(UITheme.FONT_TINY);
                l.setForeground(UITheme.TEXT_MUTED);
                p.add(l);
            }
            return p;
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && currentVisitorList != null && row < currentVisitorList.size()) {
                    Visitor v = currentVisitorList.get(row);
                    if (col == 9 && v.isCurrentlyInside()) {
                        int conf = JOptionPane.showConfirmDialog(VisitorManagementPanel.this, "Check out visitor " + v.getVisitorName() + "?", "Confirm Check-Out", JOptionPane.YES_NO_OPTION);
                        if (conf == JOptionPane.YES_OPTION) {
                            DatabaseManager.getInstance().checkOutVisitor(v.getId(), currentUser.getFullName(), currentUser.getRole().name());
                            refreshData();
                        }
                    } else if (e.getClickCount() == 2) {
                        openVisitorDetailsDialog(v);
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
        List<Visitor> all = db.getAllVisitors();

        long inside = all.stream().filter(Visitor::isCurrentlyInside).count();
        long out = all.stream().filter(v -> !v.isCurrentlyInside()).count();

        statToday.setValue(String.valueOf(all.size()));
        statInside.setValue(String.valueOf(inside));
        statCheckedOut.setValue(String.valueOf(out));
        statTotal.setValue(String.valueOf(all.size()));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Visitor> all = db.getAllVisitors();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        int statusIdx = statusFilter != null ? statusFilter.getSelectedIndex() : 0;

        currentVisitorList = all.stream().filter(v -> {
            boolean matchQuery = query.isEmpty()
                    || v.getVisitorName().toLowerCase().contains(query)
                    || v.getPhone().toLowerCase().contains(query)
                    || v.getStudentName().toLowerCase().contains(query)
                    || (v.getRoomNumber() != null && v.getRoomNumber().toLowerCase().contains(query));

            boolean matchStatus = (statusIdx == 0)
                    || (statusIdx == 1 && v.isCurrentlyInside())
                    || (statusIdx == 2 && !v.isCurrentlyInside());

            return matchQuery && matchStatus;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Visitor v : currentVisitorList) {
            tableModel.addRow(new Object[]{
                    v.getVisitorName(),
                    v.getPhone(),
                    v.getStudentName() + " (" + v.getRelation() + ")",
                    v.getRoomNumber() != null ? v.getRoomNumber() : "-",
                    v.getPurpose(),
                    v.getVisitDate(),
                    v.getCheckInTime(),
                    v.getCheckOutTime() != null ? v.getCheckOutTime() : "--:--",
                    v.getStatus().name(),
                    v.isCurrentlyInside() ? "Check Out" : "Completed"
            });
        }
    }

    public void openAddVisitorDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Register Visitor Gate Entry", "Record visitor arrival and verify resident host", "Log Entry & Check-In");

        CustomTextField nameF = new CustomTextField("Visitor Full Name");
        CustomTextField phoneF = new CustomTextField("+91 98765 00000");
        CustomComboBox<String> relationC = new CustomComboBox<>(new String[]{"Parent (Father)", "Parent (Mother)", "Guardian", "Sibling", "Academic Peer", "Official Delivery / Vendor"});

        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> students = db.getAllStudents();
        String[] stuOpts = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            stuOpts[i] = s.getName() + " (" + s.getStudentId() + " - Room " + (s.getRoomNumber() != null ? s.getRoomNumber() : "N/A") + ")";
        }
        CustomComboBox<String> studentC = new CustomComboBox<>(stuOpts);

        CustomTextField purposeF = new CustomTextField("Purpose of visit (e.g. delivering academic project materials)");
        CustomComboBox<String> idTypeC = new CustomComboBox<>(new String[]{"National ID (Aadhaar)", "Driver License", "Passport", "College ID Card"});
        CustomTextField idNumF = new CustomTextField("ID number / document proof");

        dialog.addTwoColumnRow("Visitor Name *", nameF, "Phone Number *", phoneF);
        dialog.addTwoColumnRow("Relationship to Student", relationC, "Resident Student Visited *", studentC);
        dialog.addFormField("Purpose of Visit *", purposeF);
        dialog.addTwoColumnRow("ID Proof Type", idTypeC, "ID Proof Number", idNumF);

        dialog.getPrimaryButton().addActionListener(e -> {
            String name = nameF.getText().trim();
            String phone = phoneF.getText().trim();
            String purpose = purposeF.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || purpose.isEmpty() || students.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all mandatory details (*).", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Student chosen = students.get(studentC.getSelectedIndex());
            Visitor v = new Visitor(0, name, phone, (String) relationC.getSelectedItem(), chosen.getId(),
                    chosen.getName(), chosen.getRoomNumber(), purpose, null, null, null,
                    (String) idTypeC.getSelectedItem(), idNumF.getText().trim(), Visitor.VisitorStatus.INSIDE,
                    currentUser.getFullName());

            db.addVisitor(v, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(540, 480);
    }

    private void openVisitorDetailsDialog(Visitor v) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Visitor Details: " + v.getVisitorName(), "Status: " + v.getStatus().getLabel(), "Close");

        dialog.addTwoColumnRow("Phone", new JLabel(v.getPhone()), "Relation", new JLabel(v.getRelation()));
        dialog.addTwoColumnRow("Student Visited", new JLabel(v.getStudentName()), "Room", new JLabel(v.getRoomNumber() != null ? v.getRoomNumber() : "-"));
        dialog.addTwoColumnRow("Check-In Time", new JLabel(v.getCheckInTime()), "Check-Out Time", new JLabel(v.getCheckOutTime() != null ? v.getCheckOutTime() : "Still Inside"));
        dialog.addFormField("Purpose of Visit", new JLabel(v.getPurpose()));
        dialog.addTwoColumnRow("ID Proof", new JLabel(v.getIdProofType() != null ? v.getIdProofType() : "-"), "ID Number", new JLabel(v.getIdProofNumber() != null ? v.getIdProofNumber() : "-"));

        dialog.getPrimaryButton().addActionListener(e -> dialog.dispose());
        dialog.showModal(500, 400);
    }
}
