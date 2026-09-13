package com.hostel.ui.modules.fee;

import com.hostel.db.DatabaseManager;
import com.hostel.model.FeeRecord;
import com.hostel.model.PaymentTransaction;
import com.hostel.ui.components.CustomButton;
import com.hostel.ui.components.CustomTextField;
import com.hostel.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Razorpay Test-Mode Checkout Dialog for Student Fee Payment.
 * Loads test API key from config.properties or environment variable.
 * Simulates a realistic test gateway flow (UPI, Test Card, NetBanking)
 * without moving real money.
 */
public class RazorpayCheckoutDialog extends JDialog {
    private static String razorpayKeyId = "rzp_test_HostelDemo2026Key";

    static {
        // Attempt to load from environment variable first, then config.properties
        String envKey = System.getenv("RAZORPAY_KEY_ID");
        if (envKey != null && !envKey.trim().isEmpty()) {
            razorpayKeyId = envKey.trim();
        } else {
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                Properties props = new Properties();
                props.load(fis);
                String propKey = props.getProperty("razorpay.key_id");
                if (propKey != null && !propKey.trim().isEmpty()) {
                    razorpayKeyId = propKey.trim();
                }
            } catch (Exception ignored) {}
        }
    }

    public static void showPayment(Window owner, FeeRecord fee, Consumer<PaymentTransaction> onPaymentSuccess) {
        RazorpayCheckoutDialog dialog = new RazorpayCheckoutDialog(owner, fee, onPaymentSuccess);
        dialog.setVisible(true);
    }

    private final FeeRecord fee;
    private final Consumer<PaymentTransaction> onSuccess;

    public RazorpayCheckoutDialog(Window owner, FeeRecord fee, Consumer<PaymentTransaction> onSuccess) {
        super(owner, "Razorpay Secure Checkout [TEST MODE]", ModalityType.APPLICATION_MODAL);
        this.fee = fee;
        this.onSuccess = onSuccess;

        setSize(480, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.CARD_BG);

        // Razorpay Test Header Banner
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(11, 40, 70)); // Razorpay signature dark blue
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandRow.setOpaque(false);

        JLabel rzpLogo = new JLabel("⚡ Razorpay");
        rzpLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rzpLogo.setForeground(Color.WHITE);

        JLabel testBadge = new JLabel(" TEST MODE ");
        testBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        testBadge.setForeground(new Color(254, 240, 138)); // yellow
        testBadge.setBackground(new Color(30, 58, 138));
        testBadge.setOpaque(true);
        testBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 240, 138), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        brandRow.add(rzpLogo);
        brandRow.add(testBadge);

        JLabel merchantLbl = new JLabel("Smart Sustainable Hostel");
        merchantLbl.setFont(UITheme.FONT_SMALL);
        merchantLbl.setForeground(new Color(191, 219, 254));

        header.add(brandRow, BorderLayout.NORTH);
        header.add(merchantLbl, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        // Body Content
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UITheme.CARD_BG);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Fee Summary Box
        JPanel summaryBox = new JPanel(new BorderLayout(12, 6));
        summaryBox.setBackground(UITheme.APP_BG);
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel sumLeft = new JPanel();
        sumLeft.setLayout(new BoxLayout(sumLeft, BoxLayout.Y_AXIS));
        sumLeft.setOpaque(false);

        JLabel sName = new JLabel(fee.getStudentName() + " (" + fee.getStudentRollNo() + ")");
        sName.setFont(UITheme.FONT_BODY_BOLD);
        sName.setForeground(UITheme.TEXT_MAIN);

        JLabel sTerm = new JLabel(fee.getAcademicTerm() + " • Room: " + fee.getRoomNumber());
        sTerm.setFont(UITheme.FONT_SMALL);
        sTerm.setForeground(UITheme.TEXT_MUTED);

        JLabel keyIdLbl = new JLabel("Key: " + razorpayKeyId);
        keyIdLbl.setFont(UITheme.FONT_TINY);
        keyIdLbl.setForeground(UITheme.TEXT_MUTED);

        sumLeft.add(sName);
        sumLeft.add(Box.createVerticalStrut(2));
        sumLeft.add(sTerm);
        sumLeft.add(Box.createVerticalStrut(2));
        sumLeft.add(keyIdLbl);

        JLabel amountLbl = new JLabel(String.format("₹%,.0f", fee.getBalance()));
        amountLbl.setFont(new Font("Georgia", Font.BOLD, 24));
        amountLbl.setForeground(UITheme.PRIMARY);

        summaryBox.add(sumLeft, BorderLayout.CENTER);
        summaryBox.add(amountLbl, BorderLayout.EAST);
        body.add(summaryBox);
        body.add(Box.createVerticalStrut(18));

        // Payment Method Tabs (UPI, Cards, NetBanking)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);

        // 1. UPI Tab
        JPanel upiPanel = new JPanel();
        upiPanel.setLayout(new BoxLayout(upiPanel, BoxLayout.Y_AXIS));
        upiPanel.setBackground(UITheme.CARD_BG);
        upiPanel.setBorder(new EmptyBorder(14, 12, 12, 12));

        JLabel upiPrompt = new JLabel("Enter Simulated Virtual Payment Address (VPA)");
        upiPrompt.setFont(UITheme.FONT_SMALL);
        upiPrompt.setForeground(UITheme.TEXT_SECONDARY);

        CustomTextField upiField = new CustomTextField("success@razorpay");
        upiField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel upiNote = new JLabel("<html><small style='color: #6E5D53;'>Test VPA: <b>success@razorpay</b> always approves immediately.</small></html>");

        upiPanel.add(upiPrompt);
        upiPanel.add(Box.createVerticalStrut(6));
        upiPanel.add(upiField);
        upiPanel.add(Box.createVerticalStrut(6));
        upiPanel.add(upiNote);

        tabs.addTab("UPI / QR", upiPanel);

        // 2. Card Tab
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(UITheme.CARD_BG);
        cardPanel.setBorder(new EmptyBorder(14, 12, 12, 12));

        JLabel cardPrompt = new JLabel("Test Card Number");
        cardPrompt.setFont(UITheme.FONT_SMALL);
        cardPrompt.setForeground(UITheme.TEXT_SECONDARY);

        CustomTextField cardField = new CustomTextField("4111 1111 1111 1111");
        cardField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel cardSub = new JPanel(new GridLayout(1, 2, 10, 0));
        cardSub.setOpaque(false);
        CustomTextField expField = new CustomTextField("12/28");
        CustomTextField cvvField = new CustomTextField("123");
        cardSub.add(expField);
        cardSub.add(cvvField);
        cardSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        cardPanel.add(cardPrompt);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(cardField);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(cardSub);

        tabs.addTab("Test Cards", cardPanel);

        // 3. NetBanking Tab
        JPanel nbPanel = new JPanel();
        nbPanel.setLayout(new BoxLayout(nbPanel, BoxLayout.Y_AXIS));
        nbPanel.setBackground(UITheme.CARD_BG);
        nbPanel.setBorder(new EmptyBorder(14, 12, 12, 12));

        JLabel nbPrompt = new JLabel("Select Test Bank");
        nbPrompt.setFont(UITheme.FONT_SMALL);
        nbPrompt.setForeground(UITheme.TEXT_SECONDARY);

        JComboBox<String> bankCb = new JComboBox<>(new String[]{"HDFC Test Bank (Instant Success)", "SBI Test Gateway", "ICICI Corporate Sandbox"});
        bankCb.setFont(UITheme.FONT_BODY);
        bankCb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        nbPanel.add(nbPrompt);
        nbPanel.add(Box.createVerticalStrut(6));
        nbPanel.add(bankCb);

        tabs.addTab("NetBanking", nbPanel);

        body.add(tabs);
        body.add(Box.createVerticalStrut(18));

        // Submit Button
        CustomButton payBtn = new CustomButton("Pay ₹" + String.format("%,.0f", fee.getBalance()) + " [Test Mode] →", CustomButton.Variant.PRIMARY);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        payBtn.addActionListener(e -> processSimulatedPayment());

        body.add(payBtn);
        root.add(body, BorderLayout.CENTER);

        // Footer Note
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        foot.setBackground(UITheme.APP_BG);
        foot.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel footLbl = new JLabel("🔒 Simulated Sandbox Gateway • No Real Financial Transaction");
        footLbl.setFont(UITheme.FONT_TINY);
        footLbl.setForeground(UITheme.TEXT_MUTED);
        foot.add(footLbl);
        root.add(foot, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void processSimulatedPayment() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Generate authentic Razorpay test transaction ref
        String payTestRef = "pay_test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        DatabaseManager db = DatabaseManager.getInstance();
        PaymentTransaction tx = db.recordPayment(
                fee.getId(),
                fee.getBalance(),
                PaymentTransaction.PaymentMethod.ONLINE_GATEWAY,
                payTestRef,
                fee.getStudentName()
        );

        setCursor(Cursor.getDefaultCursor());
        dispose();

        if (tx != null) {
            JOptionPane.showMessageDialog(
                    getOwner(),
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<h3 style='color: #3D8B68;'>✓ Payment Successful!</h3>" +
                            "<p>Amount Paid: <b>₹" + String.format("%,.2f", tx.getAmount()) + "</b></p>" +
                            "<p>Razorpay Transaction ID: <code>" + tx.getTransactionRef() + "</code></p>" +
                            "<p>Receipt Number: <b>" + tx.getReceiptNo() + "</b></p>" +
                            "<p style='color: #6E5D53; font-size: 11px;'>Hostel fee balance settled. Status is now PAID.</p>" +
                            "</div></html>",
                    "Fee Payment Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (onSuccess != null) {
                onSuccess.accept(tx);
            }
        } else {
            JOptionPane.showMessageDialog(getOwner(), "Failed to record payment in database.", "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
