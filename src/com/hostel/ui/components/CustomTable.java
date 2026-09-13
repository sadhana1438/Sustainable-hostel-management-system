package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class CustomTable extends JTable {

    public CustomTable() {
        super();
        initTable();
    }

    public CustomTable(TableModel model) {
        super(model);
        initTable();
    }

    private void initTable() {
        setRowHeight(42);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_MAIN);
        setSelectionBackground(UITheme.PRIMARY_LIGHT);
        setSelectionForeground(UITheme.PRIMARY);
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(UITheme.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 44));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(UITheme.FONT_BODY_BOLD);
                label.setForeground(UITheme.TEXT_SECONDARY);
                label.setBackground(new Color(248, 250, 252));
                label.setBorder(new EmptyBorder(0, 14, 0, 14));
                return label;
            }
        });

        // Default cell renderer with alternating row stripes and padding
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(new EmptyBorder(0, 14, 0, 14));
                label.setFont(UITheme.FONT_BODY);

                if (isSelected) {
                    label.setBackground(UITheme.PRIMARY_LIGHT);
                    label.setForeground(UITheme.PRIMARY);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    label.setForeground(UITheme.TEXT_MAIN);
                }
                return label;
            }
        });
    }

    public static JScrollPane wrapInScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }
}
