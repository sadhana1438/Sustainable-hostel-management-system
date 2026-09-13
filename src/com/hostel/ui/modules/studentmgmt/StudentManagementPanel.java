package com.hostel.ui.modules.studentmgmt;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Room;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManagementPanel extends JPanel {
    private final User currentUser;
    private final boolean isAdmin;

    private StatCard statTotal;
    private StatCard statActive;
    private StatCard statOnLeave;
    private StatCard statUnassigned;

    private CustomTextField searchField;
    private CustomComboBox<String> deptFilter;
    private CustomComboBox<String> yearFilter;
    private CustomComboBox<String> statusFilter;

    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<Student> currentStudentList;

    public StudentManagementPanel(User user) {
        this.currentUser = user;
        this.isAdmin = (user.getRole() == User.Role.ADMIN);

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header Title & Action Row
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Key Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Search & Filter Toolbar Card
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Data Table Card with Row Actions
        content.add(createTableCard());

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(UITheme.APP_BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_CREATED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_UPDATED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_DELETED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.ROOM_ALLOCATED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isAdmin ? "Student Management & Enrollment" : "Student Directory (Read-Only)");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(isAdmin ? "Full student lifecycle, enrollment records, room mapping and profile management" : "Resident roster and room directory");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        panel.add(titleGrp, BorderLayout.WEST);

        if (isAdmin) {
            CustomButton addBtn = new CustomButton("+ Enroll New Student", CustomButton.Variant.PRIMARY);
            addBtn.addActionListener(e -> openAddStudentDialog());
            panel.add(addBtn, BorderLayout.EAST);
        }

        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statTotal = new StatCard("Total Enrolled", "0", "All departments", UITheme.PRIMARY, "🎓");
        statActive = new StatCard("Active Residents", "0", "In hostel", UITheme.SUCCESS, "✅");
        statOnLeave = new StatCard("On Approved Leave", "0", "Gate pass active", UITheme.WARNING, "🗓");
        statUnassigned = new StatCard("Unassigned Room", "0", "Needs bed", UITheme.INFO, "🏢");

        row.add(statTotal);
        row.add(statActive);
        row.add(statOnLeave);
        row.add(statUnassigned);
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

        searchField = new CustomTextField("Search by name, roll no, room...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        deptFilter = new CustomComboBox<>(new String[]{"All Departments", "Computer Science", "Information Technology", "Electronics & Comm", "Mechanical Engg", "Civil Engg"});
        yearFilter = new CustomComboBox<>(new String[]{"All Years", "1st Year", "2nd Year", "3rd Year", "4th Year"});
        statusFilter = new CustomComboBox<>(new String[]{"All Statuses", "ACTIVE", "ON_LEAVE", "INACTIVE", "SUSPENDED"});

        deptFilter.addActionListener(e -> applyFilters());
        yearFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        CustomButton resetBtn = new CustomButton("Reset", CustomButton.Variant.GHOST);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            deptFilter.setSelectedIndex(0);
            yearFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            applyFilters();
        });

        filters.add(new JLabel("Filter:"));
        filters.add(deptFilter);
        filters.add(yearFilter);
        filters.add(statusFilter);
        filters.add(resetBtn);

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

        String[] cols = {"Student ID", "Full Name", "Department", "Year Level", "Contact Phone", "Allocated Room", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new CustomTable(tableModel);
        table.getColumnModel().getColumn(6).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "ACTIVE";
            Color c = UITheme.SUCCESS;
            if ("ON_LEAVE".equalsIgnoreCase(val)) c = UITheme.WARNING;
            else if ("SUSPENDED".equalsIgnoreCase(val)) c = UITheme.DANGER;
            else if ("INACTIVE".equalsIgnoreCase(val)) c = UITheme.TEXT_MUTED;
            return new StatusBadge(val, c);
        });

        // Actions Column Renderer & Click listener
        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton(isAdmin ? "Manage ⚙" : "View 👁");
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
                if (row >= 0 && currentStudentList != null && row < currentStudentList.size()) {
                    Student s = currentStudentList.get(row);
                    if (col == 7 || e.getClickCount() == 2) {
                        openManageStudentDialog(s);
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
        List<Student> list = db.getAllStudents();

        statTotal.setValue(String.valueOf(list.size()));
        statActive.setValue(String.valueOf(list.stream().filter(s -> s.getStatus() == Student.Status.ACTIVE).count()));
        statOnLeave.setValue(String.valueOf(list.stream().filter(s -> s.getStatus() == Student.Status.ON_LEAVE).count()));
        statUnassigned.setValue(String.valueOf(list.stream().filter(s -> s.getRoomId() <= 0).count()));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Student> all = db.getAllStudents();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String dept = deptFilter != null ? (String) deptFilter.getSelectedItem() : "All Departments";
        String year = yearFilter != null ? (String) yearFilter.getSelectedItem() : "All Years";
        String status = statusFilter != null ? (String) statusFilter.getSelectedItem() : "All Statuses";

        currentStudentList = all.stream().filter(s -> {
            boolean matchQuery = query.isEmpty()
                    || s.getName().toLowerCase().contains(query)
                    || s.getStudentId().toLowerCase().contains(query)
                    || (s.getRoomNumber() != null && s.getRoomNumber().toLowerCase().contains(query));

            boolean matchDept = "All Departments".equals(dept) || s.getDepartment().equalsIgnoreCase(dept);
            boolean matchYear = "All Years".equals(year) || s.getYear().equalsIgnoreCase(year);
            boolean matchStatus = "All Statuses".equals(status) || s.getStatus().name().equalsIgnoreCase(status);

            return matchQuery && matchDept && matchYear && matchStatus;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Student s : currentStudentList) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    s.getDepartment(),
                    s.getYear(),
                    s.getContact(),
                    s.getRoomNumber() != null ? s.getRoomNumber() : "Not Allocated",
                    s.getStatus().name(),
                    isAdmin ? "Edit / Delete" : "View Details"
            });
        }
    }

    public void openAddStudentDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Enroll New Hostel Resident", "Add resident personal, academic, and contact records", "Enroll Student");

        CustomTextField nameF = new CustomTextField("Full name");
        CustomTextField idF = new CustomTextField("Roll / Reg No (e.g. STU-2024-007)");
        CustomComboBox<String> deptC = new CustomComboBox<>(new String[]{"Computer Science", "Information Technology", "Electronics & Comm", "Mechanical Engg", "Civil Engg"});
        CustomComboBox<String> yearC = new CustomComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        CustomTextField phoneF = new CustomTextField("+91 98765 00000");
        CustomTextField emailF = new CustomTextField("student@college.edu");
        CustomTextField parentPhoneF = new CustomTextField("+91 94432 00000");
        CustomTextField bloodF = new CustomTextField("O+, B+, A+, etc.");

        // Room selection combo
        List<Room> rooms = DatabaseManager.getInstance().getAllRooms();
        String[] roomOpts = new String[rooms.size() + 1];
        roomOpts[0] = "-- None (Allocate Later) --";
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            roomOpts[i + 1] = r.getRoomNumber() + " (" + r.getBlock() + " - " + r.getAvailableBeds() + " beds left)";
        }
        CustomComboBox<String> roomC = new CustomComboBox<>(roomOpts);

        dialog.addTwoColumnRow("Student Full Name *", nameF, "Roll / Student ID *", idF);
        dialog.addTwoColumnRow("Department", deptC, "Academic Year", yearC);
        dialog.addTwoColumnRow("Student Contact Phone *", phoneF, "Student Email *", emailF);
        dialog.addTwoColumnRow("Parent / Guardian Phone *", parentPhoneF, "Blood Group", bloodF);
        dialog.addFormField("Initial Room Allocation", roomC);

        dialog.getPrimaryButton().addActionListener(e -> {
            String name = nameF.getText().trim();
            String idStr = idF.getText().trim();
            String phone = phoneF.getText().trim();
            String email = emailF.getText().trim();
            String parentPhone = parentPhoneF.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please complete all mandatory fields (*)", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedRoomIdx = roomC.getSelectedIndex();
            int rId = 0;
            String rNo = null;
            String blk = null;
            if (selectedRoomIdx > 0) {
                Room chosen = rooms.get(selectedRoomIdx - 1);
                rId = chosen.getId();
                rNo = chosen.getRoomNumber();
                blk = chosen.getBlock();
            }

            Student newS = new Student(0, idStr, name, (String) deptC.getSelectedItem(), (String) yearC.getSelectedItem(),
                    phone, email, parentPhone, parentPhone, bloodF.getText().trim(), rId, rNo, blk, Student.Status.ACTIVE, null);

            DatabaseManager.getInstance().addStudent(newS, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(540, 560);
    }

    private void openManageStudentDialog(Student student) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String title = isAdmin ? "Manage Student: " + student.getName() : "Student Details: " + student.getName();
        AppDialog dialog = new AppDialog(parent, title, "ID: " + student.getStudentId() + " • Room: " + (student.getRoomNumber() != null ? student.getRoomNumber() : "None"), isAdmin ? "Save Updates" : "Close");

        CustomTextField nameF = new CustomTextField();
        nameF.setText(student.getName());
        nameF.setEditable(isAdmin);

        CustomTextField idF = new CustomTextField();
        idF.setText(student.getStudentId());
        idF.setEditable(isAdmin);

        CustomComboBox<String> deptC = new CustomComboBox<>(new String[]{"Computer Science", "Information Technology", "Electronics & Comm", "Mechanical Engg", "Civil Engg"});
        deptC.setSelectedItem(student.getDepartment());
        deptC.setEnabled(isAdmin);

        CustomComboBox<String> yearC = new CustomComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        yearC.setSelectedItem(student.getYear());
        yearC.setEnabled(isAdmin);

        CustomTextField phoneF = new CustomTextField();
        phoneF.setText(student.getContact());
        phoneF.setEditable(isAdmin);

        CustomTextField emailF = new CustomTextField();
        emailF.setText(student.getEmail());
        emailF.setEditable(isAdmin);

        CustomTextField parentPhoneF = new CustomTextField();
        parentPhoneF.setText(student.getParentContact());
        parentPhoneF.setEditable(isAdmin);

        CustomComboBox<String> statusC = new CustomComboBox<>(new String[]{"ACTIVE", "ON_LEAVE", "INACTIVE", "SUSPENDED"});
        statusC.setSelectedItem(student.getStatus().name());
        statusC.setEnabled(isAdmin);

        dialog.addTwoColumnRow("Full Name", nameF, "Roll ID", idF);
        dialog.addTwoColumnRow("Department", deptC, "Year", yearC);
        dialog.addTwoColumnRow("Contact Phone", phoneF, "Email", emailF);
        dialog.addTwoColumnRow("Parent Phone", parentPhoneF, "Status", statusC);

        if (isAdmin) {
            JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            deletePanel.setOpaque(false);
            CustomButton delBtn = new CustomButton("🗑 Delete Student Record", CustomButton.Variant.DANGER);
            delBtn.addActionListener(e -> {
                int conf = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete resident " + student.getName() + "? This will unassign their room and remove active fee dues.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (conf == JOptionPane.YES_OPTION) {
                    DatabaseManager.getInstance().deleteStudent(student.getId(), currentUser.getFullName(), currentUser.getRole().name());
                    dialog.dispose();
                    refreshData();
                }
            });
            deletePanel.add(delBtn);
            dialog.getBodyPanel().add(Box.createVerticalStrut(12));
            dialog.getBodyPanel().add(deletePanel);

            dialog.getPrimaryButton().addActionListener(e -> {
                student.setName(nameF.getText().trim());
                student.setStudentId(idF.getText().trim());
                student.setDepartment((String) deptC.getSelectedItem());
                student.setYear((String) yearC.getSelectedItem());
                student.setContact(phoneF.getText().trim());
                student.setEmail(emailF.getText().trim());
                student.setParentContact(parentPhoneF.getText().trim());
                student.setStatus(Student.Status.valueOf((String) statusC.getSelectedItem()));

                DatabaseManager.getInstance().updateStudent(student, currentUser.getFullName(), currentUser.getRole().name());
                dialog.dispose();
                refreshData();
            });
        } else {
            dialog.getPrimaryButton().addActionListener(e -> dialog.dispose());
        }

        dialog.showModal(540, 520);
    }
}
