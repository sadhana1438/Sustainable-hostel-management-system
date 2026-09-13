package com.hostel.ui.components;

import com.hostel.model.SustainabilityUsage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class DualResourceChart extends JPanel {
    public enum Mode {
        BOTH,
        ELECTRICITY_ONLY,
        WATER_ONLY
    }

    private List<SustainabilityUsage> data;
    private Mode mode = Mode.BOTH;
    private int hoveredBarIndex = -1;

    public DualResourceChart(List<SustainabilityUsage> data) {
        this.data = data;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 240));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (data == null || data.isEmpty()) return;
                int count = data.size();
                int padLeft = 60;
                int padRight = 30;
                int chartW = getWidth() - padLeft - padRight;
                int groupW = chartW / count;
                int x = e.getX();
                if (x >= padLeft && x < getWidth() - padRight) {
                    int index = (x - padLeft) / groupW;
                    if (index >= 0 && index < count && index != hoveredBarIndex) {
                        hoveredBarIndex = index;
                        repaint();
                    }
                } else if (hoveredBarIndex != -1) {
                    hoveredBarIndex = -1;
                    repaint();
                }
            }
        });
    }

    public void setData(List<SustainabilityUsage> data) {
        this.data = data;
        repaint();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        if (data == null || data.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(UITheme.FONT_BODY);
            g2.drawString("No sustainability usage data recorded yet.", w / 2 - 120, h / 2);
            g2.dispose();
            return;
        }

        int padLeft = 65;
        int padRight = 30;
        int padTop = 30;
        int padBottom = 45;

        int chartW = w - padLeft - padRight;
        int chartH = h - padTop - padBottom;

        // Find max values
        double maxElec = 1000;
        double maxWater = 100;
        for (SustainabilityUsage s : data) {
            if (s.getElectricityKwh() > maxElec) maxElec = s.getElectricityKwh();
            if (s.getWaterKiloLitres() > maxWater) maxWater = s.getWaterKiloLitres();
        }
        maxElec *= 1.15; // padding
        maxWater *= 1.15;

        // Draw horizontal grid lines
        g2.setColor(new Color(241, 245, 249));
        g2.setStroke(new BasicStroke(1.0f));
        int gridCount = 4;
        for (int i = 0; i <= gridCount; i++) {
            int y = padTop + (chartH * i / gridCount);
            g2.drawLine(padLeft, y, w - padRight, y);

            // Left axis labels (Electricity kWh)
            if (mode != Mode.WATER_ONLY) {
                double val = maxElec * (gridCount - i) / gridCount;
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_TINY);
                String lbl = String.format("%.0fk", val / 1000.0);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, padLeft - fm.stringWidth(lbl) - 8, y + 4);
            }
        }

        int count = data.size();
        int groupW = chartW / count;
        int barW = Math.max(12, groupW / 3);

        for (int i = 0; i < count; i++) {
            SustainabilityUsage u = data.get(i);
            int groupX = padLeft + (i * groupW);
            int centerX = groupX + (groupW / 2);

            // Anomaly indicator
            if (u.hasAnyAnomaly()) {
                g2.setColor(new Color(254, 226, 226, 120));
                g2.fillRoundRect(groupX + 4, padTop - 10, groupW - 8, chartH + 10, 8, 8);
                g2.setColor(UITheme.DANGER);
                g2.setFont(UITheme.FONT_TINY);
                g2.drawString("⚠ Jump", centerX - 18, padTop - 14);
            }

            // Electricity Bar
            if (mode != Mode.WATER_ONLY) {
                int barH = (int) ((u.getElectricityKwh() / maxElec) * chartH);
                int bx = (mode == Mode.BOTH) ? centerX - barW - 2 : centerX - (barW / 2);
                int by = padTop + chartH - barH;

                Color bColor = u.isElectricityAnomaly() ? UITheme.DANGER : UITheme.ELEC_COLOR;
                if (i == hoveredBarIndex) bColor = bColor.darker();

                g2.setColor(bColor);
                g2.fillRoundRect(bx, by, barW, barH, 6, 6);

                if (i == hoveredBarIndex || count <= 6) {
                    g2.setFont(UITheme.FONT_TINY);
                    g2.setColor(UITheme.TEXT_SECONDARY);
                    String valText = String.format("%.0f", u.getElectricityKwh());
                    g2.drawString(valText, bx - 2, by - 4);
                }
            }

            // Water Bar
            if (mode != Mode.ELECTRICITY_ONLY) {
                int barH = (int) ((u.getWaterKiloLitres() / maxWater) * chartH);
                int bx = (mode == Mode.BOTH) ? centerX + 2 : centerX - (barW / 2);
                int by = padTop + chartH - barH;

                Color bColor = u.isWaterAnomaly() ? UITheme.DANGER : UITheme.WATER_COLOR;
                if (i == hoveredBarIndex) bColor = bColor.darker();

                g2.setColor(bColor);
                g2.fillRoundRect(bx, by, barW, barH, 6, 6);

                if ((i == hoveredBarIndex || count <= 6) && mode == Mode.WATER_ONLY) {
                    g2.setFont(UITheme.FONT_TINY);
                    g2.setColor(UITheme.TEXT_SECONDARY);
                    String valText = String.format("%.0fkL", u.getWaterKiloLitres());
                    g2.drawString(valText, bx - 2, by - 4);
                }
            }

            // Month Label
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.setFont(UITheme.FONT_SMALL);
            FontMetrics fm = g2.getFontMetrics();
            int lx = centerX - (fm.stringWidth(u.getMonthLabel()) / 2);
            g2.drawString(u.getMonthLabel(), lx, padTop + chartH + 20);

            // Variance percentage
            if (i > 0) {
                double pct = (mode == Mode.WATER_ONLY) ? u.getWaterChangePct() : u.getElectricityChangePct();
                String pctStr = (pct >= 0 ? "+" : "") + String.format("%.1f%%", pct);
                g2.setFont(UITheme.FONT_TINY);
                g2.setColor(pct > 15.0 ? UITheme.DANGER : (pct < 0 ? UITheme.SUCCESS : UITheme.TEXT_MUTED));
                int px = centerX - (g2.getFontMetrics().stringWidth(pctStr) / 2);
                g2.drawString(pctStr, px, padTop + chartH + 34);
            }
        }

        // Legend at top right
        int legendX = w - padRight - 220;
        int legendY = 12;

        if (mode != Mode.WATER_ONLY) {
            g2.setColor(UITheme.ELEC_COLOR);
            g2.fillRoundRect(legendX, legendY, 12, 12, 3, 3);
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.setFont(UITheme.FONT_TINY);
            g2.drawString("Electricity (kWh)", legendX + 16, legendY + 10);
        }

        if (mode != Mode.ELECTRICITY_ONLY) {
            int waterLegX = legendX + 110;
            g2.setColor(UITheme.WATER_COLOR);
            g2.fillRoundRect(waterLegX, legendY, 12, 12, 3, 3);
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.setFont(UITheme.FONT_TINY);
            g2.drawString("Water (kL)", waterLegX + 16, legendY + 10);
        }

        g2.dispose();
    }
}
