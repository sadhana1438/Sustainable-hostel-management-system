package com.hostel.model;

import java.io.Serializable;

public class Asset implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Condition {
        GOOD("Good / Operational", "#10B981"),
        DAMAGED("Damaged", "#EF4444"),
        UNDER_REPAIR("Under Repair", "#F59E0B"),
        MISSING("Missing / Lost", "#64748B");

        private final String label;
        private final String colorHex;

        Condition(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String assetCode; // e.g. "AST-FUR-101"
    private String name;
    private String category; // "Furniture", "Electrical", "Electronics", "Plumbing", "Sports & Gym"
    private int quantity;
    private String location; // e.g. "Block A - Common Room", "Dining Hall", "Room 204"
    private Condition condition;
    private String status; // "In Use", "In Storage", "Decommissioned"
    private double estimatedValue;
    private String lastInspectedDate;
    private String damageNotes;

    public Asset() {
        this.condition = Condition.GOOD;
        this.status = "In Use";
    }

    public Asset(int id, String assetCode, String name, String category, int quantity, 
                 String location, Condition condition, String status, double estimatedValue, 
                 String lastInspectedDate, String damageNotes) {
        this.id = id;
        this.assetCode = assetCode;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.condition = condition != null ? condition : Condition.GOOD;
        this.status = status != null ? status : "In Use";
        this.estimatedValue = estimatedValue;
        this.lastInspectedDate = lastInspectedDate;
        this.damageNotes = damageNotes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(double estimatedValue) { this.estimatedValue = estimatedValue; }

    public String getLastInspectedDate() { return lastInspectedDate; }
    public void setLastInspectedDate(String lastInspectedDate) { this.lastInspectedDate = lastInspectedDate; }

    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }
}
