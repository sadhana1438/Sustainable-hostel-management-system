package com.hostel.ui.modules.inventory;

import com.hostel.db.DatabaseManager;
import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;
import com.hostel.model.Asset;
import com.hostel.model.User;
import com.hostel.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryAssetPanel extends JPanel {
    private final User currentUser;
    private final boolean isAdmin;

    private StatCard statTotalAssets;
    private StatCard statGoodCondition;
    private StatCard statDamaged;
    private StatCard statUnderRepair;

    private CustomTextField searchField;
    private CustomComboBox<String> categoryFilter;
    private CustomComboBox<String> conditionFilter;
    private CustomTable table;
    private DefaultTableModel tableModel;
    private List<Asset> currentAssetList;

    public InventoryAssetPanel(User user) {
        this.currentUser = user;
        this.isAdmin = (user.getRole() == User.Role.ADMIN);

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

        // Filters Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Table Card
        content.add(createTableCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.APP_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();

        AppEventBus.getInstance().subscribe(AppEvent.EventType.ASSET_ADDED, e -> refreshData());
        AppEventBus.getInstance().subscribe(AppEvent.EventType.ASSET_CONDITION_FLAGGED, e -> refreshData());
    }

    private JPanel createHeaderRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);

        JLabel title = new JLabel(isAdmin ? "Inventory & Physical Asset Management" : "Hostel Asset Registry & Damage Reporting");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(isAdmin ? "Track furniture, electronics, sports gear, appliances, and maintenance status" : "Inspect hostel assets and flag damaged items for repair");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);

        if (isAdmin) {
            CustomButton addBtn = new CustomButton("+ Add New Asset", CustomButton.Variant.PRIMARY);
            addBtn.addActionListener(e -> openAddAssetDialog());
            panel.add(addBtn, BorderLayout.EAST);
        }

        panel.add(titleGrp, BorderLayout.WEST);
        return panel;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        statTotalAssets = new StatCard("Total Asset Items", "0", "Across all facilities", UITheme.PRIMARY, "📦");
        statGoodCondition = new StatCard("Good / Operational", "0", "Ready in service", UITheme.SUCCESS, "✅");
        statDamaged = new StatCard("Damaged Assets", "0", "Needs replacement", UITheme.DANGER, "⚠️");
        statUnderRepair = new StatCard("Under Repair", "0", "Technician servicing", UITheme.WARNING, "🔧");

        row.add(statTotalAssets);
        row.add(statGoodCondition);
        row.add(statDamaged);
        row.add(statUnderRepair);
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

        searchField = new CustomTextField("Search by asset code, name, location...");
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { applyFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        categoryFilter = new CustomComboBox<>(new String[]{"All Categories", "Furniture", "Electrical", "Plumbing", "Sports & Gym", "Electronics"});
        conditionFilter = new CustomComboBox<>(new String[]{"All Conditions", "GOOD", "DAMAGED", "UNDER_REPAIR", "MISSING"});

        categoryFilter.addActionListener(e -> applyFilters());
        conditionFilter.addActionListener(e -> applyFilters());

        filters.add(new JLabel("Category:"));
        filters.add(categoryFilter);
        filters.add(new JLabel("Condition:"));
        filters.add(conditionFilter);

        card.add(searchField, BorderLayout.WEST);
        card.add(filters, BorderLayout.EAST);
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

        String[] cols = {"Asset ID", "Asset Name", "Category", "Quantity", "Location", "Condition", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new CustomTable(tableModel);

        table.getColumnModel().getColumn(5).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String val = value != null ? value.toString() : "GOOD";
            Color c = UITheme.SUCCESS;
            if ("DAMAGED".equalsIgnoreCase(val)) c = UITheme.DANGER;
            else if ("UNDER_REPAIR".equalsIgnoreCase(val)) c = UITheme.WARNING;
            else if ("MISSING".equalsIgnoreCase(val)) c = UITheme.TEXT_MUTED;
            return new StatusBadge(val.replace("_", " "), c);
        });

        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            p.setOpaque(false);
            JButton btn = new JButton(isAdmin ? "Edit Asset ⚙" : "Flag Damage 🚩");
            btn.setFont(UITheme.FONT_TINY);
            btn.setFocusPainted(false);
            p.add(btn);
            return p;
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && currentAssetList != null && row < currentAssetList.size()) {
                    Asset a = currentAssetList.get(row);
                    if (col == 7 || e.getClickCount() == 2) {
                        openManageAssetDialog(a);
                    }
                }
            }
        });

        JScrollPane scroll = CustomTable.wrapInScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 420));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Asset> all = db.getAllAssets();

        int totalUnits = all.stream().mapToInt(Asset::getQuantity).sum();
        long good = all.stream().filter(a -> a.getCondition() == Asset.Condition.GOOD).count();
        long dam = all.stream().filter(a -> a.getCondition() == Asset.Condition.DAMAGED).count();
        long rep = all.stream().filter(a -> a.getCondition() == Asset.Condition.UNDER_REPAIR).count();

        statTotalAssets.setValue(String.valueOf(totalUnits));
        statGoodCondition.setValue(String.valueOf(good));
        statDamaged.setValue(String.valueOf(dam));
        statUnderRepair.setValue(String.valueOf(rep));

        applyFilters();
    }

    private void applyFilters() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Asset> all = db.getAllAssets();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String cat = categoryFilter != null ? (String) categoryFilter.getSelectedItem() : "All Categories";
        String cond = conditionFilter != null ? (String) conditionFilter.getSelectedItem() : "All Conditions";

        currentAssetList = all.stream().filter(a -> {
            boolean matchQuery = query.isEmpty()
                    || a.getName().toLowerCase().contains(query)
                    || a.getAssetCode().toLowerCase().contains(query)
                    || a.getLocation().toLowerCase().contains(query);

            boolean matchCat = "All Categories".equals(cat) || a.getCategory().equalsIgnoreCase(cat);
            boolean matchCond = "All Conditions".equals(cond) || a.getCondition().name().equalsIgnoreCase(cond);

            return matchQuery && matchCat && matchCond;
        }).collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Asset a : currentAssetList) {
            tableModel.addRow(new Object[]{
                    a.getAssetCode(),
                    a.getName(),
                    a.getCategory(),
                    a.getQuantity() + " Units",
                    a.getLocation(),
                    a.getCondition().name(),
                    a.getStatus(),
                    "Action"
            });
        }
    }

    private void openAddAssetDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AppDialog dialog = new AppDialog(parent, "Register New Hostel Asset", "Add physical equipment, furniture, or appliance record", "Save Asset");

        CustomTextField codeF = new CustomTextField("Asset code (e.g. AST-FUR-005)");
        CustomTextField nameF = new CustomTextField("Item name (e.g. Table Tennis Board)");
        CustomComboBox<String> catC = new CustomComboBox<>(new String[]{"Furniture", "Electrical", "Plumbing", "Sports & Gym", "Electronics"});
        CustomTextField qtyF = new CustomTextField("1");
        CustomTextField locF = new CustomTextField("Location (e.g. Clubhouse Room 1)");
        CustomTextField valF = new CustomTextField("Estimated value in ₹");

        dialog.addTwoColumnRow("Asset Code", codeF, "Category *", catC);
        dialog.addFormField("Asset Item Name *", nameF);
        dialog.addTwoColumnRow("Quantity Units", qtyF, "Location / Placement *", locF);
        dialog.addFormField("Estimated Asset Value (₹)", valF);

        dialog.getPrimaryButton().addActionListener(e -> {
            String name = nameF.getText().trim();
            String loc = locF.getText().trim();
            if (name.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in item name and location.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int qty = 1;
            try { qty = Integer.parseInt(qtyF.getText().trim()); } catch (Exception ignored) {}
            double val = 0;
            try { val = Double.parseDouble(valF.getText().trim()); } catch (Exception ignored) {}

            Asset asset = new Asset(0, codeF.getText().trim(), name, (String) catC.getSelectedItem(),
                    qty, loc, Asset.Condition.GOOD, "In Use", val, null, null);

            DatabaseManager.getInstance().addAsset(asset, currentUser.getFullName(), currentUser.getRole().name());
            dialog.setConfirmed(true);
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(520, 420);
    }

    private void openManageAssetDialog(Asset a) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String title = isAdmin ? "Manage Asset: " + a.getName() : "Flag Asset Condition: " + a.getName();
        AppDialog dialog = new AppDialog(parent, title, "Code: " + a.getAssetCode() + " • Location: " + a.getLocation(), "Save Condition Update");

        Asset.Condition[] conds = Asset.Condition.values();
        String[] condNames = new String[conds.length];
        for (int i = 0; i < conds.length; i++) condNames[i] = conds[i].getLabel();
        CustomComboBox<String> condC = new CustomComboBox<>(condNames);
        condC.setSelectedIndex(a.getCondition().ordinal());

        CustomTextField notesF = new CustomTextField("Damage details / inspection findings");
        if (a.getDamageNotes() != null) notesF.setText(a.getDamageNotes());

        dialog.addFormField("Current Physical Condition *", condC);
        dialog.addFormField("Inspection / Damage Notes", notesF);

        dialog.getPrimaryButton().addActionListener(e -> {
            Asset.Condition chosen = conds[condC.getSelectedIndex()];
            DatabaseManager.getInstance().flagAssetDamage(a.getId(), chosen, notesF.getText().trim(),
                    currentUser.getFullName(), currentUser.getRole().name());
            dialog.dispose();
            refreshData();
        });

        dialog.showModal(500, 320);
    }
}
