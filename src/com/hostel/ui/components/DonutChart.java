package com.hostel.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

public class DonutChart extends JPanel {
    private int occupied;
    private int total;
    private String centerTitle = "Occupancy";
    private Color activeColor = UITheme.PRIMARY;

    public DonutChart(int occupied, int total, String centerTitle, Color activeColor) {
        this.occupied = occupied;
        this.total = Math.max(1, total);
        this.centerTitle = centerTitle != null ? centerTitle : "Occupancy";
        this.activeColor = activeColor != null ? activeColor : UITheme.PRIMARY;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(180, 180));
    }

    public void setValues(int occupied, int total) {
        this.occupied = occupied;
        this.total = Math.max(1, total);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 20;
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        double percentage = Math.min(1.0, (double) occupied / total);
        double angle = percentage * 360.0;

        // Background Track Arc
        g2.setColor(new Color(241, 245, 249));
        g2.fill(new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.PIE));

        // Active Value Arc
        g2.setColor(activeColor);
        g2.fill(new Arc2D.Double(x, y, size, size, 90, -angle, Arc2D.PIE));

        // Center cutout
        int innerSize = (int) (size * 0.68);
        int ix = (w - innerSize) / 2;
        int iy = (h - innerSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(ix, iy, innerSize, innerSize));

        // Center Text
        g2.setColor(UITheme.TEXT_MAIN);
        g2.setFont(UITheme.FONT_HEADER);
        String pctStr = String.format("%.0f%%", percentage * 100.0);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(pctStr)) / 2;
        int ty = (h / 2) + 2;
        g2.drawString(pctStr, tx, ty);

        g2.setFont(UITheme.FONT_TINY);
        g2.setColor(UITheme.TEXT_MUTED);
        FontMetrics fm2 = g2.getFontMetrics();
        int stx = (w - fm2.stringWidth(centerTitle)) / 2;
        g2.drawString(centerTitle, stx, ty + 16);

        g2.dispose();
    }
}
