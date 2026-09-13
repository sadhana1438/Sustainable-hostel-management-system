package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class CustomComboBox<E> extends JComboBox<E> {
    public CustomComboBox(E[] items) {
        super(items);
        initCombo();
    }

    public CustomComboBox(ComboBoxModel<E> model) {
        super(model);
        initCombo();
    }

    public CustomComboBox() {
        super();
        initCombo();
    }

    private void initCombo() {
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_MAIN);
        setBackground(Color.WHITE);
        setFocusable(false);
        setOpaque(false);

        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(UITheme.FONT_BODY);
                label.setBorder(new EmptyBorder(6, 12, 6, 12));
                if (isSelected) {
                    label.setBackground(UITheme.PRIMARY_LIGHT);
                    label.setForeground(UITheme.PRIMARY);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(UITheme.TEXT_MAIN);
                }
                return label;
            }
        });

        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        UITheme.applyQualityRendering(g2);
                        g2.setColor(UITheme.TEXT_SECONDARY);
                        int w = getWidth();
                        int h = getHeight();
                        int[] xPoints = {w / 2 - 4, w / 2 + 4, w / 2};
                        int[] yPoints = {h / 2 - 2, h / 2 - 2, h / 2 + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
                g2.dispose();
            }
        });

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 38));
    }
}
