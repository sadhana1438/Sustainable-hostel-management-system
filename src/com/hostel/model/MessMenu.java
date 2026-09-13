package com.hostel.model;

import java.io.Serializable;

public class MessMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MealType {
        BREAKFAST("Breakfast", "07:30 AM - 09:15 AM", "#F59E0B"),
        LUNCH("Lunch", "12:30 PM - 02:30 PM", "#10B981"),
        SNACKS("Evening Tea & Snacks", "05:00 PM - 06:15 PM", "#0EA5E9"),
        DINNER("Dinner", "07:45 PM - 09:45 PM", "#6366F1");

        private final String label;
        private final String defaultTimings;
        private final String colorHex;

        MealType(String label, String defaultTimings, String colorHex) {
            this.label = label;
            this.defaultTimings = defaultTimings;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getDefaultTimings() { return defaultTimings; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String dayOfWeek; // "Monday", "Tuesday", etc.
    private MealType mealType;
    private String menuItems; // e.g. "Idli, Sambar, Coconut Chutney, Tea/Coffee"
    private String specialItem; // e.g. "Kesari Bath (Special)"
    private String timings; // e.g. "07:30 AM - 09:30 AM"
    private String dietaryType; // "Veg & Non-Veg", "Pure Veg", "Special Feast"

    public MessMenu() {}

    public MessMenu(int id, String dayOfWeek, MealType mealType, String menuItems, 
                    String specialItem, String timings, String dietaryType) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.menuItems = menuItems;
        this.specialItem = specialItem;
        this.timings = timings != null ? timings : (mealType != null ? mealType.getDefaultTimings() : "");
        this.dietaryType = dietaryType != null ? dietaryType : "Pure Veg";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public String getMenuItems() { return menuItems; }
    public void setMenuItems(String menuItems) { this.menuItems = menuItems; }

    public String getSpecialItem() { return specialItem; }
    public void setSpecialItem(String specialItem) { this.specialItem = specialItem; }

    public String getTimings() { return timings; }
    public void setTimings(String timings) { this.timings = timings; }

    public String getDietaryType() { return dietaryType; }
    public void setDietaryType(String dietaryType) { this.dietaryType = dietaryType; }
}
