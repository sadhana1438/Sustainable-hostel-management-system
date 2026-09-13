package com.hostel.ui.shell;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Notification;
import com.hostel.model.User;
import com.hostel.ui.components.CustomButton;
import com.hostel.ui.components.StatusBadge;
import com.hostel.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class AppHeader extends JPanel {
    private final JLabel pageTitleLabel;
    private final JLabel pageSubtitleLabel;
    private final JLabel userAvatarLabel;
    private final JLabel userNameLabel;
    private final StatusBadge userRoleBadge;
    private final JPanel notifBellButton;
    private final JLabel notifBadgeLabel;
    private final User currentUser;
    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final Consumer<User.Role> onQuickSwitchRole;

    public AppHeader(User user, Consumer<String> onNavigate, Runnable onLogout, Consumer<User.Role> onQuickSwitchRole) {
        this.currentUser = user;
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;
        this.onQuickSwitchRole = onQuickSwitchRole;

        setLayout(new BorderLayout(16, 0));
        setBackground(UITheme.HEADER_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(12, 24, 12, 24)
        ));
        setPreferredSize(new Dimension(0, 68));

        // Left Section: Page Title & Breadcrumb/Hostel info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        pageTitleLabel = new JLabel("Dashboard Overview");
        pageTitleLabel.setFont(UITheme.FONT_HEADER);
        pageTitleLabel.setForeground(UITheme.TEXT_MAIN);

        pageSubtitleLabel = new JLabel("Smart Sustainable Hostel Management System");
        pageSubtitleLabel.setFont(UITheme.FONT_TINY);
        pageSubtitleLabel.setForeground(UITheme.TEXT_MUTED);

        leftPanel.add(pageTitleLabel);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(pageSubtitleLabel);

        // Right Section: Notification Bell + User Profile Dropdown
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);

        // Notification Bell Icon Widget with Badge Counter
        notifBellButton = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        notifBellButton.setPreferredSize(new Dimension(40, 40));
        notifBellButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notifBellButton.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel bellIcon = new JLabel("🔔", SwingConstants.CENTER);
        bellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        notifBellButton.add(bellIcon, BorderLayout.CENTER);

        notifBadgeLabel = new JLabel("0", SwingConstants.CENTER);
        notifBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        notifBadgeLabel.setForeground(Color.WHITE);
        notifBadgeLabel.setOpaque(true);
        notifBadgeLabel.setBackground(UITheme.DANGER);
        notifBadgeLabel.setBorder(new EmptyBorder(1, 4, 1, 4));
        notifBadgeLabel.setVisible(false);
        notifBellButton.add(notifBadgeLabel, BorderLayout.EAST);

        notifBellButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNotificationPopup(notifBellButton);
            }
        });

        // User Profile Pill
        JPanel profilePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        profilePill.setOpaque(false);
        profilePill.setCursor(new Cursor(Cursor.HAND_CURSOR));

        userAvatarLabel = new JLabel(user.getAvatarInitial(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userAvatarLabel.setPreferredSize(new Dimension(36, 36));
        userAvatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userAvatarLabel.setForeground(Color.WHITE);

        JPanel userTextGroup = new JPanel();
        userTextGroup.setLayout(new BoxLayout(userTextGroup, BoxLayout.Y_AXIS));
        userTextGroup.setOpaque(false);

        userNameLabel = new JLabel(user.getFullName());
        userNameLabel.setFont(UITheme.FONT_BODY_BOLD);
        userNameLabel.setForeground(UITheme.TEXT_MAIN);

        Color roleBadgeColor = UITheme.PRIMARY;
        if (user.getRole() == User.Role.WARDEN) roleBadgeColor = UITheme.INFO;
        else if (user.getRole() == User.Role.STUDENT) roleBadgeColor = UITheme.SUCCESS;
        userRoleBadge = new StatusBadge(user.getRole().getDisplayName(), roleBadgeColor);

        userTextGroup.add(userNameLabel);
        userTextGroup.add(Box.createVerticalStrut(2));
        userTextGroup.add(userRoleBadge);

        profilePill.add(userAvatarLabel);
        profilePill.add(userTextGroup);

        profilePill.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserProfileMenu(profilePill);
            }
        });

        // Whole-App Light/Dark Mode Toggle Button
        JButton headerThemeBtn = new JButton(UITheme.isDarkMode() ? "☀️" : "🌙");
        headerThemeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        headerThemeBtn.setToolTipText(UITheme.isDarkMode() ? "Switch to Light Mode" : "Switch to Dark Mode");
        headerThemeBtn.setFocusPainted(false);
        headerThemeBtn.setBackground(UITheme.CARD_BG);
        headerThemeBtn.setForeground(UITheme.TEXT_MAIN);
        headerThemeBtn.setPreferredSize(new Dimension(40, 40));
        headerThemeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerThemeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(6, 6, 6, 6)
        ));
        headerThemeBtn.addActionListener(e -> {
            UITheme.toggleTheme();
            headerThemeBtn.setText(UITheme.isDarkMode() ? "☀️" : "🌙");
            headerThemeBtn.setToolTipText(UITheme.isDarkMode() ? "Switch to Light Mode" : "Switch to Dark Mode");
        });

        rightPanel.add(headerThemeBtn);
        rightPanel.add(notifBellButton);
        rightPanel.add(profilePill);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        refreshNotificationBadge();

        // Subscribe to event bus for real-time unread updates & theme changes
        AppEventBus.getInstance().subscribe(AppEvent.EventType.NOTIFICATION_RECEIVED, ev -> refreshNotificationBadge());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, ev -> {
            setBackground(UITheme.HEADER_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                    new EmptyBorder(12, 24, 12, 24)
            ));
            pageTitleLabel.setForeground(UITheme.TEXT_MAIN);
            pageSubtitleLabel.setForeground(UITheme.TEXT_MUTED);
            userNameLabel.setForeground(UITheme.TEXT_MAIN);
            headerThemeBtn.setText(UITheme.isDarkMode() ? "☀️" : "🌙");
            headerThemeBtn.setBackground(UITheme.CARD_BG);
            headerThemeBtn.setForeground(UITheme.TEXT_MAIN);
            repaint();
        });
        AppEventBus.getInstance().subscribe(AppEvent.EventType.LEAVE_APPROVED, ev -> refreshNotificationBadge());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.FEE_PAID, ev -> refreshNotificationBadge());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.COMPLAINT_FILED, ev -> refreshNotificationBadge());
    }

    public void setPageTitle(String title, String subtitle) {
        pageTitleLabel.setText(title);
        pageSubtitleLabel.setText(subtitle != null ? subtitle : "Smart Sustainable Hostel Management System");
        revalidate();
        repaint();
    }

    public void refreshNotificationBadge() {
        int count = DatabaseManager.getInstance().getUnreadNotificationCount(currentUser);
        if (count > 0) {
            notifBadgeLabel.setText(String.valueOf(count));
            notifBadgeLabel.setVisible(true);
        } else {
            notifBadgeLabel.setVisible(false);
        }
        notifBellButton.revalidate();
        notifBellButton.repaint();
    }

    private void showNotificationPopup(Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        popup.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(248, 250, 252));
        header.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Recent Notifications");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel viewAll = new JLabel("View All →");
        viewAll.setFont(UITheme.FONT_TINY);
        viewAll.setForeground(UITheme.PRIMARY);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popup.setVisible(false);
                if (onNavigate != null) onNavigate.accept("NOTIFICATIONS");
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);
        popup.add(header);

        List<Notification> notifs = DatabaseManager.getInstance().getNotificationsForUser(currentUser);
        int max = Math.min(notifs.size(), 4);
        if (max == 0) {
            JMenuItem empty = new JMenuItem("No recent notifications");
            empty.setEnabled(false);
            empty.setFont(UITheme.FONT_SMALL);
            popup.add(empty);
        } else {
            for (int i = 0; i < max; i++) {
                Notification n = notifs.get(i);
                JMenuItem item = new JMenuItem(String.format("<html><b>%s</b><br><font color='#64748b' size='2'>%s</font></html>",
                        n.getTitle(), n.getMessage().length() > 40 ? n.getMessage().substring(0, 40) + "..." : n.getMessage()));
                item.setFont(UITheme.FONT_SMALL);
                item.setBackground(Color.WHITE);
                item.setBorder(new EmptyBorder(6, 12, 6, 12));
                item.addActionListener(e -> {
                    DatabaseManager.getInstance().markNotificationAsRead(n.getId());
                    refreshNotificationBadge();
                    if (onNavigate != null) onNavigate.accept("NOTIFICATIONS");
                });
                popup.add(item);
            }
        }

        popup.show(invoker, invoker.getWidth() - 260, invoker.getHeight() + 4);
    }

    private void showUserProfileMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));

        JMenuItem profileItem = new JMenuItem("Signed in as " + currentUser.getFullName() + " (" + currentUser.getRole().getDisplayName() + ")");
        profileItem.setFont(UITheme.FONT_TINY);
        profileItem.setEnabled(false);
        menu.add(profileItem);
        menu.addSeparator();

        // Switch Role Quick Demo Helpers
        JMenu switchRole = new JMenu("Switch Workspace Mode...");
        switchRole.setFont(UITheme.FONT_SMALL);

        JMenuItem toAdmin = new JMenuItem("Administrator Mode");
        toAdmin.addActionListener(e -> { if (onQuickSwitchRole != null) onQuickSwitchRole.accept(User.Role.ADMIN); });

        JMenuItem toWarden = new JMenuItem("Warden Mode");
        toWarden.addActionListener(e -> { if (onQuickSwitchRole != null) onQuickSwitchRole.accept(User.Role.WARDEN); });

        JMenuItem toStudent = new JMenuItem("Student Mode");
        toStudent.addActionListener(e -> { if (onQuickSwitchRole != null) onQuickSwitchRole.accept(User.Role.STUDENT); });

        switchRole.add(toAdmin);
        switchRole.add(toWarden);
        switchRole.add(toStudent);
        menu.add(switchRole);

        menu.addSeparator();

        JMenuItem logoutItem = new JMenuItem("Logout Session");
        logoutItem.setFont(UITheme.FONT_BODY_BOLD);
        logoutItem.setForeground(UITheme.DANGER);
        logoutItem.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        menu.add(logoutItem);

        menu.show(invoker, 0, invoker.getHeight() + 4);
    }
}
