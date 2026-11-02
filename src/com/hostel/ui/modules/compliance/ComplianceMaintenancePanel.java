package com.hostel.ui.modules.compliance;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Complaint;
import com.hostel.model.ComplianceRecord;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ComplianceMaintenancePanel extends JPanel {
    private final User currentUser;
    private final boolean isStudent;

    private JTabbedPane tabbedPane;

    // Maintenance Components
    private StatCard statPendingMaint;
    private StatCard statInProgressMaint;
    private StatCard statResolvedMaint;
    private StatCard statTotalMaint;
    private CustomTable maintTable;
    private DefaultTableModel maintTableModel;
    private List<Complaint> currentComplaintList;

    // Compliance Components
    private StatCard statViolations;
    private StatCard statTotalFines;
    private StatCard statFinesPaid;
    private CustomTable compTable;
    private DefaultTableModel compTableModel;
    private List<ComplianceRecord> currentComplianceList;

    public ComplianceMaintenancePanel(User user) {
        this.currentUser = user;
        this.isStudent = (user.getRole() == User.Role.STUDENT);

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(UITheme.APP_BG);
        main.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        main.add(createHeaderRow());
        main.add(Box.createVerticalStrut(18));

        // Modern Tabbed Container
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("🛠 Maintenance & Facility Complaints", createMaintenanceTab());

        if (!isStudent) {
            tabbedPane.addTab("⚖ Compliance & Disciplinary Records", createComplianceTab());
        }

        main.add(tabbedPane);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.COMPLAINT_FILED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.COMPLAINT_STATUS_CHANGED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.COMPLIANCE_RECORDED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isStudent ? "My Maintenance Requests" : "Compliance & Maintenance Hub");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(isStudent ? "Report facility issues, track repair progress, and resolve room tickets" : "Manage facility work orders (Pending → In Progress → Resolved) and disciplinary audits");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        if (isStudent) {
            CustomButton addCompBtn = new CustomButton("+ File Maintenance Ticket", CustomButton.Variant.PRIMARY);
            addCompBtn.addActionListener(e -> openFileComplaintDialog());
            btnPanel.add(addCompBtn);
        } else {
            CustomButton addDisciplineBtn = new CustomButton("⚖ Record Violation", CustomButton.Variant.SECONDARY);
            addDisciplineBtn.addActionListener(e -> openAddComplianceDialog());
            btnPanel.add(addDisciplineBtn);
        }

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createMaintenanceTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.APP_BG);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        statPendingMaint = new StatCard("Pending Triage", "0", "Unassigned tickets", UITheme.DANGER, "⚠️");
        statInProgressMaint = new StatCard("In Progress", "0", "Technician dispatched", UITheme.INFO, "🔧");
        statResolvedMaint = new StatCard("Resolved", "0", "Fixed & closed", UITheme.SUCCESS, "✅");
        statTotalMaint = new StatCard("Total Tickets", "0", "All reported issues", UITheme.PRIMARY, "📋");

        statsRow.add(statPendingMaint);
        statsRow.add(statInProgressMaint);
        statsRow.add(statResolvedMaint);
        statsRow.add(statTotalMaint);

        panel.add(statsRow);
        panel.add(Box.createVerticalStrut(16));

        // Table Card
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

        String[] cols = {"Ticket ID", "Student", "Room", "Category", "Description", "Filed Date", "Assigned To", "Status", "Actions"};
        maintTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        maintTable = new CustomTable(maintTableModel);

        // Status Badge (Walking Pending -> In Progress -> Resolved)
        maintTable.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "PENDING";
            Color c = UITheme.DANGER;
            if ("IN_PROGRESS".equalsIgnoreCase(val)) c = UITheme.INFO;
            else if ("RESOLVED".equalsIgnoreCase(val)) c = UITheme.SUCCESS;
            return new StatusBadge(val.replace("_", " "), c);
        });

        maintTable.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton(isStudent ? "View 👁" : "Manage Status 🔧");
            btn.setFont(UITheme.FONT_TINY);
            btn.setFocusPainted(false);
            p.add(btn);
            return p;
        });

        maintTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = maintTable.getSelectedRow();
                int col = maintTable.getSelectedColumn();
                if (row >= 0 && currentComplaintList != null && row < currentComplaintList.size()) {
                    Complaint c = currentComplaintList.get(row);
                    if (col == 8 || e.getClickCount() == 2) {
                        openManageComplaintDialog(c);
                    }
                }
            }
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(maintTable);
        scroll.setPreferredSize(new Dimension(0, 380));
        card.add(scroll, BorderLayout.CENTER);
        panel.add(card);

        return panel;
    }

    private JPanel createComplianceTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.APP_BG);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        statViolations = new StatCard("Total Infractions", "0", "Discipline violations", UITheme.WARNING, "⚠️");
        statTotalFines = new StatCard("Total Fines Imposed", "₹0", "Disciplinary surcharges", UITheme.DANGER, "💸");
        statFinesPaid = new StatCard("Settled Infractions", "0", "Paid & cleared", UITheme.SUCCESS, "✅");

        statsRow.add(statViolations);
        statsRow.add(statTotalFines);
        statsRow.add(statFinesPaid);

        panel.add(statsRow);
        panel.add(Box.createVerticalStrut(16));

        // Table Card
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

        String[] cols = {"Student Name", "Roll ID", "Room", "Rule Violated", "Date", "Description", "Action Taken", "Fine Amount", "Fine Paid"};
        compTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        compTable = new CustomTable(compTableModel);
        compTable.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            boolean paid = Boolean.TRUE.equals(value);
            return new StatusBadge(paid ? "PAID" : "UNPAID", paid ? UITheme.SUCCESS : UITheme.DANGER);
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(compTable);
        scroll.setPreferredSize(new Dimension(0, 380));
        card.add(scroll, BorderLayout.CENTER);
        panel.add(card);

        return panel;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();

        // Refresh Maintenance
        currentComplaintList = isStudent 
                ? db.getComplaintsForStudent(db.getStudentByStudentId(currentUser.getStudentId()) != null ? db.getStudentByStudentId(currentUser.getStudentId()).getId() : 1)
                : db.getAllComplaints();

        long p = currentComplaintList.stream().filter(c -> c.getStatus() == Complaint.Status.PENDING).count();
        long ip = currentComplaintList.stream().filter(c -> c.getStatus() == Complaint.Status.IN_PROGRESS).count();
        long r = currentComplaintList.stream().filter(c -> c.getStatus() == Complaint.Status.RESOLVED).count();

        statPendingMaint.setValue(String.valueOf(p));
        statInProgressMaint.setValue(String.valueOf(ip));
        statResolvedMaint.setValue(String.valueOf(r));
        statTotalMaint.setValue(String.valueOf(currentComplaintList.size()));

        maintTableModel.setRowCount(0);
        for (Complaint c : currentComplaintList) {
            maintTableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getStudentName(),
                    c.getRoomNumber() != null ? c.getRoomNumber() : "-",
                    c.getCategory().getLabel(),
                    c.getDescription(),
                    c.getFiledDate(),
                    c.getAssignedTo() != null ? c.getAssignedTo() : "Unassigned",
                    c.getStatus().name(),
                    "Action"
            });
        }

        // Refresh Compliance
        if (!isStudent && compTableModel != null) {
            currentComplianceList = db.getAllComplianceRecords();
            statViolations.setValue(String.valueOf(currentComplianceList.size()));

            double totalFine = currentComplianceList.stream().mapToDouble(ComplianceRecord::getFineAmount).sum();
            long paidCount = currentComplianceList.stream().filter(ComplianceRecord::isFinePaid).count();

            statTotalFines.setValue(String.format("₹%,.0f", totalFine));
            statFinesPaid.setValue(paidCount + " / " + currentComplianceList.size());

            compTableModel.setRowCount(0);
            for (ComplianceRecord cr : currentComplianceList) {
                compTableModel.addRow(new Object[]{
                        cr.getStudentName(),
                        cr.getStudentRollNo(),
                        cr.getRoomNumber() != null ? cr.getRoomNumber() : "-",
                        cr.getRuleViolated(),
                        cr.getViolationDate(),
                        cr.getDescription(),
                        cr.getActionTaken(),
                        String.format("₹%,.2f", cr.getFineAmount()),
                        cr.isFinePaid()
                });
            }
        }

        revalidate();
        repaint();
    }

    public void openFileComplaintDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Report Maintenance Issue / Complaint", "Submit room or facility issue for staff resolution", "File Ticket");

        DatabaseManager db = DatabaseManager.getInstance();
        Student student = db.getStudentByStudentId(currentUser.getStudentId());
        if (student == null) student = db.getAllStudents().get(0);

        Complaint.Category[] cats = Complaint.Category.values();
        String[] catNames = new String[cats.length];
        for (int i = 0; i < cats.length; i++) catNames[i] = cats[i].getLabel();
        CustomComboBox<String> catC = new CustomComboBox<>(catNames);

        CustomComboBox<String> prioC = new CustomComboBox<>(new String[]{"Low", "Medium", "High", "Urgent"});
        prioC.setSelectedItem("Medium");

        CustomTextField roomF = new CustomTextField(student.getRoomNumber() != null ? student.getRoomNumber() : "A-204");
        CustomTextField descF = new CustomTextField("Detailed description of problem (e.g. Tap leaking in bathroom sink)");

        dialog.addTwoColumnRow("Category *", catC, "Priority Level", prioC);
        dialog.addFormField("Room / Location *", roomF);
        dialog.addFormField("Description of Issue *", descF);

        Student finalStudent = student;
        dialog.getPrimaryButton().addActionListener(e -> {
            String desc = descF.getText().trim();
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a description of the issue.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Complaint.Category cat = cats[catC.getSelectedIndex()];
            Complaint c = new Complaint(0, null, finalStudent.getId(), finalStudent.getName(),
                    roomF.getText().trim(), cat, desc, (String) prioC.getSelectedItem(),
                    null, null, Complaint.Status.PENDING, null, null);

            db.submitComplaint(c, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(520, 400);
    }

    public void openManageComplaintDialog(Complaint c) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String title = isStudent ? "Ticket Details: " + c.getComplaintId() : "Manage Maintenance Ticket: " + c.getComplaintId();
        AppDialog dialog = new AppDialog(parent, title, "Resident: " + c.getStudentName() + " (Room " + c.getRoomNumber() + ")", isStudent ? "Close" : "Update Ticket Status");

        dialog.addTwoColumnRow("Category", new JLabel(c.getCategory().getLabel()), "Priority", new JLabel(c.getPriority()));
        dialog.addFormField("Issue Description", new JLabel("<html>" + c.getDescription() + "</html>"));

        CustomComboBox<String> statusC = new CustomComboBox<>(new String[]{"PENDING", "IN_PROGRESS", "RESOLVED"});
        statusC.setSelectedItem(c.getStatus().name());
        statusC.setEnabled(!isStudent);

        CustomTextField assignedF = new CustomTextField("Technician / Staff name (e.g. Ramesh - Plumber)");
        if (c.getAssignedTo() != null) assignedF.setText(c.getAssignedTo());
        assignedF.setEditable(!isStudent);

        CustomTextField notesF = new CustomTextField("Resolution notes / repairs completed");
        if (c.getResolutionNotes() != null) notesF.setText(c.getResolutionNotes());
        notesF.setEditable(!isStudent);

        dialog.addTwoColumnRow("Ticket Status", statusC, "Assigned Technician", assignedF);
        dialog.addFormField("Resolution & Repair Notes", notesF);

        if (!isStudent) {
            dialog.getPrimaryButton().addActionListener(e -> {
                Complaint.Status st = Complaint.Status.valueOf((String) statusC.getSelectedItem());
                DatabaseManager.getInstance().updateComplaintStatus(c.getId(), st, assignedF.getText().trim(),
                        notesF.getText().trim(), currentUser.getFullName(), currentUser.getRole().name());
                dialog.dispose();
                refreshData();
            });
        } else {
            dialog.getPrimaryButton().addActionListener(e -> dialog.dispose());
        }

        dialog.showModal(540, 460);
    }

    private void openAddComplianceDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Record Rule Violation & Fine", "Document disciplinary infraction against resident", "Record Infraction");

        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> students = db.getAllStudents();
        String[] stuOpts = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            stuOpts[i] = s.getName() + " (" + s.getStudentId() + " - Room " + (s.getRoomNumber() != null ? s.getRoomNumber() : "-") + ")";
        }
        CustomComboBox<String> stuC = new CustomComboBox<>(stuOpts);

        CustomComboBox<String> ruleC = new CustomComboBox<>(new String[]{
                "Unauthorized Electrical Heating Appliance",
                "Late Entry Post 10:00 PM Curfew",
                "Noise & Curfew Disturbance in Corridor",
                "Unauthorized Overnight Guest in Room",
                "Damage to Hostel Property / Furniture",
                "Smoking / Substance Prohibition Violation"
        });

        CustomTextField descF = new CustomTextField("Audit description & details");
        CustomComboBox<String> actionC = new CustomComboBox<>(new String[]{
                "Written Warning Issued",
                "Fine Imposed & Parent Notified",
                "Counseling & Warning Letter",
                "Hostel Suspension Hearing Scheduled"
        });
        CustomTextField fineF = new CustomTextField("500");

        dialog.addFormField("Resident Student *", stuC);
        dialog.addFormField("Rule Violated *", ruleC);
        dialog.addFormField("Detailed Description *", descF);
        dialog.addTwoColumnRow("Action Taken", actionC, "Fine Amount (₹)", fineF);

        dialog.getPrimaryButton().addActionListener(e -> {
            String desc = descF.getText().trim();
            if (desc.isEmpty() || students.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double fine = 0;
            try { fine = Double.parseDouble(fineF.getText().trim()); } catch (Exception ignored) {}

            Student chosen = students.get(stuC.getSelectedIndex());
            ComplianceRecord cr = new ComplianceRecord(0, chosen.getId(), chosen.getName(), chosen.getStudentId(),
                    chosen.getRoomNumber(), (String) ruleC.getSelectedItem(), null, desc,
                    (String) actionC.getSelectedItem(), fine, currentUser.getFullName(), false);

            db.addComplianceRecord(cr, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(540, 480);
    }
}
