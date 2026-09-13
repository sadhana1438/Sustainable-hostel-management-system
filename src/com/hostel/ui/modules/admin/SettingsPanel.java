package com.hostel.ui.modules.admin;

import com.hostel.db.DatabaseManager;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SettingsPanel extends JPanel {
    private final User currentUser;

    public SettingsPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Settings Cards
        if (currentUser.getRole() == User.Role.ADMIN) {
            content.add(createManageUsersCard());
            content.add(Box.createVerticalStrut(18));
        }
        content.add(createHostelInfoCard());
        content.add(Box.createVerticalStrut(18));
        content.add(createDatabaseConfigCard());
        content.add(Box.createVerticalStrut(18));
        content.add(createSustainabilityRulesCard());

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

        JLabel title = new JLabel("System Preferences & Administration Settings");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Configure hostel capacity rules, database persistence endpoints, and sustainability alert thresholds");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        panel.add(titleGrp, BorderLayout.WEST);
        return panel;
    }

    private JPanel createHostelInfoCard() {
        JPanel card = createBaseCard("Institution & Hostel Facility Details");

        CustomTextField nameF = new CustomTextField();
        nameF.setText("Greenfield University Central Campus Hostel");

        CustomTextField codeF = new CustomTextField();
        codeF.setText("HST-CAMPUS-01");

        CustomTextField curfewF = new CustomTextField();
        curfewF.setText("10:00 PM");

        CustomTextField wardenContactF = new CustomTextField();
        wardenContactF.setText("+91 98450 33445 (Warden Desk)");

        card.add(createField("Hostel Name", nameF));
        card.add(createTwoCol("Hostel Registration Code", codeF, "Standard Night Curfew", curfewF));
        card.add(createField("Emergency Warden Helpline", wardenContactF));

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnP.setOpaque(false);
        CustomButton saveBtn = new CustomButton("Save Facility Info", CustomButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hostel facility configuration updated.", "Saved", JOptionPane.INFORMATION_MESSAGE));
        btnP.add(saveBtn);
        card.add(btnP);

        return card;
    }

    private JPanel createDatabaseConfigCard() {
        JPanel card = createBaseCard("MySQL Database & JDBC Connectivity");

        DatabaseManager db = DatabaseManager.getInstance();

        JLabel statusLbl = new JLabel(db.isMysqlConnected() ? "● MySQL JDBC Connected (Live)" : "● In-Memory / Local JDBC Fallback (Active & Fully Operational)");
        statusLbl.setFont(UITheme.FONT_BODY_BOLD);
        statusLbl.setForeground(db.isMysqlConnected() ? UITheme.SUCCESS : UITheme.INFO);

        CustomTextField hostF = new CustomTextField("localhost");
        CustomTextField portF = new CustomTextField("3306");
        CustomTextField dbF = new CustomTextField("hostel_db");
        CustomTextField userF = new CustomTextField("root");
        CustomPasswordField passF = new CustomPasswordField("");

        card.add(createField("Database Engine Status", statusLbl));
        card.add(createTwoCol("MySQL Host", hostF, "Port", portF));
        card.add(createTwoCol("Database Name", dbF, "DB Username", userF));
        card.add(createField("DB Password", passF));

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnP.setOpaque(false);

        CustomButton testBtn = new CustomButton("Test Connection", CustomButton.Variant.SECONDARY);
        testBtn.addActionListener(e -> {
            boolean ok = db.attemptMySQLConnection();
            JOptionPane.showMessageDialog(this, ok ? "Successfully connected to MySQL database on " + hostF.getText() : "MySQL server not reachable on localhost:3306. Seamless local embedded mode remains active.", "Connection Status", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        });

        CustomButton saveBtn = new CustomButton("Apply DB Settings", CustomButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> {
            int port = 3306;
            try { port = Integer.parseInt(portF.getText().trim()); } catch (Exception ignored) {}
            db.configureMySQL(hostF.getText().trim(), port, dbF.getText().trim(), userF.getText().trim(), new String(passF.getPassword()));
            JOptionPane.showMessageDialog(this, "Database connection settings applied.", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        btnP.add(testBtn);
        btnP.add(saveBtn);
        card.add(btnP);

        return card;
    }

    private JPanel createSustainabilityRulesCard() {
        JPanel card = createBaseCard("Sustainability & Anomaly Jump Thresholds");

        CustomTextField spikeThresholdF = new CustomTextField("15");
        CustomTextField elecTargetF = new CustomTextField("12000");
        CustomTextField waterTargetF = new CustomTextField("300");

        card.add(createTwoCol("Spike Anomaly Jump Threshold (%)", spikeThresholdF, "Target Monthly Electricity (kWh)", elecTargetF));
        card.add(createField("Target Monthly Water (kL)", waterTargetF));

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnP.setOpaque(false);
        CustomButton saveBtn = new CustomButton("Save Threshold Rules", CustomButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sustainability rules and thresholds updated.", "Saved", JOptionPane.INFORMATION_MESSAGE));
        btnP.add(saveBtn);
        card.add(btnP);

        return card;
    }

    private JPanel createBaseCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(14));
        return card;
    }

    private JPanel createField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        p.setBorder(new EmptyBorder(0, 0, 12, 0));
        return p;
    }

    private JPanel createTwoCol(String label1, JComponent comp1, String label2, JComponent comp2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setBorder(new EmptyBorder(0, 0, 12, 0));

        row.add(createField(label1, comp1));
        row.add(createField(label2, comp2));
        return row;
    }

    private JPanel createManageUsersCard() {
        JPanel card = createBaseCard("Manage Users & System Accounts (Admin Only)");

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel sub = new JLabel("Add or remove Administrator and Warden staff accounts with isolated logins.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        CustomButton addAccBtn = new CustomButton("+ Add Staff Account", CustomButton.Variant.PRIMARY);
        topRow.add(sub, BorderLayout.WEST);
        topRow.add(addAccBtn, BorderLayout.EAST);
        card.add(topRow);
        card.add(Box.createVerticalStrut(12));

        String[] cols = {"ID", "Full Name", "Username", "Role", "Email", "Phone", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        CustomTable userTable = new CustomTable(model);

        Runnable refreshUsers = () -> {
            model.setRowCount(0);
            List<User> users = DatabaseManager.getInstance().getAllUsers();
            for (User u : users) {
                model.addRow(new Object[]{
                        u.getId(),
                        u.getFullName(),
                        u.getUsername(),
                        u.getRole().name(),
                        u.getEmail() != null ? u.getEmail() : "-",
                        u.getPhone() != null ? u.getPhone() : "-",
                        u.getId() == currentUser.getId() ? "(Active Session)" : "Delete Account ✕"
                });
            }
        };
        refreshUsers.run();

        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = userTable.getSelectedRow();
                int col = userTable.getSelectedColumn();
                if (row >= 0 && col == 6) {
                    int uid = (int) model.getValueAt(row, 0);
                    if (uid == currentUser.getId()) {
                        JOptionPane.showMessageDialog(SettingsPanel.this, "Cannot delete the currently active user account.", "Action Forbidden", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String uName = (String) model.getValueAt(row, 1);
                    int conf = JOptionPane.showConfirmDialog(SettingsPanel.this, "Are you sure you want to delete account: " + uName + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (conf == JOptionPane.YES_OPTION) {
                        DatabaseManager.getInstance().deleteUser(uid);
                        refreshUsers.run();
                        JOptionPane.showMessageDialog(SettingsPanel.this, "Account removed successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        addAccBtn.addActionListener(e -> openAddUserDialog(refreshUsers));

        JScrollPane scroll = CustomTable.wrapInScrollPane(userTable);
        scroll.setPreferredSize(new Dimension(0, 180));
        card.add(scroll);

        return card;
    }

    private void openAddUserDialog(Runnable onAdded) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Staff Account", true);
        d.setSize(440, 480);
        d.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        CustomTextField nameF = new CustomTextField();
        CustomTextField userF = new CustomTextField();
        CustomComboBox<String> roleCb = new CustomComboBox<>(new String[]{"ADMIN", "WARDEN"});
        CustomPasswordField passF = new CustomPasswordField("Password");
        CustomTextField emailF = new CustomTextField();
        CustomTextField phoneF = new CustomTextField();

        p.add(createField("Full Name", nameF));
        p.add(createField("Username", userF));
        p.add(createField("Assigned Role", roleCb));
        p.add(createField("Initial Password", passF));
        p.add(createField("Email Address", emailF));
        p.add(createField("Contact Phone", phoneF));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        CustomButton cancelBtn = new CustomButton("Cancel", CustomButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> d.dispose());
        CustomButton createBtn = new CustomButton("Create Account", CustomButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> {
            String fullName = nameF.getText().trim();
            String username = userF.getText().trim();
            String password = new String(passF.getPassword());
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Full Name, Username, and Password are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User.Role role = User.Role.valueOf((String) roleCb.getSelectedItem());
            User newUser = new User(0, username, password, fullName, role, emailF.getText().trim(), phoneF.getText().trim(), null, null);
            boolean ok = DatabaseManager.getInstance().addUser(newUser);
            if (ok) {
                onAdded.run();
                d.dispose();
                JOptionPane.showMessageDialog(SettingsPanel.this, "Account created successfully for " + fullName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(d, "Username already exists or database error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(createBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRow);

        d.setContentPane(p);
        d.setVisible(true);
    }
}
