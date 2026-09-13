package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusBadge extends JLabel {
    private Color badgeColor = UITheme.SUCCESS;
    private Color textColor = UITheme.SUCCESS_TEXT;
    private Color bgColor = UITheme.SUCCESS_LIGHT;

    public StatusBadge(String text, Color badgeColor) {
        super(text, SwingConstants.CENTER);
        setBadgeColor(badgeColor);
        setFont(UITheme.FONT_TINY);
        setBorder(new EmptyBorder(4, 10, 4, 10));
        setOpaque(false);
    }

    public StatusBadge(String text) {
        this(text, UITheme.SUCCESS);
    }

    public void setBadgeColor(Color color) {
        if (color == null) color = UITheme.SUCCESS;
        this.badgeColor = color;
        // Generate light background and darker foreground
        this.bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 35);
        this.textColor = color.darker();
        setForeground(this.textColor);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Rounded pill background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, h, h);

        // Subtle border
        g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 80));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width + 12, Math.max(d.height, 24));
    }
}
