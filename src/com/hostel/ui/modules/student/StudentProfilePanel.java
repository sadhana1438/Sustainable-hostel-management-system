package com.hostel.ui.modules.student;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.*;
import com.hostel.ui.components.*;
import com.hostel.ui.modules.fee.RazorpayCheckoutDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentProfilePanel extends JPanel {
    public enum Mode {
        PROFILE_ONLY,
        ROOM_ONLY,
        FEES_ONLY
    }

    private final User currentUser;
    private final Mode mode;

    public StudentProfilePanel(User user, Mode mode) {
        this.currentUser = user;
        this.mode = mode != null ? mode : Mode.PROFILE_ONLY;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        refreshContent();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.FEE_PAID, e -> refreshContent());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.ROOM_ALLOCATED, e -> refreshContent());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, e -> refreshContent());
    }

    public void refreshContent() {
        removeAll();
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        DatabaseManager db = DatabaseManager.getInstance();
        Student student = db.getStudentByStudentId(currentUser.getStudentId());
        if (student == null) {
            List<Student> all = db.getAllStudents();
            student = !all.isEmpty() ? all.get(0) : null;
        }

        if (student != null) {
            if (this.mode == Mode.PROFILE_ONLY) {
                content.add(createProfileHeader("My Student Profile", "Personal, academic, emergency contact, and residency records"));
                content.add(Box.createVerticalStrut(18));
                content.add(createProfileCard(student));
            } else if (this.mode == Mode.ROOM_ONLY) {
                content.add(createProfileHeader("My Allocated Room & Suite", "Current room allocation, block details, amenities and roommates"));
                content.add(Box.createVerticalStrut(18));
                content.add(createRoomCard(student));
            } else if (this.mode == Mode.FEES_ONLY) {
                content.add(createProfileHeader("My Fees & Payment History", "Hostel rent, mess dues, receipts, and simulated Razorpay payment gateway"));
                content.add(Box.createVerticalStrut(18));
                content.add(createFeesCard(student));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createProfileHeader(String title, String sub) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(titleLbl);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(subLbl);

        panel.add(titleGrp, BorderLayout.WEST);
        return panel;
    }

    private JPanel createProfileCard(Student student) {
        JPanel card = createBaseCard("Academic & Identity Information");

        card.add(createTwoCol("Full Legal Name", new JLabel(student.getName()), "Roll ID / Student ID", new JLabel(student.getStudentId())));
        card.add(createTwoCol("Department / Major", new JLabel(student.getDepartment()), "Current Academic Year", new JLabel(student.getYear())));
        card.add(createTwoCol("Contact Phone", new JLabel(student.getContact()), "Institutional Email", new JLabel(student.getEmail())));
        card.add(createTwoCol("Emergency Contact", new JLabel(student.getEmergencyContact()), "Parent / Guardian Phone", new JLabel(student.getParentContact())));
        card.add(createTwoCol("Blood Group", new JLabel(student.getBloodGroup()), "Residency Status", new StatusBadge(student.getStatus().name(), UITheme.SUCCESS)));
        card.add(createField("Hostel Admission Date", new JLabel(student.getAdmissionDate())));

        return card;
    }

    private JPanel createRoomCard(Student student) {
        JPanel card = createBaseCard("Room " + (student.getRoomNumber() != null ? student.getRoomNumber() : "Unassigned") + " Allocation");

        DatabaseManager db = DatabaseManager.getInstance();
        Room room = db.getRoomByNumber(student.getRoomNumber());
        if (room == null && !db.getAllRooms().isEmpty()) room = db.getAllRooms().get(0);
        final Room finalRoom = room;

        if (room != null) {
            card.add(createTwoCol("Allocated Room Number", new JLabel(room.getRoomNumber()), "Hostel Block / Wing", new JLabel(room.getBlock())));
            card.add(createTwoCol("Room Type", new JLabel(room.getRoomType()), "Floor Number", new JLabel("Floor " + room.getFloor())));
            card.add(createTwoCol("Total Bed Capacity", new JLabel(room.getCapacity() + " Beds"), "Room Status", new StatusBadge(room.getStatus().name(), UITheme.SUCCESS)));
            card.add(createField("Included Room Amenities", new JLabel(room.getAmenities() != null ? room.getAmenities() : "Air Conditioning, Study Desk, Attached Bath")));

            // Roommates List
            List<Student> roommates = db.getAllStudents().stream()
                    .filter(s -> s.getRoomId() == finalRoom.getId() && s.getId() != student.getId())
                    .toList();

            JPanel matePanel = new JPanel();
            matePanel.setLayout(new BoxLayout(matePanel, BoxLayout.Y_AXIS));
            matePanel.setOpaque(false);

            if (roommates.isEmpty()) {
                JLabel empty = new JLabel("No roommates currently assigned to this room.");
                empty.setFont(UITheme.FONT_BODY);
                empty.setForeground(UITheme.TEXT_MUTED);
                matePanel.add(empty);
            } else {
                for (Student mate : roommates) {
                    JPanel r = new JPanel(new BorderLayout(8, 0));
                    r.setOpaque(false);
                    r.setBorder(new EmptyBorder(4, 0, 4, 0));
                    JLabel l = new JLabel("<html><b>" + mate.getName() + "</b> (" + mate.getStudentId() + ") • " + mate.getDepartment() + " • Phone: " + mate.getContact() + "</html>");
                    l.setFont(UITheme.FONT_BODY);
                    l.setForeground(UITheme.TEXT_MAIN);
                    r.add(l, BorderLayout.CENTER);
                    matePanel.add(r);
                }
            }

            card.add(createField("My Roommates", matePanel));
        }

        return card;
    }

    private JPanel createFeesCard(Student student) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        DatabaseManager db = DatabaseManager.getInstance();
        FeeRecord fee = db.getFeeRecordForStudent(student.getId());
        if (fee == null && !db.getAllFeeRecords().isEmpty()) fee = db.getAllFeeRecords().get(0);

        if (fee != null) {
            JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
            stats.setOpaque(false);
            stats.add(new StatCard("Total Invoiced", String.format("₹%,.0f", fee.getTotalFee()), fee.getAcademicTerm(), UITheme.PRIMARY, "📜"));
            stats.add(new StatCard("Paid Amount", String.format("₹%,.0f", fee.getPaidAmount()), "Settled", UITheme.SUCCESS, "💰"));
            stats.add(new StatCard("Balance Due", String.format("₹%,.0f", fee.getBalance()), "Due: " + fee.getDueDate(), fee.getBalance() > 0 ? UITheme.DANGER : UITheme.SUCCESS, "⏳"));
            stats.add(new StatCard("Payment Status", fee.getStatus().getLabel(), "Hostel Account", fee.getStatus() == FeeRecord.FeeStatus.PAID ? UITheme.SUCCESS : UITheme.WARNING, "✅"));

            container.add(stats);
            container.add(Box.createVerticalStrut(18));

            // Razorpay Payment Action Card if pending dues
            if (fee.getBalance() > 0) {
                JPanel payCard = new JPanel(new BorderLayout(16, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        UITheme.applyQualityRendering(g2);
                        g2.setColor(UITheme.CARD_BG);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                        g2.setColor(UITheme.PRIMARY);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                payCard.setOpaque(false);
                payCard.setBorder(new EmptyBorder(16, 20, 16, 20));

                JPanel textGroup = new JPanel();
                textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
                textGroup.setOpaque(false);

                JLabel pTitle = new JLabel("Pending Dues Settlement: " + String.format("₹%,.0f", fee.getBalance()));
                pTitle.setFont(UITheme.FONT_SUBHEADER);
                pTitle.setForeground(UITheme.PRIMARY);

                JLabel pSub = new JLabel("Settle your outstanding term dues seamlessly via the integrated Razorpay Test Gateway (UPI / Card / NetBanking).");
                pSub.setFont(UITheme.FONT_SMALL);
                pSub.setForeground(UITheme.TEXT_SECONDARY);

                textGroup.add(pTitle);
                textGroup.add(Box.createVerticalStrut(3));
                textGroup.add(pSub);

                CustomButton payNowBtn = new CustomButton("💳 Pay Now (Razorpay Test Mode)", CustomButton.Variant.PRIMARY);
                payNowBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                payNowBtn.setPreferredSize(new Dimension(260, 42));

                final FeeRecord finalFee = fee;
                payNowBtn.addActionListener(e -> {
                    RazorpayCheckoutDialog.showPayment(SwingUtilities.getWindowAncestor(this), finalFee, tx -> refreshContent());
                });

                payCard.add(textGroup, BorderLayout.CENTER);
                payCard.add(payNowBtn, BorderLayout.EAST);

                container.add(payCard);
                container.add(Box.createVerticalStrut(18));
            }

            // Payment History Table
            JPanel card = createBaseCard("My Payment History & Transaction Receipts");
            List<PaymentTransaction> txs = db.getPaymentTransactionsForStudent(student.getId());

            String[] cols = {"Receipt No", "Amount Paid", "Payment Method", "Transaction Ref", "Payment Date", "Recorded By"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (PaymentTransaction tx : txs) {
                model.addRow(new Object[]{
                        tx.getReceiptNo(),
                        String.format("₹%,.2f", tx.getAmount()),
                        tx.getPaymentMethod().getLabel(),
                        tx.getTransactionRef() != null ? tx.getTransactionRef() : "-",
                        tx.getPaymentDate(),
                        tx.getRecordedBy()
                });
            }

            CustomTable table = new CustomTable(model);
            JScrollPane scroll = CustomTable.wrapInScrollPane(table);
            scroll.setPreferredSize(new Dimension(0, 220));

            card.add(scroll);
            container.add(card);
        }

        return container;
    }

    private JPanel createBaseCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
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
}
