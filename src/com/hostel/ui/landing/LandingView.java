package com.hostel.ui.landing;

import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.ui.components.CustomButton;
import com.hostel.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Public Landing Page for the Smart Sustainable Hostel Management System.
 * First screen presented to visitors before signing in.
 * Designed with an elegant Pinterest-inspired warm aesthetic and live light/dark toggle.
 */
public class LandingView extends JPanel {
    private final Runnable onNavigateToLogin;
    private final JButton themeBtn;

    public LandingView(Runnable onNavigateToLogin) {
        this.onNavigateToLogin = onNavigateToLogin;
        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        // Header Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setOpaque(false);
        navBar.setBorder(new EmptyBorder(24, 40, 20, 40));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel("🌿");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel brandTitle = new JLabel("Smart Sustainable Hostel");
        brandTitle.setFont(UITheme.FONT_TITLE);
        brandTitle.setForeground(UITheme.TEXT_MAIN);

        brandPanel.add(logoIcon);
        brandPanel.add(brandTitle);

        JPanel navActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        navActions.setOpaque(false);

        themeBtn = new JButton(UITheme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
        themeBtn.setFont(UITheme.FONT_BODY_BOLD);
        themeBtn.setFocusPainted(false);
        themeBtn.setBackground(UITheme.CARD_BG);
        themeBtn.setForeground(UITheme.TEXT_MAIN);
        themeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        themeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeBtn.addActionListener(e -> {
            UITheme.toggleTheme();
            updateThemeUI();
        });

        CustomButton signInNavBtn = new CustomButton("Sign In →", CustomButton.Variant.PRIMARY);
        signInNavBtn.setPreferredSize(new Dimension(130, 40));
        signInNavBtn.addActionListener(e -> onNavigateToLogin.run());

        navActions.add(themeBtn);
        navActions.add(signInNavBtn);

        navBar.add(brandPanel, BorderLayout.WEST);
        navBar.add(navActions, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // Main Hero & Features Scroll Area
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 40, 40, 40));

        // Hero Section
        JPanel heroPanel = new JPanel();
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setOpaque(false);
        heroPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pillBadge = new JLabel("  ECO-CONSCIOUS CAMPUS LIVING & ADMINISTRATION  ");
        pillBadge.setFont(UITheme.FONT_TINY);
        pillBadge.setForeground(UITheme.PRIMARY);
        pillBadge.setBackground(UITheme.PRIMARY_LIGHT);
        pillBadge.setOpaque(true);
        pillBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        pillBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heroTitle = new JLabel("Living in Harmony with Campus & Climate");
        heroTitle.setFont(new Font("Georgia", Font.BOLD, 36));
        heroTitle.setForeground(UITheme.TEXT_MAIN);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heroSub = new JLabel("<html><div style='text-align: center; max-width: 680px;'>" +
                "A serene, software-driven residential ecosystem connecting students, wardens, and administrators. " +
                "Empowering smart room assignments, real-time energy conservation, transparent fee settlements, and automated campus welfare." +
                "</div></html>");
        heroSub.setFont(UITheme.FONT_HEADER);
        heroSub.setForeground(UITheme.TEXT_SECONDARY);
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        CustomButton heroSignInBtn = new CustomButton("Enter Portal & Sign In →", CustomButton.Variant.PRIMARY);
        heroSignInBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heroSignInBtn.setPreferredSize(new Dimension(240, 48));
        heroSignInBtn.setMaximumSize(new Dimension(240, 48));
        heroSignInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroSignInBtn.addActionListener(e -> onNavigateToLogin.run());

        heroPanel.add(Box.createVerticalStrut(10));
        heroPanel.add(pillBadge);
        heroPanel.add(Box.createVerticalStrut(16));
        heroPanel.add(heroTitle);
        heroPanel.add(Box.createVerticalStrut(12));
        heroPanel.add(heroSub);
        heroPanel.add(Box.createVerticalStrut(24));
        heroPanel.add(heroSignInBtn);
        heroPanel.add(Box.createVerticalStrut(36));

        mainContent.add(heroPanel);

        // Feature Highlights Grid (2x2)
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(1080, 380));
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);

        grid.add(createFeatureCard("🏢", "Smart Room Allocation",
                "Intelligent double & triple occupancy assignments with wing-level vacancy maps, roommate profiles, and dynamic room transfer logs."));

        grid.add(createFeatureCard("🌿", "Renewable Energy & Water Audits",
                "Real-time utility consumption meters tracking monthly kWh and kL trends with automatic anomaly detection and conservation drives."));

        grid.add(createFeatureCard("💳", "Simulated Fee Gateway & Ledgers",
                "Integrated Razorpay test-mode payments for student dues, instant verified receipts, and clear audit ledgers with zero real money risk."));

        grid.add(createFeatureCard("🛡️", "Digital Gate Passes & Welfare",
                "Paperless outstation leave applications, visitor entry pass stamping, and multi-tier room repair ticketing with technician dispatch."));

        mainContent.add(grid);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, e -> updateThemeUI());
    }

    private JPanel createFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel descLbl = new JLabel("<html><p style='width: 380px;'>" + description + "</p></html>");
        descLbl.setFont(UITheme.FONT_BODY);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(descLbl);

        return card;
    }

    private void updateThemeUI() {
        setBackground(UITheme.APP_BG);
        themeBtn.setText(UITheme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
        themeBtn.setBackground(UITheme.CARD_BG);
        themeBtn.setForeground(UITheme.TEXT_MAIN);
        themeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        repaint();
    }
}
