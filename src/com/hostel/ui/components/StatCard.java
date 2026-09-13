package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtextLabel;
    private final JPanel iconPanel;
    private Color accentColor = UITheme.PRIMARY;
    private String iconSymbol = "●";

    public StatCard(String title, String value, String subtext, Color accentColor, String iconSymbol) {
        this.accentColor = accentColor != null ? accentColor : UITheme.PRIMARY;
        this.iconSymbol = iconSymbol != null ? iconSymbol : "●";

        setLayout(new BorderLayout(12, 8));
        setBackground(UITheme.CARD_BG);
        setBorder(new EmptyBorder(16, 18, 16, 18));
        setOpaque(false);

        // Center Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.FONT_TINY);
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.FONT_STAT_VAL);
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        subtextLabel = new JLabel(subtext != null ? subtext : "");
        subtextLabel.setFont(UITheme.FONT_SMALL);
        subtextLabel.setForeground(UITheme.TEXT_SECONDARY);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(valueLabel);
        if (subtext != null && !subtext.isEmpty()) {
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(subtextLabel);
        }

        // Left/Right Icon Badge
        iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyQualityRendering(g2);
                int size = Math.min(getWidth(), getHeight());
                // Tinted circle
                Color bgTint = new Color(StatCard.this.accentColor.getRed(), StatCard.this.accentColor.getGreen(), StatCard.this.accentColor.getBlue(), 35);
                g2.setColor(bgTint);
                g2.fillRoundRect((getWidth() - size) / 2, (getHeight() - size) / 2, size, size, 12, 12);

                // Symbol / Icon
                g2.setColor(StatCard.this.accentColor);
                g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(StatCard.this.iconSymbol)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(StatCard.this.iconSymbol, x, y);

                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(42, 42));
        iconPanel.setOpaque(false);

        add(contentPanel, BorderLayout.CENTER);
        add(iconPanel, BorderLayout.EAST);
    }

    public StatCard(String title, String value, String subtext, Color accentColor) {
        this(title, value, subtext, accentColor, "●");
    }

    public void setValue(String value) {
        valueLabel.setText(value);
        repaint();
    }

    public void setSubtext(String subtext) {
        subtextLabel.setText(subtext);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Card Surface
        g2.setColor(UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, w, h, 12, 12);

        // Border
        g2.setColor(UITheme.BORDER);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }
}
