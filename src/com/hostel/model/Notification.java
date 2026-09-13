package com.hostel.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NotificationType {
        LEAVE("Leave Request", "#3B82F6"),
        FEE("Fee & Payment", "#10B981"),
        COMPLAINT("Complaint & Maintenance", "#EF4444"),
        NOTICE("Notice Board", "#8B5CF6"),
        SUSTAINABILITY("Sustainability Alert", "#F59E0B"),
        VISITOR("Visitor Desk", "#0EA5E9"),
        SYSTEM("System Event", "#64748B");

        private final String label;
        private final String colorHex;

        NotificationType(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private int targetUserId; // 0 for broadcast / role-wide
    private String targetRole; // "ALL", "ADMIN", "WARDEN", "STUDENT"
    private String title;
    private String message;
    private NotificationType type;
    private String timestamp; // "YYYY-MM-DD HH:mm:ss"
    private boolean isRead;
    private String actionLink; // e.g. "MODULE_LEAVE", "MODULE_FEES", "MODULE_COMPLAINTS"

    public Notification() {
        this.isRead = false;
        this.type = NotificationType.SYSTEM;
    }

    public Notification(int id, int targetUserId, String targetRole, String title, 
                        String message, NotificationType type, String timestamp, 
                        boolean isRead, String actionLink) {
        this.id = id;
        this.targetUserId = targetUserId;
        this.targetRole = targetRole != null ? targetRole : "ALL";
        this.title = title;
        this.message = message;
        this.type = type != null ? type : NotificationType.SYSTEM;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.actionLink = actionLink;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTargetUserId() { return targetUserId; }
    public void setTargetUserId(int targetUserId) { this.targetUserId = targetUserId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public String getActionLink() { return actionLink; }
    public void setActionLink(String actionLink) { this.actionLink = actionLink; }
}
