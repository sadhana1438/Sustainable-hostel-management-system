package com.hostel.ui.modules.notification;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Notification;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsPanel extends JPanel {
    private final User currentUser;

    private StatCard statTotal;
    private StatCard statUnread;
    private StatCard statActionable;

    private CustomComboBox<String> typeFilter;
    private JPanel notificationListContainer;
    private List<Notification> currentNotifications;

    public NotificationsPanel(User user) {
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

        // Stats Row
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(18));

        // Filter Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Notifications List Card
        content.add(createListCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.NOTIFICATION_RECEIVED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Notification & Alert Center");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Real-time system advisories, leave updates, payment receipts, and maintenance tickets");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        CustomButton markReadBtn = new CustomButton("✓ Mark All as Read", CustomButton.Variant.SECONDARY);
        markReadBtn.addActionListener(e -> {
            DatabaseManager.getInstance().markAllNotificationsAsRead(currentUser);
            refreshData();
        });

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(markReadBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        statTotal = new StatCard("Total Notifications", "0", "Received in workspace", UITheme.PRIMARY, "🔔");
        statUnread = new StatCard("Unread Alerts", "0", "New messages", UITheme.DANGER, "🔴");
        statActionable = new StatCard("System & Operational", "0", "Categorized updates", UITheme.INFO, "📋");

        row.add(statTotal);
        row.add(statUnread);
        row.add(statActionable);
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

        JLabel title = new JLabel("Filter Notifications by Category:");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_SECONDARY);

        typeFilter = new CustomComboBox<>(new String[]{
                "All Categories",
                "LEAVE (Leave Requests)",
                "FEE (Fees & Payments)",
                "COMPLAINT (Maintenance & Tickets)",
                "NOTICE (Announcements)",
                "SUSTAINABILITY (Resource Alerts)",
                "VISITOR (Gate Entry)"
        });
        typeFilter.addActionListener(e -> applyFilter());

        card.add(title, BorderLayout.WEST);
        card.add(typeFilter, BorderLayout.EAST);
        return card;
    }

    private JPanel createListCard() {
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
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        notificationListContainer = new JPanel();
        notificationListContainer.setLayout(new BoxLayout(notificationListContainer, BoxLayout.Y_AXIS));
        notificationListContainer.setOpaque(false);

        card.add(notificationListContainer, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Notification> list = db.getNotificationsForUser(currentUser);

        long unread = list.stream().filter(n -> !n.isRead()).count();

        statTotal.setValue(String.valueOf(list.size()));
        statUnread.setValue(String.valueOf(unread));
        statActionable.setValue(String.valueOf(list.size()));

        applyFilter();
    }

    private void applyFilter() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Notification> all = db.getNotificationsForUser(currentUser);

        int filterIdx = typeFilter != null ? typeFilter.getSelectedIndex() : 0;

        currentNotifications = all.stream().filter(n -> {
            if (filterIdx == 0) return true;
            if (filterIdx == 1) return n.getType() == Notification.NotificationType.LEAVE;
            if (filterIdx == 2) return n.getType() == Notification.NotificationType.FEE;
            if (filterIdx == 3) return n.getType() == Notification.NotificationType.COMPLAINT;
            if (filterIdx == 4) return n.getType() == Notification.NotificationType.NOTICE;
            if (filterIdx == 5) return n.getType() == Notification.NotificationType.SUSTAINABILITY;
            if (filterIdx == 6) return n.getType() == Notification.NotificationType.VISITOR;
            return true;
        }).collect(Collectors.toList());

        notificationListContainer.removeAll();

        if (currentNotifications.isEmpty()) {
            JLabel empty = new JLabel("No notifications found under this filter.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setBorder(new EmptyBorder(24, 0, 24, 0));
            notificationListContainer.add(empty);
        } else {
            for (Notification n : currentNotifications) {
                notificationListContainer.add(createNotificationRow(n));
                notificationListContainer.add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel createNotificationRow(Notification n) {
        JPanel row = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(n.isRead() ? new Color(248, 250, 252) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(n.isRead() ? UITheme.BORDER : UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(n.isRead() ? 1.0f : 1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Left Icon Pill
        StatusBadge typeBadge = new StatusBadge(n.getType().getLabel(), Color.decode(n.getType().getColorHex()));

        // Center Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel((n.isRead() ? "" : "● ") + n.getTitle());
        title.setFont(n.isRead() ? UITheme.FONT_BODY : UITheme.FONT_BODY_BOLD);
        title.setForeground(n.isRead() ? UITheme.TEXT_MAIN : UITheme.PRIMARY);

        JLabel msg = new JLabel("<html><body style='width: 600px;'>" + n.getMessage() + "</body></html>");
        msg.setFont(UITheme.FONT_SMALL);
        msg.setForeground(UITheme.TEXT_SECONDARY);

        JLabel time = new JLabel("🕒 " + n.getTimestamp());
        time.setFont(UITheme.FONT_TINY);
        time.setForeground(UITheme.TEXT_MUTED);

        content.add(title);
        content.add(Box.createVerticalStrut(2));
        content.add(msg);
        content.add(Box.createVerticalStrut(4));
        content.add(time);

        // Right Action
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        if (!n.isRead()) {
            CustomButton readBtn = new CustomButton("Mark Read", CustomButton.Variant.GHOST);
            readBtn.setFont(UITheme.FONT_TINY);
            readBtn.addActionListener(e -> {
                DatabaseManager.getInstance().markNotificationAsRead(n.getId());
                refreshData();
            });
            actions.add(readBtn);
        }

        row.add(typeBadge, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);
        row.add(actions, BorderLayout.EAST);
        return row;
    }
}
