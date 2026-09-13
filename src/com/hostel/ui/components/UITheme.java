package com.hostel.ui.components;

import com.hostel.event.AppEvent;
import com.hostel.event.AppEventBus;

import java.awt.*;

/**
 * Elegant Pinterest-inspired design tokens and dynamic theme system
 * for Smart Sustainable Hostel Management System.
 * Warm neutral palette (linen, cream, warm terracotta, deep espresso, taupe)
 * with full persistent Light and Dark mode support.
 */
public class UITheme {
    private static boolean darkMode = false;

    // Brand & Primary Accents
    public static Color PRIMARY;
    public static Color PRIMARY_HOVER;
    public static Color PRIMARY_LIGHT;

    // Neutral Surfaces & Dark Sidebar
    public static Color SIDEBAR_BG;
    public static Color SIDEBAR_HOVER;
    public static Color SIDEBAR_ACTIVE;
    public static Color SIDEBAR_TEXT;
    public static Color SIDEBAR_MUTED;

    public static Color APP_BG;
    public static Color CARD_BG;
    public static Color HEADER_BG;
    public static Color BORDER;
    public static Color BORDER_FOCUS;

    // Typography Colors
    public static Color TEXT_MAIN;
    public static Color TEXT_SECONDARY;
    public static Color TEXT_MUTED;
    public static Color TEXT_WHITE = Color.WHITE;

    // Semantic Status Colors
    public static Color SUCCESS;
    public static Color SUCCESS_LIGHT;
    public static Color SUCCESS_TEXT;

    public static Color WARNING;
    public static Color WARNING_LIGHT;
    public static Color WARNING_TEXT;

    public static Color DANGER;
    public static Color DANGER_LIGHT;
    public static Color DANGER_TEXT;

    public static Color INFO;
    public static Color INFO_LIGHT;
    public static Color INFO_TEXT;

    // Sustainability Meters
    public static Color ELEC_COLOR;
    public static Color WATER_COLOR;

    // Refined Typography: Elegant serif for titles/headers, clean sans-serif for body
    public static final Font FONT_TITLE = new Font("Georgia", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Georgia", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Georgia", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_VAL = new Font("Georgia", Font.BOLD, 26);

    static {
        applyTokens();
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        if (darkMode != dark) {
            darkMode = dark;
            applyTokens();
            AppEventBus.getInstance().publish(AppEvent.EventType.THEME_CHANGED, dark, "Theme changed to " + (dark ? "Dark" : "Light"));
        }
    }

    public static void toggleTheme() {
        setDarkMode(!darkMode);
    }

    private static void applyTokens() {
        if (!darkMode) {
            // Warm Pinterest Light Palette
            PRIMARY = new Color(194, 109, 77);         // Warm Terracotta (#C26D4D)
            PRIMARY_HOVER = new Color(171, 88, 57);     // Rich Terracotta Hover
            PRIMARY_LIGHT = new Color(249, 239, 233);   // Soft Terracotta Tint

            SIDEBAR_BG = new Color(45, 36, 32);        // Deep Espresso (#2D2420)
            SIDEBAR_HOVER = new Color(60, 48, 43);
            SIDEBAR_ACTIVE = new Color(75, 60, 54);
            SIDEBAR_TEXT = new Color(239, 233, 225);   // Warm Linen (#EFE9E1)
            SIDEBAR_MUTED = new Color(168, 154, 144);  // Warm Stone

            APP_BG = new Color(250, 247, 242);         // Warm Porcelain Cream (#FAF7F2)
            CARD_BG = Color.WHITE;                     // Warm Crisp White
            HEADER_BG = Color.WHITE;
            BORDER = new Color(233, 226, 216);         // Warm Oatmeal Sand (#E9E2D8)
            BORDER_FOCUS = new Color(194, 109, 77);

            TEXT_MAIN = new Color(45, 36, 32);         // Deep Warm Espresso
            TEXT_SECONDARY = new Color(110, 93, 83);   // Warm Taupe / Chestnut
            TEXT_MUTED = new Color(158, 140, 130);     // Soft Warm Clay

            SUCCESS = new Color(61, 139, 104);         // Sage Emerald (#3D8B68)
            SUCCESS_LIGHT = new Color(235, 245, 238);
            SUCCESS_TEXT = new Color(30, 85, 60);

            WARNING = new Color(201, 122, 43);         // Warm Amber Ochre (#C97A2B)
            WARNING_LIGHT = new Color(253, 243, 231);
            WARNING_TEXT = new Color(138, 77, 18);

            DANGER = new Color(200, 75, 75);           // Soft Terracotta Crimson (#C84B4B)
            DANGER_LIGHT = new Color(253, 240, 240);
            DANGER_TEXT = new Color(145, 35, 35);

            INFO = new Color(90, 125, 143);            // Muted Slate Teal (#5A7D8F)
            INFO_LIGHT = new Color(239, 244, 247);
            INFO_TEXT = new Color(45, 80, 98);

            ELEC_COLOR = new Color(212, 139, 56);      // Warm Ochre
            WATER_COLOR = new Color(78, 135, 153);     // Sage Teal
        } else {
            // Warm Pinterest Dark Palette (Deep Charcoal / Roasted Espresso / Warm Almond)
            PRIMARY = new Color(217, 125, 88);         // Terracotta Glow (#D97D58)
            PRIMARY_HOVER = new Color(232, 140, 103);
            PRIMARY_LIGHT = new Color(55, 35, 28);

            SIDEBAR_BG = new Color(24, 20, 18);        // Deep Charcoal Espresso (#181412)
            SIDEBAR_HOVER = new Color(38, 30, 26);
            SIDEBAR_ACTIVE = new Color(53, 44, 39);
            SIDEBAR_TEXT = new Color(246, 242, 236);   // Warm Almond Cream
            SIDEBAR_MUTED = new Color(160, 146, 136);

            APP_BG = new Color(24, 20, 18);            // Warm Charcoal Background
            CARD_BG = new Color(35, 29, 26);           // Dark Cacao Walnut (#231D1A)
            HEADER_BG = new Color(35, 29, 26);
            BORDER = new Color(56, 47, 42);            // Warm Clay Border (#382F2A)
            BORDER_FOCUS = new Color(217, 125, 88);

            TEXT_MAIN = new Color(246, 242, 236);      // Warm Almond Cream
            TEXT_SECONDARY = new Color(184, 171, 160); // Warm Oatmeal
            TEXT_MUTED = new Color(133, 118, 107);     // Dried Clay

            SUCCESS = new Color(82, 165, 125);
            SUCCESS_LIGHT = new Color(30, 50, 40);
            SUCCESS_TEXT = new Color(160, 230, 190);

            WARNING = new Color(229, 152, 74);
            WARNING_LIGHT = new Color(55, 40, 25);
            WARNING_TEXT = new Color(255, 200, 140);

            DANGER = new Color(224, 97, 97);
            DANGER_LIGHT = new Color(55, 28, 28);
            DANGER_TEXT = new Color(255, 175, 175);

            INFO = new Color(106, 149, 168);
            INFO_LIGHT = new Color(28, 42, 48);
            INFO_TEXT = new Color(175, 215, 235);

            ELEC_COLOR = new Color(229, 152, 74);
            WATER_COLOR = new Color(106, 149, 168);
        }
    }

    public static void applyQualityRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
