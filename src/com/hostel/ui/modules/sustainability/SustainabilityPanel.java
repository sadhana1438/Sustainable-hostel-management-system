package com.hostel.ui.modules.sustainability;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.SustainabilityUsage;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SustainabilityPanel extends JPanel {
    private final User currentUser;

    // Electricity KPI
    private StatCard statElecCurrent;
    private StatCard statElecPrev;
    private StatCard statElecChange;

    // Water KPI
    private StatCard statWaterCurrent;
    private StatCard statWaterPrev;
    private StatCard statWaterChange;

    private DualResourceChart chart;
    private CustomTable table;
    private DefaultTableModel tableModel;
    private JPanel anomalyAlertBanner;
    private JLabel anomalyAlertText;

    public SustainabilityPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.APP_BG);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header Title & Actions
        content.add(createHeaderRow());
        content.add(Box.createVerticalStrut(18));

        // Anomaly Jump Banner (Shown if latest month has unusual spike > 15%)
        content.add(createAnomalyBanner());
        content.add(Box.createVerticalStrut(16));

        // Dual Section KPI Cards (Electricity & Water)
        content.add(createKPISection());
        content.add(Box.createVerticalStrut(20));

        // Monthly Consumption Dual-Bar Trend Visualizer Card
        content.add(createChartCard());
        content.add(Box.createVerticalStrut(20));

        // Historical Manual Log Data Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.SUSTAINABILITY_LOGGED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel("⚡ Electricity & Water Sustainability Monitoring");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel("Software-only manual resource tracking, month-over-month consumption analysis, and surge detection");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        CustomButton logBtn = new CustomButton("⚡ Log Monthly Usage Data", CustomButton.Variant.PRIMARY);
        logBtn.addActionListener(e -> openAddUsageDialog());

        panel.add(titleGrp, BorderLayout.WEST);
        panel.add(logBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createAnomalyBanner() {
        anomalyAlertBanner = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(new Color(254, 242, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(252, 165, 165));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        anomalyAlertBanner.setOpaque(false);
        anomalyAlertBanner.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel icon = new JLabel("⚠️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        anomalyAlertText = new JLabel("Unusual Consumption Jump Detected (>15% increase). Review HVAC and pipeline infrastructure.");
        anomalyAlertText.setFont(UITheme.FONT_BODY_BOLD);
        anomalyAlertText.setForeground(UITheme.DANGER_TEXT);

        anomalyAlertBanner.add(icon, BorderLayout.WEST);
        anomalyAlertBanner.add(anomalyAlertText, BorderLayout.CENTER);
        anomalyAlertBanner.setVisible(false);
        return anomalyAlertBanner;
    }

    private JPanel createKPISection() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);

        // Electricity Group
        JPanel elecBox = new JPanel(new BorderLayout(0, 10)) {
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
        elecBox.setOpaque(false);
        elecBox.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel eTitle = new JLabel("⚡ ELECTRICITY CONSUMPTION (kWh)");
        eTitle.setFont(UITheme.FONT_SUBHEADER);
        eTitle.setForeground(new Color(180, 83, 9)); // Amber 700

        JPanel eGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        eGrid.setOpaque(false);
        statElecCurrent = new StatCard("Current Month", "0 kWh", "Aug 2024", UITheme.ELEC_COLOR, "⚡");
        statElecPrev = new StatCard("Previous Month", "0 kWh", "Jul 2024", UITheme.TEXT_MUTED, "📅");
        statElecChange = new StatCard("Monthly Variance", "0.0%", "vs last month", UITheme.SUCCESS, "📈");

        eGrid.add(statElecCurrent);
        eGrid.add(statElecPrev);
        eGrid.add(statElecChange);

        elecBox.add(eTitle, BorderLayout.NORTH);
        elecBox.add(eGrid, BorderLayout.CENTER);

        // Water Group
        JPanel waterBox = new JPanel(new BorderLayout(0, 10)) {
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
        waterBox.setOpaque(false);
        waterBox.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel wTitle = new JLabel("💧 WATER CONSUMPTION (kL)");
        wTitle.setFont(UITheme.FONT_SUBHEADER);
        wTitle.setForeground(new Color(3, 105, 161)); // Sky 700

        JPanel wGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        wGrid.setOpaque(false);
        statWaterCurrent = new StatCard("Current Month", "0 kL", "Aug 2024", UITheme.WATER_COLOR, "💧");
        statWaterPrev = new StatCard("Previous Month", "0 kL", "Jul 2024", UITheme.TEXT_MUTED, "📅");
        statWaterChange = new StatCard("Monthly Variance", "0.0%", "vs last month", UITheme.SUCCESS, "📈");

        wGrid.add(statWaterCurrent);
        wGrid.add(statWaterPrev);
        wGrid.add(statWaterChange);

        waterBox.add(wTitle, BorderLayout.NORTH);
        waterBox.add(wGrid, BorderLayout.CENTER);

        container.add(elecBox);
        container.add(waterBox);
        return container;
    }

    private JPanel createChartCard() {
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

        JLabel title = new JLabel("Month-over-Month Resource Consumption Trends");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        JPanel toggles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toggles.setOpaque(false);

        CustomButton btnAll = new CustomButton("Combined", CustomButton.Variant.PRIMARY);
        CustomButton btnElec = new CustomButton("Electricity", CustomButton.Variant.SECONDARY);
        CustomButton btnWater = new CustomButton("Water", CustomButton.Variant.SECONDARY);

        btnAll.setFont(UITheme.FONT_TINY);
        btnElec.setFont(UITheme.FONT_TINY);
        btnWater.setFont(UITheme.FONT_TINY);

        btnAll.addActionListener(e -> chart.setMode(DualResourceChart.Mode.BOTH));
        btnElec.addActionListener(e -> chart.setMode(DualResourceChart.Mode.ELECTRICITY_ONLY));
        btnWater.addActionListener(e -> chart.setMode(DualResourceChart.Mode.WATER_ONLY));

        toggles.add(btnAll);
        toggles.add(btnElec);
        toggles.add(btnWater);

        header.add(title, BorderLayout.WEST);
        header.add(toggles, BorderLayout.EAST);

        chart = new DualResourceChart(DatabaseManager.getInstance().getAllSustainabilityUsages());
        chart.setPreferredSize(new Dimension(0, 260));

        card.add(header, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {
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

        JLabel title = new JLabel("Monthly Manual Entry Log & Anomaly Analysis");
        title.setFont(UITheme.FONT_SUBHEADER);
        title.setForeground(UITheme.TEXT_MAIN);

        String[] cols = {"Month", "Electricity (kWh)", "Elec Variance %", "Water (kL)", "Water Variance %", "Anomaly Status", "Remarks / Audit Notes", "Logged Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new CustomTable(tableModel);

        // Highlight anomaly status
        table.getColumnModel().getColumn(5).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            boolean isJump = "UNUSUAL JUMP (⚠)".equals(value);
            return new StatusBadge(isJump ? "⚠ JUMP (>15%)" : "NORMAL", isJump ? UITheme.DANGER : UITheme.SUCCESS);
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 240));

        card.add(title, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<SustainabilityUsage> list = db.getAllSustainabilityUsages();

        if (!list.isEmpty()) {
            SustainabilityUsage latest = list.get(list.size() - 1);
            SustainabilityUsage prev = list.size() > 1 ? list.get(list.size() - 2) : null;

            statElecCurrent.setValue(String.format("%,.0f kWh", latest.getElectricityKwh()));
            statElecCurrent.setSubtext(latest.getMonthLabel());

            statElecPrev.setValue(prev != null ? String.format("%,.0f kWh", prev.getElectricityKwh()) : "0 kWh");
            statElecPrev.setSubtext(prev != null ? prev.getMonthLabel() : "-");

            String eChg = (latest.getElectricityChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", latest.getElectricityChangePct());
            statElecChange.setValue(eChg);
            statElecChange.setSubtext(latest.isElectricityAnomaly() ? "⚠ Unusual Spike" : "Within range");

            statWaterCurrent.setValue(String.format("%,.0f kL", latest.getWaterKiloLitres()));
            statWaterCurrent.setSubtext(latest.getMonthLabel());

            statWaterPrev.setValue(prev != null ? String.format("%,.0f kL", prev.getWaterKiloLitres()) : "0 kL");
            statWaterPrev.setSubtext(prev != null ? prev.getMonthLabel() : "-");

            String wChg = (latest.getWaterChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", latest.getWaterChangePct());
            statWaterChange.setValue(wChg);
            statWaterChange.setSubtext(latest.isWaterAnomaly() ? "⚠ Unusual Spike" : "Within range");

            if (latest.hasAnyAnomaly()) {
                anomalyAlertBanner.setVisible(true);
                anomalyAlertText.setText("Notice: " + latest.getMonthLabel() + " recorded unusual consumption surge ("
                        + (latest.isElectricityAnomaly() ? "Electricity: " + eChg + " " : "")
                        + (latest.isWaterAnomaly() ? "Water: " + wChg : "") + "). Notes: \"" + latest.getRemarks() + "\"");
            } else {
                anomalyAlertBanner.setVisible(false);
            }
        }

        chart.setData(list);

        tableModel.setRowCount(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            SustainabilityUsage u = list.get(i);
            tableModel.addRow(new Object[]{
                    u.getMonthLabel(),
                    String.format("%,.0f", u.getElectricityKwh()),
                    (u.getElectricityChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", u.getElectricityChangePct()),
                    String.format("%,.0f", u.getWaterKiloLitres()),
                    (u.getWaterChangePct() >= 0 ? "+" : "") + String.format("%.1f%%", u.getWaterChangePct()),
                    u.hasAnyAnomaly() ? "UNUSUAL JUMP (⚠)" : "NORMAL",
                    u.getRemarks() != null ? u.getRemarks() : "Standard operational usage",
                    u.getRecordedDate()
            });
        }

        revalidate();
        repaint();
    }

    public void openAddUsageDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Enter Monthly Resource Consumption", "Manual electricity and water entry with instant variance calculation", "Save & Calculate");

        CustomTextField monthF = new CustomTextField("YYYY-MM (e.g. 2024-09)");
        CustomTextField labelF = new CustomTextField("e.g. Sep 2024");
        CustomTextField elecF = new CustomTextField("Electricity units in kWh (e.g. 14200)");
        CustomTextField waterF = new CustomTextField("Water units in kL (e.g. 330)");
        CustomTextField remarksF = new CustomTextField("Operational remarks / weather conditions / event notes");

        dialog.addTwoColumnRow("Month Code (YYYY-MM) *", monthF, "Month Label *", labelF);
        dialog.addTwoColumnRow("Electricity Units (kWh) *", elecF, "Water Units (kL) *", waterF);
        dialog.addFormField("Remarks / Seasonal Factors", remarksF);

        dialog.getPrimaryButton().addActionListener(e -> {
            try {
                String ym = monthF.getText().trim();
                String lbl = labelF.getText().trim();
                double el = Double.parseDouble(elecF.getText().trim());
                double wt = Double.parseDouble(waterF.getText().trim());

                if (ym.isEmpty() || lbl.isEmpty() || el <= 0 || wt <= 0) throw new NumberFormatException();

                DatabaseManager.getInstance().addSustainabilityUsage(ym, lbl, el, wt, remarksF.getText().trim(),
                        currentUser.getFullName(), currentUser.getRole().name());

                dialog.setConfirmed(true);
                dialog.dispose();
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric consumption units and month information.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.showModal(520, 380);
    }
}
