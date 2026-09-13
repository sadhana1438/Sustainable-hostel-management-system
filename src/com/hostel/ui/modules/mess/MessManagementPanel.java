package com.hostel.ui.modules.mess;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.MessFeedback;
import com.hostel.model.MessMenu;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MessManagementPanel extends JPanel {
    private final User currentUser;
    private final boolean isStudent;

    private JPanel todayMenuCardsContainer;
    private CustomComboBox<String> daySelector;
    private CustomTable weeklyTable;
    private DefaultTableModel weeklyTableModel;
    private List<MessMenu> currentWeekMenus;

    private JPanel feedbackContainer;

    public MessManagementPanel(User user) {
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

        // Today's Live Menu Cards Strip
        content.add(createTodayMealSection());
        content.add(Box.createVerticalStrut(20));

        // Weekly Menu Schedule & Editor Card
        content.add(createWeeklyMenuCard());
        content.add(Box.createVerticalStrut(20));

        // Student Feedback & Quality Ratings Section
        content.add(createFeedbackSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.MESS_FEEDBACK_SUBMITTED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.DATA_REFRESH_REQUESTED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("Dining & Mess Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Daily meal schedule, weekly dietary rotation, nutritional menu and resident feedback");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        CustomButton feedbackBtn = new CustomButton("⭐ Submit Mess Feedback / Review", CustomButton.Variant.PRIMARY);
        feedbackBtn.addActionListener(e -> openSubmitFeedbackDialog());

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(feedbackBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTodayMealSection() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setOpaque(false);

        String currentDay = LocalDate.now().getDayOfWeek().toString();
        currentDay = currentDay.substring(0, 1).toUpperCase() + currentDay.substring(1).toLowerCase();

        JLabel title = new JLabel("TODAY'S DINING SCHEDULE (" + currentDay.toUpperCase() + ")");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        todayMenuCardsContainer = new JPanel(new GridLayout(1, 4, 14, 0));
        todayMenuCardsContainer.setOpaque(false);

        container.add(title, BorderLayout.NORTH);
        container.add(todayMenuCardsContainer, BorderLayout.CENTER);
        return container;
    }

    private JPanel createWeeklyMenuCard() {
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

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Weekly Nutritional Menu Matrix");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        daySelector = new CustomComboBox<>(new String[]{"All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
        daySelector.addActionListener(e -> applyWeeklyFilter());

        controls.add(new JLabel("Filter Day:"));
        controls.add(daySelector);

        if (!isStudent) {
            CustomButton editBtn = new CustomButton("✏ Edit Selected Meal Item", CustomButton.Variant.SECONDARY);
            editBtn.setFont(UITheme.FONT_TINY);
            editBtn.addActionListener(e -> {
                int row = weeklyTable.getSelectedRow();
                if (row >= 0 && currentWeekMenus != null && row < currentWeekMenus.size()) {
                    openEditMenuDialog(currentWeekMenus.get(row));
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a menu row to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            controls.add(editBtn);
        }

        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        String[] cols = {"Day of Week", "Meal Session", "Menu Items", "Special Delicacy", "Meal Timings", "Dietary Type"};
        weeklyTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        weeklyTable = new CustomTable(weeklyTableModel);
        weeklyTable.getColumnModel().getColumn(1).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "";
            Color c = UITheme.PRIMARY;
            if ("Breakfast".equalsIgnoreCase(val)) c = UITheme.WARNING;
            else if ("Lunch".equalsIgnoreCase(val)) c = UITheme.SUCCESS;
            else if ("Snacks".equalsIgnoreCase(val)) c = UITheme.INFO;
            return new StatusBadge(val, c);
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(weeklyTable);
        scroll.setPreferredSize(new Dimension(0, 320));

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFeedbackSection() {
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

        JLabel title = new JLabel("Recent Student Feedback & Food Quality Reviews");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        feedbackContainer = new JPanel();
        feedbackContainer.setLayout(new BoxLayout(feedbackContainer, BoxLayout.Y_AXIS));
        feedbackContainer.setOpaque(false);

        card.add(title, BorderLayout.NORTH);
        card.add(feedbackContainer, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        String currentDay = LocalDate.now().getDayOfWeek().toString();
        currentDay = currentDay.substring(0, 1).toUpperCase() + currentDay.substring(1).toLowerCase();

        List<MessMenu> todayList = db.getMessMenuForDay(currentDay);
        if (todayList.isEmpty()) todayList = db.getMessMenuForDay("Monday");

        todayMenuCardsContainer.removeAll();
        for (MessMenu m : todayList) {
            todayMenuCardsContainer.add(createMealCard(m));
        }

        applyWeeklyFilter();

        // Refresh Feedbacks
        feedbackContainer.removeAll();
        List<MessFeedback> feedbacks = db.getAllMessFeedbacks();
        if (feedbacks.isEmpty()) {
            JLabel empty = new JLabel("No mess feedback reviews submitted yet.");
            empty.setFont(UITheme.FONT_SMALL);
            empty.setForeground(UITheme.TEXT_MUTED);
            feedbackContainer.add(empty);
        } else {
            for (MessFeedback fb : feedbacks) {
                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(6, 0, 6, 0));

                String stars = "★".repeat(fb.getRating()) + "☆".repeat(5 - fb.getRating());
                JLabel user = new JLabel("<html><b>" + fb.getStudentName() + "</b> (Room " + fb.getRoomNumber() + ") • <font color='#f59e0b'>" + stars + "</font> <i>" + fb.getMealType() + "</i><br><font color='#475569'>\"" + fb.getComments() + "\"</font> <font color='#94a3b8' size='2'>• " + fb.getDateSubmitted() + "</font></html>");
                user.setFont(UITheme.FONT_BODY);

                StatusBadge badge = new StatusBadge(fb.getCategory(), fb.isFoodComplaint() ? UITheme.DANGER : UITheme.SUCCESS);

                row.add(user, BorderLayout.CENTER);
                row.add(badge, BorderLayout.EAST);
                feedbackContainer.add(row);
            }
        }

        revalidate();
        repaint();
    }

    private JPanel createMealCard(MessMenu m) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
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
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel meal = new JLabel(m.getMealType().getLabel());
        meal.setFont(UITheme.FONT_BODY_BOLD);
        meal.setForeground(UITheme.TEXT_MAIN);

        JLabel timings = new JLabel(m.getTimings());
        timings.setFont(UITheme.FONT_TINY);
        timings.setForeground(UITheme.PRIMARY);

        top.add(meal, BorderLayout.WEST);
        top.add(timings, BorderLayout.EAST);

        JLabel items = new JLabel("<html><body style='width: 170px; line-height: 1.3;'><font color='#475569'>" + m.getMenuItems() + "</font></body></html>");
        items.setFont(UITheme.FONT_SMALL);

        JLabel special = new JLabel("✨ " + (m.getSpecialItem() != null ? m.getSpecialItem() : "Chef's Special"));
        special.setFont(UITheme.FONT_TINY);
        special.setForeground(new Color(180, 83, 9));

        card.add(top, BorderLayout.NORTH);
        card.add(items, BorderLayout.CENTER);
        card.add(special, BorderLayout.SOUTH);
        return card;
    }

    private void applyWeeklyFilter() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<MessMenu> all = db.getAllMessMenus();

        String day = daySelector != null ? (String) daySelector.getSelectedItem() : "All Days";

        currentWeekMenus = all.stream().filter(m -> "All Days".equals(day) || m.getDayOfWeek().equalsIgnoreCase(day))
                .toList();

        weeklyTableModel.setRowCount(0);
        for (MessMenu m : currentWeekMenus) {
            weeklyTableModel.addRow(new Object[]{
                    m.getDayOfWeek(),
                    m.getMealType().getLabel(),
                    m.getMenuItems(),
                    m.getSpecialItem() != null ? m.getSpecialItem() : "-",
                    m.getTimings(),
                    m.getDietaryType()
            });
        }
    }

    private void openSubmitFeedbackDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Submit Mess Feedback & Rating", "Help us maintain top hygiene, taste, and dietary quality", "Submit Review");

        DatabaseManager db = DatabaseManager.getInstance();
        Student student = db.getStudentByStudentId(currentUser.getStudentId());
        if (student == null) student = db.getAllStudents().get(0);

        CustomComboBox<String> mealC = new CustomComboBox<>(new String[]{"Breakfast", "Lunch", "Evening Snacks", "Dinner"});
        CustomComboBox<String> ratingC = new CustomComboBox<>(new String[]{"⭐⭐⭐⭐⭐ (5 - Exceptional)", "⭐⭐⭐⭐ (4 - Very Good)", "⭐⭐⭐ (3 - Average)", "⭐⭐ (2 - Below Par)", "⭐ (1 - Unacceptable)"});
        CustomComboBox<String> catC = new CustomComboBox<>(new String[]{"Taste & Food Quality", "Hygiene & Kitchen Cleanliness", "Portion Sizing", "Staff Courteousness & Service", "Special Meal Request"});
        CustomTextField commentF = new CustomTextField("Detailed feedback or suggestion...");
        JCheckBox compCheck = new JCheckBox("Flag as official food complaint / quality grievance");
        compCheck.setFont(UITheme.FONT_SMALL);
        compCheck.setOpaque(false);

        dialog.addTwoColumnRow("Meal Session *", mealC, "Star Rating *", ratingC);
        dialog.addFormField("Feedback Category *", catC);
        dialog.addFormField("Comments & Review Notes *", commentF);
        dialog.addFormField("Grievance Status", compCheck);

        Student finalStudent = student;
        dialog.getPrimaryButton().addActionListener(e -> {
            String comm = commentF.getText().trim();
            if (comm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please add your review comments.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int rating = 5 - ratingC.getSelectedIndex();
            MessFeedback fb = new MessFeedback(0, finalStudent.getId(), finalStudent.getName(),
                    finalStudent.getRoomNumber() != null ? finalStudent.getRoomNumber() : "A-204",
                    (String) mealC.getSelectedItem(), rating, (String) catC.getSelectedItem(),
                    comm, compCheck.isSelected(), "Received", null);

            db.submitMessFeedback(fb, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(520, 420);
    }

    private void openEditMenuDialog(MessMenu m) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Edit Menu for " + m.getDayOfWeek() + " (" + m.getMealType().getLabel() + ")", "Update dishes, special item, and service timings", "Update Menu");

        CustomTextField itemsF = new CustomTextField();
        itemsF.setText(m.getMenuItems());

        CustomTextField specialF = new CustomTextField();
        specialF.setText(m.getSpecialItem() != null ? m.getSpecialItem() : "");

        CustomTextField timingsF = new CustomTextField();
        timingsF.setText(m.getTimings() != null ? m.getTimings() : "");

        dialog.addFormField("Menu Items (Comma Separated) *", itemsF);
        dialog.addTwoColumnRow("Special Delicacy", specialF, "Meal Timings", timingsF);

        dialog.getPrimaryButton().addActionListener(e -> {
            DatabaseManager.getInstance().updateMessMenuItem(m.getId(), itemsF.getText().trim(), specialF.getText().trim(), timingsF.getText().trim());
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(520, 320);
    }
}
