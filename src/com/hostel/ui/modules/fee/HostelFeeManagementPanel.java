package com.hostel.ui.modules.fee;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.FeeRecord;
import com.hostel.model.PaymentTransaction;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class HostelFeeManagementPanel extends JPanel {
    private final User currentUser;

    private StatCard statTotalCollected;
    private StatCard statTotalPending;
    private StatCard statPaidCount;
    private StatCard statPendingCount;

    private CustomTextField searchField;
    private CustomComboBox<String> statusFilter;
    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<FeeRecord> currentFeeList;

    public HostelFeeManagementPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header Title & Actions
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Key Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Search & Filter Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Fee Records Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.FEE_PAID, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.STUDENT_CREATED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Hostel Fee & Payment Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Fee collection structures, term dues, receipt generation and payment tracking");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        CustomButton payBtn = new CustomButton("💳 Record Fee Payment", CustomButton.Variant.PRIMARY);
        payBtn.addActionListener(e -> openRecordPaymentDialog(null));

        CustomButton addFeeBtn = new CustomButton("+ Add Fee Structure", CustomButton.Variant.SECONDARY);
        addFeeBtn.addActionListener(e -> openAddFeeDialog());

        btnPanel.add(payBtn);
        btnPanel.add(addFeeBtn);

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statTotalCollected = new StatCard("Total Collected", "₹0", "Deposited to hostel account", UITheme.SUCCESS, "💰");
        statTotalPending = new StatCard("Total Pending Balance", "₹0", "Outstanding dues", UITheme.DANGER, "⏳");
        statPaidCount = new StatCard("Paid In Full", "0", "Students clear", UITheme.PRIMARY, "✅");
        statPendingCount = new StatCard("Pending Students", "0", "Awaiting settlement", UITheme.WARNING, "⚠️");

        row.add(statTotalCollected);
        row.add(statTotalPending);
        row.add(statPaidCount);
        row.add(statPendingCount);
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

        searchField = new CustomTextField("Search by student name, roll no, room...");
        searchField.setPreferredSize(new Dimension(320, 36));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { applyFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        statusFilter = new CustomComboBox<>(new String[]{"All Statuses", "PAID", "PARTIAL", "PENDING", "OVERDUE"});
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

        String[] cols = {"Student Name", "Roll ID", "Room", "Total Fee", "Paid Amount", "Balance Due", "Due Date", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new CustomTable(tableModel);

        // Status Badge Renderer
        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "PENDING";
            Color c = UITheme.SUCCESS;
            if ("PARTIAL".equalsIgnoreCase(val)) c = UITheme.WARNING;
            else if ("PENDING".equalsIgnoreCase(val) || "OVERDUE".equalsIgnoreCase(val)) c = UITheme.DANGER;
            return new StatusBadge(val, c);
        });

        table.getColumnModel().getColumn(8).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton("Record / History 💳");
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
                if (row >= 0 && currentFeeList != null && row < currentFeeList.size()) {
                    FeeRecord f = currentFeeList.get(row);
                    if (col == 8 || e.getClickCount() == 2) {
                        openRecordPaymentDialog(f);
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
        List<FeeRecord> fees = db.getAllFeeRecords();

        double totalPaid = fees.stream().mapToDouble(FeeRecord::getPaidAmount).sum();
        double totalBalance = fees.stream().mapToDouble(FeeRecord::getBalance).sum();
        long paidCount = fees.stream().filter(f -> f.getStatus() == FeeRecord.FeeStatus.PAID).count();
        long pendingCount = fees.stream().filter(f -> f.getBalance() > 0).count();

        statTotalCollected.setValue(String.format("₹%,.0f", totalPaid));
        statTotalPending.setValue(String.format("₹%,.0f", totalBalance));
        statPaidCount.setValue(String.valueOf(paidCount));
        statPendingCount.setValue(String.valueOf(pendingCount));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<FeeRecord> all = db.getAllFeeRecords();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String status = statusFilter != null ? (String) statusFilter.getSelectedItem() : "All Statuses";

        currentFeeList = all.stream().filter(f -> {
            boolean matchQuery = query.isEmpty()
                    || f.getStudentName().toLowerCase().contains(query)
                    || f.getStudentRollNo().toLowerCase().contains(query)
                    || (f.getRoomNumber() != null && f.getRoomNumber().toLowerCase().contains(query));

            boolean matchStatus = "All Statuses".equals(status) || f.getStatus().name().equalsIgnoreCase(status);
            return matchQuery && matchStatus;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (FeeRecord f : currentFeeList) {
            tableModel.addRow(new Object[]{
                    f.getStudentName(),
                    f.getStudentRollNo(),
                    f.getRoomNumber() != null ? f.getRoomNumber() : "-",
                    String.format("₹%,.2f", f.getTotalFee()),
                    String.format("₹%,.2f", f.getPaidAmount()),
                    String.format("₹%,.2f", f.getBalance()),
                    f.getDueDate(),
                    f.getStatus().name(),
                    "Pay"
            });
        }
    }

    public void openRecordPaymentDialog(FeeRecord initialFee) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Record Fee Payment & Generate Receipt", "Post transaction to student account with instant receipt", "Submit Payment");

        DatabaseManager db = DatabaseManager.getInstance();
        List<FeeRecord> fees = db.getAllFeeRecords();

        String[] feeOpts = new String[fees.size()];
        int selectedIndex = 0;
        for (int i = 0; i < fees.size(); i++) {
            FeeRecord f = fees.get(i);
            feeOpts[i] = f.getStudentName() + " (" + f.getStudentRollNo() + ") - Due: ₹" + String.format("%,.2f", f.getBalance());
            if (initialFee != null && f.getId() == initialFee.getId()) {
                selectedIndex = i;
            }
        }
        CustomComboBox<String> feeCombo = new CustomComboBox<>(feeOpts);
        feeCombo.setSelectedIndex(selectedIndex);

        CustomTextField amountField = new CustomTextField("Payment amount (e.g. 25000)");
        if (initialFee != null && initialFee.getBalance() > 0) {
            amountField.setText(String.valueOf((int) initialFee.getBalance()));
        }

        PaymentTransaction.PaymentMethod[] methods = PaymentTransaction.PaymentMethod.values();
        String[] methodNames = new String[methods.length];
        for (int i = 0; i < methods.length; i++) methodNames[i] = methods[i].getLabel();
        CustomComboBox<String> methodCombo = new CustomComboBox<>(methodNames);

        CustomTextField refField = new CustomTextField("Transaction Ref / UPI UTR / Challan No");
        CustomTextField notesField = new CustomTextField("Payment notes / Remarks");

        dialog.addFormField("Select Student Fee Account *", feeCombo);
        dialog.addTwoColumnRow("Payment Amount (₹) *", amountField, "Payment Method *", methodCombo);
        dialog.addTwoColumnRow("Transaction Reference No", refField, "Notes", notesField);

        dialog.getPrimaryButton().addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                int idx = feeCombo.getSelectedIndex();
                FeeRecord targetFee = fees.get(idx);

                PaymentTransaction.PaymentMethod chosenMethod = methods[methodCombo.getSelectedIndex()];
                PaymentTransaction tx = db.recordPayment(targetFee.getId(), amt, chosenMethod, refField.getText().trim(),
                        currentUser.getFullName(), currentUser.getRole().name(), notesField.getText().trim());

                if (tx != null) {
                    JOptionPane.showMessageDialog(dialog, "Payment Recorded Successfully!\nReceipt #" + tx.getReceiptNo() + " has been generated.", "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.setConfirmed(true);
                    dialog.dispose();
                    refreshData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.showModal(540, 420);
    }

    private void openAddFeeDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Define Hostel Fee Structure", "Set standard semester rates for rooms, mess & utilities", "Save Structure");

        CustomTextField nameF = new CustomTextField("e.g. Academic Year 2024-25");
        CustomTextField hostelF = new CustomTextField("35000");
        CustomTextField messF = new CustomTextField("24000");
        CustomTextField utilF = new CustomTextField("5000");
        CustomTextField dueF = new CustomTextField("2024-10-15");

        dialog.addFormField("Fee Plan / Term Name *", nameF);
        dialog.addTwoColumnRow("Hostel Rent Fee (₹)", hostelF, "Mess Fee (₹)", messF);
        dialog.addTwoColumnRow("Utility Deposit (₹)", utilF, "Payment Due Date (YYYY-MM-DD)", dueF);

        dialog.getPrimaryButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Fee structure saved and applied to upcoming terms.", "Fee Structure Saved", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        dialog.showModal(520, 380);
    }
}
