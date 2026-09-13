package com.hostel.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    public enum Variant {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        DANGER,
        GHOST,
        OUTLINE
    }

    private final Variant variant;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public CustomButton(String text, Variant variant) {
        super(text);
        this.variant = variant != null ? variant : Variant.PRIMARY;
        initButton();
    }

    public CustomButton(String text) {
        this(text, Variant.PRIMARY);
    }

    private void initButton() {
        setFont(UITheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int width = getWidth();
        int height = getHeight();

        Color bgColor;
        Color fgColor;
        Color borderColor = null;

        switch (variant) {
            case PRIMARY:
                bgColor = isPressed ? UITheme.PRIMARY_HOVER.darker() : (isHovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY);
                fgColor = Color.WHITE;
                break;
            case SECONDARY:
                bgColor = isPressed ? UITheme.BORDER : (isHovered ? new Color(241, 245, 249) : Color.WHITE);
                fgColor = UITheme.TEXT_MAIN;
                borderColor = UITheme.BORDER;
                break;
            case SUCCESS:
                bgColor = isPressed ? UITheme.SUCCESS.darker() : (isHovered ? new Color(5, 150, 105) : UITheme.SUCCESS);
                fgColor = Color.WHITE;
                break;
            case DANGER:
                bgColor = isPressed ? UITheme.DANGER.darker() : (isHovered ? new Color(220, 38, 38) : UITheme.DANGER);
                fgColor = Color.WHITE;
                break;
            case GHOST:
                bgColor = isPressed ? new Color(226, 232, 240) : (isHovered ? new Color(241, 245, 249) : new Color(0, 0, 0, 0));
                fgColor = UITheme.TEXT_MAIN;
                break;
            case OUTLINE:
                bgColor = isPressed ? UITheme.PRIMARY_LIGHT : (isHovered ? UITheme.PRIMARY_LIGHT : Color.WHITE);
                fgColor = UITheme.PRIMARY;
                borderColor = UITheme.PRIMARY;
                break;
            default:
                bgColor = UITheme.PRIMARY;
                fgColor = Color.WHITE;
        }

        if (!isEnabled()) {
            bgColor = new Color(241, 245, 249);
            fgColor = UITheme.TEXT_MUTED;
            borderColor = UITheme.BORDER;
        }

        // Background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Border if applicable
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        // Text
        g2.setFont(getFont());
        g2.setColor(fgColor);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width + 24, 80), Math.max(d.height + 12, 36));
    }
}
