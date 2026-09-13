package com.hostel.ui.modules.notice;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Notice;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NoticeManagementPanel extends JPanel {
    private final User currentUser;
    private final boolean isStudent;

    private StatCard statTotalNotices;
    private StatCard statUrgentNotices;
    private StatCard statGeneralNotices;

    private JPanel noticesGrid;

    public NoticeManagementPanel(User user) {
        this.currentUser = user;
        this.isStudent = (user.getRole() == User.Role.STUDENT);

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
        content.add(Box.createVerticalStrut(20));

        // Notice Cards Grid
        noticesGrid = new JPanel();
        noticesGrid.setLayout(new BoxLayout(noticesGrid, BoxLayout.Y_AXIS));
        noticesGrid.setOpaque(false);
        content.add(noticesGrid);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.NOTICE_POSTED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isStudent ? "Official Hostel Notice Board" : "Notice & Circular Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(isStudent ? "Official hostel announcements, curfew circulars, mess schedules, and emergency alerts" : "Draft, schedule and broadcast circulars to students, wardens, or entire hostel");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        if (!isStudent) {
            CustomButton createBtn = new CustomButton("+ Create Announcement", CustomButton.Variant.PRIMARY);
            createBtn.addActionListener(e -> openCreateNoticeDialog());
            panel.add(createBtn, BorderLayout.EAST);
        }

        panel.add(titleGrp, BorderLayout.WEST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);

        statTotalNotices = new StatCard("Total Announcements", "0", "Active notices", UITheme.PRIMARY, "📢");
        statUrgentNotices = new StatCard("Urgent / High Priority", "0", "Critical advisories", UITheme.DANGER, "🚨");
        statGeneralNotices = new StatCard("General Information", "0", "Operational updates", UITheme.INFO, "📌");

        row.add(statTotalNotices);
        row.add(statUrgentNotices);
        row.add(statGeneralNotices);
        return row;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Notice> list = db.getAllNotices();

        long urgent = list.stream().filter(n -> n.getPriority() == Notice.Priority.URGENT || n.getPriority() == Notice.Priority.HIGH).count();
        long gen = list.stream().filter(n -> n.getPriority() == Notice.Priority.NORMAL).count();

        statTotalNotices.setValue(String.valueOf(list.size()));
        statUrgentNotices.setValue(String.valueOf(urgent));
        statGeneralNotices.setValue(String.valueOf(gen));

        noticesGrid.removeAll();

        if (list.isEmpty()) {
            JLabel empty = new JLabel("No active announcements published.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_MUTED);
            noticesGrid.add(empty);
        } else {
            for (Notice n : list) {
                noticesGrid.add(createNoticeCard(n));
                noticesGrid.add(Box.createVerticalStrut(14));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel createNoticeCard(Notice notice) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Priority border accent
                Color borderCol = UITheme.BORDER;
                if (notice.getPriority() == Notice.Priority.URGENT) borderCol = new Color(252, 165, 165);
                else if (notice.getPriority() == Notice.Priority.HIGH) borderCol = new Color(253, 230, 138);

                g2.setColor(borderCol);
                g2.setStroke(new BasicStroke(notice.getPriority() == Notice.Priority.URGENT ? 1.5f : 1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header: Priority badge + Category + Date
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);

        Color pColor = notice.getPriority() == Notice.Priority.URGENT ? UITheme.DANGER : (notice.getPriority() == Notice.Priority.HIGH ? UITheme.WARNING : UITheme.TEXT_MUTED);
        StatusBadge pBadge = new StatusBadge(notice.getPriority().getLabel(), pColor);

        JLabel catLabel = new JLabel("• " + notice.getCategory());
        catLabel.setFont(UITheme.FONT_TINY);
        catLabel.setForeground(UITheme.TEXT_SECONDARY);

        leftBadges.add(pBadge);
        leftBadges.add(catLabel);

        JLabel dateLabel = new JLabel("📅 " + notice.getPostedDate() + " • Posted by " + notice.getPostedBy());
        dateLabel.setFont(UITheme.FONT_TINY);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        header.add(leftBadges, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        // Body Content
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JLabel titleLbl = new JLabel(notice.getTitle());
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(UITheme.TEXT_MAIN);

        JLabel contentLbl = new JLabel("<html><body style='width: 750px; line-height: 1.4;'>" + notice.getContent() + "</body></html>");
        contentLbl.setFont(UITheme.FONT_BODY);
        contentLbl.setForeground(UITheme.TEXT_SECONDARY);

        body.add(titleLbl);
        body.add(Box.createVerticalStrut(6));
        body.add(contentLbl);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private void openCreateNoticeDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Publish Notice / Circular", "Broadcast important circular to hostel residents and staff", "Publish Announcement");

        CustomTextField titleF = new CustomTextField("Announcement headline / Title");
        CustomComboBox<String> catC = new CustomComboBox<>(new String[]{"Curfew / Security", "Maintenance", "Mess", "Hostel Dues", "Events & Sports", "Emergency Advisory", "General"});
        CustomComboBox<String> prioC = new CustomComboBox<>(new String[]{"NORMAL (General)", "HIGH Priority", "URGENT Announcement"});
        CustomComboBox<String> audC = new CustomComboBox<>(new String[]{"ALL (Everyone)", "STUDENTS Only", "WARDENS Only"});
        CustomTextField contentF = new CustomTextField("Complete circular text and instructions");

        dialog.addFormField("Announcement Title *", titleF);
        dialog.addTwoColumnRow("Category", catC, "Priority Level *", prioC);
        dialog.addFormField("Target Audience", audC);
        dialog.addFormField("Announcement Content *", contentF);

        dialog.getPrimaryButton().addActionListener(e -> {
            String t = titleF.getText().trim();
            String c = contentF.getText().trim();
            if (t.isEmpty() || c.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in title and announcement text.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Notice.Priority p = Notice.Priority.NORMAL;
            if (prioC.getSelectedIndex() == 1) p = Notice.Priority.HIGH;
            else if (prioC.getSelectedIndex() == 2) p = Notice.Priority.URGENT;

            Notice n = new Notice(0, t, c, (String) catC.getSelectedItem(), p, currentUser.getFullName(),
                    currentUser.getRole().name(), null, (String) audC.getSelectedItem(), true, false);

            DatabaseManager.getInstance().addNotice(n, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(540, 460);
    }
}
