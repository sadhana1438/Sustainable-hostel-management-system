package com.hostel.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CustomTextField extends JTextField implements FocusListener {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 8;

    public CustomTextField(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        initField();
    }

    public CustomTextField() {
        this("");
    }

    private void initField() {
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_MAIN);
        setCaretColor(UITheme.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        addFocusListener(this);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(isEnabled() ? Color.WHITE : new Color(248, 250, 252));
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Border
        if (isFocused) {
            g2.setColor(UITheme.BORDER_FOCUS);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(UITheme.BORDER);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();

        super.paintComponent(g);

        // Placeholder
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            Graphics2D gp = (Graphics2D) g.create();
            UITheme.applyQualityRendering(gp);
            gp.setColor(UITheme.TEXT_MUTED);
            gp.setFont(getFont());
            FontMetrics fm = gp.getFontMetrics();
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            gp.drawString(placeholder, getInsets().left, y);
            gp.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 38));
    }
}
