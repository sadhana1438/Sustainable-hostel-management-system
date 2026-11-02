package com.hostel.ui.shell;

import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.User;
import com.hostel.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppSidebar extends JPanel {
    public static class NavItem {
        public final String id;
        public final String label;
        public final String icon;

        public NavItem(String id, String label, String icon) {
            this.id = id;
            this.label = label;
            this.icon = icon;
        }
    }

    private final User currentUser;
    private final Consumer<String> onNavSelected;
    private final Runnable onLogout;
    private final List<NavItemPanel> navItemPanels = new ArrayList<>();
    private String activeNavId = "DASHBOARD";

    public AppSidebar(User user, Consumer<String> onNavSelected, Runnable onLogout) {
        this.currentUser = user;
        this.onNavSelected = onNavSelected;
        this.onLogout = onLogout;

        setLayout(new BorderLayout());
        setBackground(UITheme.SIDEBAR_BG);
        setPreferredSize(new Dimension(240, 0));

        // Brand Banner
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.SIDEBAR_BG);
        brandPanel.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel logoBadge = new JLabel("🌿 ECO-LIVING", SwingConstants.LEFT);
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        logoBadge.setForeground(new Color(129, 140, 248)); // Indigo 400

        JLabel appTitle = new JLabel("Hostel Suite");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(user.getRole().getDisplayName().toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLabel.setForeground(UITheme.SIDEBAR_MUTED);

        brandPanel.add(logoBadge);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(appTitle);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(roleLabel);

        // Navigation Items List
        JPanel navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(UITheme.SIDEBAR_BG);
        navListPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        List<NavItem> items = getNavItemsForRole(user.getRole());
        for (NavItem item : items) {
            NavItemPanel panel = new NavItemPanel(item);
            navItemPanels.add(panel);
            navListPanel.add(panel);
            navListPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollPane = new JScrollPane(navListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.SIDEBAR_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        // Footer with Logout
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(11, 17, 32)); // Darker bottom
        footerPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        NavItemPanel logoutItem = new NavItemPanel(new NavItem("LOGOUT", "Logout", "⏻"));
        logoutItem.setLogoutStyle();
        footerPanel.add(logoutItem, BorderLayout.CENTER);

        add(brandPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // Set initial active state
        setActiveNav("DASHBOARD");

        AppEventBus.getInstance().subscribe(AppEvent.EventType.THEME_CHANGED, ev -> {
            setBackground(UITheme.SIDEBAR_BG);
            brandPanel.setBackground(UITheme.SIDEBAR_BG);
            navListPanel.setBackground(UITheme.SIDEBAR_BG);
            scrollPane.getViewport().setBackground(UITheme.SIDEBAR_BG);
            footerPanel.setBackground(UITheme.isDarkMode() ? new Color(18, 14, 12) : new Color(36, 28, 25));
            for (NavItemPanel p : navItemPanels) {
                p.setActive(p == navItemPanels.stream().filter(np -> np.item.id.equals(activeNavId)).findFirst().orElse(null));
            }
            repaint();
        });
    }

    private List<NavItem> getNavItemsForRole(User.Role role) {
        List<NavItem> list = new ArrayList<>();
        list.add(new NavItem("DASHBOARD", "Dashboard", "📊"));

        if (role == User.Role.ADMIN) {
            list.add(new NavItem("STUDENTS", "Student Management", "🎓"));
            list.add(new NavItem("ROOMS", "Bed Allocation", "🛏"));
            list.add(new NavItem("FEES", "Hostel Fee Mgmt", "💳"));
            list.add(new NavItem("VISITORS", "Visitor Management", "👥"));
            list.add(new NavItem("LEAVES", "Leave Management", "🗓"));
            list.add(new NavItem("COMPLIANCE", "Compliance & Maint", "🛠"));
            list.add(new NavItem("SUSTAINABILITY", "Electricity & Water", "⚡"));
            list.add(new NavItem("INVENTORY", "Inventory & Assets", "📦"));
            list.add(new NavItem("NOTICES", "Notices & News", "📢"));
            list.add(new NavItem("MESS", "Mess Management", "🍽"));
            list.add(new NavItem("NOTIFICATIONS", "Notifications", "🔔"));
            list.add(new NavItem("REPORTS", "Reports & Analytics", "📈"));
            list.add(new NavItem("SETTINGS", "System Settings", "⚙"));
        } else if (role == User.Role.WARDEN) {
            list.add(new NavItem("STUDENTS", "Student Directory", "🎓"));
            list.add(new NavItem("ROOMS", "Bed Allocation", "🛏"));
            list.add(new NavItem("FEES", "Hostel Fee Mgmt", "💳"));
            list.add(new NavItem("VISITORS", "Visitor Management", "👥"));
            list.add(new NavItem("LEAVES", "Leave Requests", "🗓"));
            list.add(new NavItem("COMPLIANCE", "Compliance & Maint", "🛠"));
            list.add(new NavItem("SUSTAINABILITY", "Electricity & Water", "⚡"));
            list.add(new NavItem("NOTICES", "Notice Management", "📢"));
            list.add(new NavItem("MESS", "Mess Management", "🍽"));
            list.add(new NavItem("NOTIFICATIONS", "Notifications", "🔔"));
            list.add(new NavItem("REPORTS", "Hostel Reports", "📈"));
        } else { // STUDENT
            list.add(new NavItem("STUDENT_PROFILE", "My Profile", "👤"));
            list.add(new NavItem("STUDENT_ROOM", "My Room", "🏢"));
            list.add(new NavItem("STUDENT_FEES", "My Fees & Dues", "💳"));
            list.add(new NavItem("STUDENT_LEAVES", "Leave Requests", "🗓"));
            list.add(new NavItem("STUDENT_COMPLAINTS", "My Complaints", "🛠"));
            list.add(new NavItem("STUDENT_VISITORS", "My Visitors", "👥"));
            list.add(new NavItem("STUDENT_NOTICES", "Notice Board", "📢"));
            list.add(new NavItem("STUDENT_MESS", "Mess & Feedback", "🍽"));
            list.add(new NavItem("NOTIFICATIONS", "Notifications", "🔔"));
        }
        return list;
    }

    public void setActiveNav(String navId) {
        this.activeNavId = navId;
        for (NavItemPanel p : navItemPanels) {
            p.setActive(p.item.id.equalsIgnoreCase(navId));
        }
    }

    private class NavItemPanel extends JPanel {
        final NavItem item;
        private boolean isActive = false;
        private boolean isHovered = false;
        private boolean isLogout = false;
        private final JLabel iconLbl;
        private final JLabel textLbl;

        public NavItemPanel(NavItem item) {
            this.item = item;
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(9, 14, 9, 14));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            iconLbl = new JLabel(item.icon, SwingConstants.CENTER);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            iconLbl.setForeground(UITheme.SIDEBAR_MUTED);
            iconLbl.setPreferredSize(new Dimension(20, 20));

            textLbl = new JLabel(item.label);
            textLbl.setFont(UITheme.FONT_BODY);
            textLbl.setForeground(UITheme.SIDEBAR_TEXT);

            add(iconLbl, BorderLayout.WEST);
            add(textLbl, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isLogout) {
                        if (onLogout != null) onLogout.run();
                    } else {
                        setActiveNav(item.id);
                        if (onNavSelected != null) onNavSelected.accept(item.id);
                    }
                }
            });
        }

        public void setLogoutStyle() {
            this.isLogout = true;
            textLbl.setForeground(new Color(248, 113, 113)); // Red text
            iconLbl.setForeground(new Color(248, 113, 113));
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                textLbl.setFont(UITheme.FONT_BODY_BOLD);
                textLbl.setForeground(Color.WHITE);
                iconLbl.setForeground(UITheme.PRIMARY_LIGHT);
            } else {
                textLbl.setFont(UITheme.FONT_BODY);
                textLbl.setForeground(isLogout ? new Color(248, 113, 113) : UITheme.SIDEBAR_TEXT);
                iconLbl.setForeground(isLogout ? new Color(248, 113, 113) : UITheme.SIDEBAR_MUTED);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.applyQualityRendering(g2);

            int w = getWidth();
            int h = getHeight();

            if (isActive) {
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, w, h, 8, 8);
            } else if (isHovered) {
                g2.setColor(isLogout ? new Color(239, 68, 68, 30) : UITheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, w, h, 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
