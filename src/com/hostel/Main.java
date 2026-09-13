package com.hostel;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.User;
import com.hostel.ui.auth.LoginView;
import com.hostel.ui.components.UITheme;
import com.hostel.ui.landing.LandingView;
import com.hostel.ui.shell.MainAppShell;

import javax.swing.*;
import java.awt.*;

/**
 * Main application launcher for Smart Sustainable Hostel Management System.
 * Initializes anti-aliased look & feel, persistent H2 database state, and orchestrates
 * public landing page, role-based authentication, and shell lifecycle.
 */
public class Main {
    private static JFrame mainFrame;
    private static CardLayout rootCardLayout;
    private static JPanel rootContainer;
    private static User currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set modern System Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Enable anti-aliased text rendering across all Swing components
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
            } catch (Exception ignored) {}

            // Pre-initialize database manager (creates H2 schema & seed data)
            DatabaseManager.getInstance();

            initAndShowGUI();
        });
    }

    private static void initAndShowGUI() {
        mainFrame = new JFrame("Smart Sustainable Hostel Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1280, 840);
        mainFrame.setMinimumSize(new Dimension(1024, 680));
        mainFrame.setLocationRelativeTo(null);

        rootCardLayout = new CardLayout();
        rootContainer = new JPanel(rootCardLayout);

        // 1. Landing View (Public first screen)
        LandingView landingView = new LandingView(Main::showLogin);
        rootContainer.add(landingView, "LANDING");

        // 2. Login View (3-Role Simplified Sign-In)
        LoginView loginView = new LoginView(Main::onLoginSuccess, Main::showLanding);
        rootContainer.add(loginView, "LOGIN");

        mainFrame.setContentPane(rootContainer);
        showLanding();
        mainFrame.setVisible(true);

        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, e -> {
            rootContainer.setBackground(UITheme.APP_BG);
            SwingUtilities.updateComponentTreeUI(mainFrame);
        });
    }

    public static void showLanding() {
        currentUser = null;
        mainFrame.setTitle("Smart Sustainable Hostel - Living in Harmony with Campus & Climate");
        rootCardLayout.show(rootContainer, "LANDING");
    }

    public static void showLogin() {
        currentUser = null;
        mainFrame.setTitle("Smart Sustainable Hostel Management System - Sign In");
        rootCardLayout.show(rootContainer, "LOGIN");
    }

    public static void onLoginSuccess(User user) {
        currentUser = user;
        mainFrame.setTitle("Smart Sustainable Hostel Management System - " + user.getFullName() + " (" + user.getRole().getDisplayName() + ")");

        MainAppShell shell = new MainAppShell(user, Main::showLanding, Main::quickSwitchRole);
        rootContainer.add(shell, "SHELL");
        rootCardLayout.show(rootContainer, "SHELL");
    }

    public static void quickSwitchRole(User.Role targetRole) {
        DatabaseManager db = DatabaseManager.getInstance();
        User targetUser;
        if (targetRole == User.Role.ADMIN) {
            targetUser = db.authenticate("admin", "admin123", User.Role.ADMIN);
        } else if (targetRole == User.Role.WARDEN) {
            targetUser = db.authenticate("warden", "warden123", User.Role.WARDEN);
        } else {
            targetUser = db.authenticate("student", "student123", User.Role.STUDENT);
        }

        if (targetUser != null) {
            onLoginSuccess(targetUser);
        }
    }
}
