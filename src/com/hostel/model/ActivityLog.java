package com.hostel.model;

import java.io.Serializable;

public class ActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String timestamp; // "YYYY-MM-DD HH:mm:ss"
    private String actorName;
    private String actorRole; // "ADMIN", "WARDEN", "STUDENT", "SYSTEM"
    private String actionType; // "STUDENT_ADDED", "ROOM_ALLOCATED", "PAYMENT_RECORDED", "LEAVE_SUBMITTED", "LEAVE_APPROVED", "COMPLAINT_FILED", "COMPLAINT_RESOLVED", "RESOURCE_LOGGED", "NOTICE_POSTED", "VISITOR_CHECKED_IN", "VISITOR_CHECKED_OUT"
    private String description;
    private String entityId;
    private String badgeColor;

    public ActivityLog() {}

    public ActivityLog(int id, String timestamp, String actorName, String actorRole, 
                       String actionType, String description, String entityId) {
        this.id = id;
        this.timestamp = timestamp;
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.actionType = actionType;
        this.description = description;
        this.entityId = entityId;
        this.badgeColor = computeBadgeColor(actionType);
    }

    private String computeBadgeColor(String action) {
        if (action == null) return "#64748B";
        if (action.contains("APPROVED") || action.contains("RESOLVED") || action.contains("RECORDED") || action.contains("ALLOCATED")) {
            return "#10B981";
        }
        if (action.contains("LEAVE") || action.contains("SUBMITTED") || action.contains("CHECKED_IN")) {
            return "#3B82F6";
        }
        if (action.contains("COMPLAINT") || action.contains("DAMAGED") || action.contains("REJECTED")) {
            return "#EF4444";
        }
        if (action.contains("RESOURCE") || action.contains("ANOMALY")) {
            return "#F59E0B";
        }
        return "#6366F1";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { 
        this.actionType = actionType;
        this.badgeColor = computeBadgeColor(actionType);
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getBadgeColor() { return badgeColor; }
}
