package com.hostel.ui.auth;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Simplified 2-Step Sign-In Flow:
 * Step 1: Exactly 3 simple choices (Admin, Warden, Student).
 * Step 2: Clean username/password form for the selected role.
 * Removed demo quick switch chips and verbose dropdowns.
 */
public class LoginView extends JPanel {
    private final Consumer<User> onLoginSuccess;
    private final Runnable onBackToLanding;

    private final CardLayout stepCardLayout;
    private final JPanel stepContainer;

    private User.Role selectedRole = User.Role.ADMIN;
    private final JLabel step2RoleLabel;
    private final CustomTextField usernameField;
    private final CustomPasswordField passwordField;
    private final JPanel errorBanner;
    private final JLabel errorLabel;
    private final CustomButton loginBtn;

    public LoginView(Consumer<User> onLoginSuccess, Runnable onBackToLanding) {
        this.onLoginSuccess = onLoginSuccess;
        this.onBackToLanding = onBackToLanding;

        setLayout(new GridBagLayout());
        setBackground(UITheme.APP_BG);

        // Main Card Container
        JPanel mainCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainCard.setOpaque(false);
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setBorder(new EmptyBorder(36, 36, 36, 36));
        mainCard.setPreferredSize(new Dimension(480, 520));

        // Brand Banner Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoBadge = new JLabel("🌿 SMART SUSTAINABLE HOSTEL", SwingConstants.CENTER);
        logoBadge.setFont(UITheme.FONT_TINY);
        logoBadge.setForeground(UITheme.PRIMARY);
        logoBadge.setBackground(UITheme.PRIMARY_LIGHT);
        logoBadge.setOpaque(true);
        logoBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Campus Living Portal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign In to Access Your Workspace");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(logoBadge);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(title);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subtitle);
        brandPanel.add(Box.createVerticalStrut(20));

        mainCard.add(brandPanel);

        // Step Card Container (Step 1: Role Picker, Step 2: Credentials)
        stepCardLayout = new CardLayout();
        stepContainer = new JPanel(stepCardLayout);
        stepContainer.setOpaque(false);

        // ==========================================
        // STEP 1: ROLE SELECTION (3 Simple Choices)
        // ==========================================
        JPanel step1RoleSelect = new JPanel();
        step1RoleSelect.setLayout(new BoxLayout(step1RoleSelect, BoxLayout.Y_AXIS));
        step1RoleSelect.setOpaque(false);

        JLabel rolePickPrompt = new JLabel("Select your portal role to continue:");
        rolePickPrompt.setFont(UITheme.FONT_SUBHEADER);
        rolePickPrompt.setForeground(UITheme.TEXT_MAIN);
        rolePickPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        step1RoleSelect.add(rolePickPrompt);
        step1RoleSelect.add(Box.createVerticalStrut(16));

        step1RoleSelect.add(createRoleChoiceCard("Admin", "⚙️", "Full institutional control & configuration", User.Role.ADMIN));
        step1RoleSelect.add(Box.createVerticalStrut(10));
        step1RoleSelect.add(createRoleChoiceCard("Warden", "🛡️", "Resident allocations, gate passes & welfare", User.Role.WARDEN));
        step1RoleSelect.add(Box.createVerticalStrut(10));
        step1RoleSelect.add(createRoleChoiceCard("Student", "🎓", "Personal account, room, dues & leave requests", User.Role.STUDENT));

        step1RoleSelect.add(Box.createVerticalStrut(18));

        JButton backToHomeBtn = new JButton("← Back to Landing Page");
        backToHomeBtn.setFont(UITheme.FONT_SMALL);
        backToHomeBtn.setForeground(UITheme.TEXT_SECONDARY);
        backToHomeBtn.setContentAreaFilled(false);
        backToHomeBtn.setBorderPainted(false);
        backToHomeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToHomeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToHomeBtn.addActionListener(e -> onBackToLanding.run());
        step1RoleSelect.add(backToHomeBtn);

        stepContainer.add(step1RoleSelect, "STEP_ROLES");

        // ==========================================
        // STEP 2: CREDENTIALS INPUT FORM
        // ==========================================
        JPanel step2Credentials = new JPanel();
        step2Credentials.setLayout(new BoxLayout(step2Credentials, BoxLayout.Y_AXIS));
        step2Credentials.setOpaque(false);

        JPanel roleHeaderRow = new JPanel(new BorderLayout());
        roleHeaderRow.setOpaque(false);
        roleHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        step2RoleLabel = new JLabel("Signing in as Admin");
        step2RoleLabel.setFont(UITheme.FONT_SUBHEADER);
        step2RoleLabel.setForeground(UITheme.TEXT_MAIN);

        JButton changeRoleBtn = new JButton("Change Role ↺");
        changeRoleBtn.setFont(UITheme.FONT_TINY);
        changeRoleBtn.setForeground(UITheme.PRIMARY);
        changeRoleBtn.setContentAreaFilled(false);
        changeRoleBtn.setBorderPainted(false);
        changeRoleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeRoleBtn.addActionListener(e -> {
            clearError();
            stepCardLayout.show(stepContainer, "STEP_ROLES");
        });

        roleHeaderRow.add(step2RoleLabel, BorderLayout.WEST);
        roleHeaderRow.add(changeRoleBtn, BorderLayout.EAST);

        step2Credentials.add(roleHeaderRow);
        step2Credentials.add(Box.createVerticalStrut(12));

        // Error Banner
        errorBanner = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.DANGER_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(UITheme.DANGER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        errorBanner.setOpaque(false);
        errorBanner.setBorder(new EmptyBorder(8, 12, 8, 12));
        errorBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        errorBanner.setVisible(false);

        errorLabel = new JLabel("Invalid credentials or role mismatch.");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER_TEXT);
        errorBanner.add(errorLabel, BorderLayout.CENTER);

        step2Credentials.add(errorBanner);
        step2Credentials.add(Box.createVerticalStrut(10));

        // Username Field
        JLabel userLbl = new JLabel("Username / Roll No");
        userLbl.setFont(UITheme.FONT_BODY_BOLD);
        userLbl.setForeground(UITheme.TEXT_MAIN);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new CustomTextField("e.g. admin or student roll");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Password Field
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UITheme.FONT_BODY_BOLD);
        passLbl.setForeground(UITheme.TEXT_MAIN);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new CustomPasswordField("Password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        KeyAdapter enterSubmit = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        };
        usernameField.addKeyListener(enterSubmit);
        passwordField.addKeyListener(enterSubmit);

        step2Credentials.add(userLbl);
        step2Credentials.add(Box.createVerticalStrut(4));
        step2Credentials.add(usernameField);
        step2Credentials.add(Box.createVerticalStrut(12));
        step2Credentials.add(passLbl);
        step2Credentials.add(Box.createVerticalStrut(4));
        step2Credentials.add(passwordField);
        step2Credentials.add(Box.createVerticalStrut(18));

        loginBtn = new CustomButton("Sign In to Workspace →", CustomButton.Variant.PRIMARY);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());

        step2Credentials.add(loginBtn);
        step2Credentials.add(Box.createVerticalStrut(12));

        JButton backBtn = new JButton("← Choose Different Role");
        backBtn.setFont(UITheme.FONT_SMALL);
        backBtn.setForeground(UITheme.TEXT_SECONDARY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            clearError();
            stepCardLayout.show(stepContainer, "STEP_ROLES");
        });
        step2Credentials.add(backBtn);

        stepContainer.add(step2Credentials, "STEP_FORM");

        mainCard.add(stepContainer);
        add(mainCard);

        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, e -> {
            setBackground(UITheme.APP_BG);
            repaint();
        });
    }

    private JPanel createRoleChoiceCard(String roleName, String icon, String desc, User.Role role) {
        JPanel panel = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textGrp = new JPanel();
        textGrp.setLayout(new BoxLayout(textGrp, BoxLayout.Y_AXIS));
        textGrp.setOpaque(false);

        JLabel nameLbl = new JLabel(roleName);
        nameLbl.setFont(UITheme.FONT_SUBHEADER);
        nameLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UITheme.FONT_TINY);
        descLbl.setForeground(UITheme.TEXT_MUTED);

        textGrp.add(nameLbl);
        textGrp.add(Box.createVerticalStrut(2));
        textGrp.add(descLbl);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 16));
        arrow.setForeground(UITheme.PRIMARY);

        panel.add(iconLbl, BorderLayout.WEST);
        panel.add(textGrp, BorderLayout.CENTER);
        panel.add(arrow, BorderLayout.EAST);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectRoleAndAdvance(role, roleName);
            }
        });

        return panel;
    }

    private void selectRoleAndAdvance(User.Role role, String roleName) {
        this.selectedRole = role;
        step2RoleLabel.setText("Signing in as " + roleName);
        clearError();

        // Default hint credentials for ease of testing
        if (role == User.Role.ADMIN) {
            usernameField.setText("admin");
            passwordField.setText("admin123");
        } else if (role == User.Role.WARDEN) {
            usernameField.setText("warden");
            passwordField.setText("warden123");
        } else {
            usernameField.setText("student");
            passwordField.setText("student123");
        }

        stepCardLayout.show(stepContainer, "STEP_FORM");
        usernameField.requestFocusInWindow();
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        DatabaseManager db = DatabaseManager.getInstance();
        User user = db.authenticate(username, password, selectedRole);

        if (user != null) {
            clearError();
            onLoginSuccess.accept(user);
        } else {
            showError("Invalid username, password, or account does not have " + selectedRole.getDisplayName() + " access.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorBanner.setVisible(true);
        revalidate();
        repaint();
    }

    private void clearError() {
        errorBanner.setVisible(false);
        revalidate();
        repaint();
    }
}
