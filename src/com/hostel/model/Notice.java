package com.hostel.model;

import java.io.Serializable;

public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Priority {
        NORMAL("General Notice", "#64748B"),
        HIGH("High Priority", "#F59E0B"),
        URGENT("Urgent Announcement", "#EF4444");

        private final String label;
        private final String colorHex;

        Priority(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String title;
    private String content;
    private String category; // "Mess", "Curfew / Security", "Maintenance", "Hostel Dues", "Events", "General"
    private Priority priority;
    private String postedBy; // e.g. "Chief Warden Dr. Ramesh", "Admin Office"
    private String postedRole;
    private String postedDate;
    private String targetAudience; // "ALL", "STUDENTS", "WARDENS"
    private boolean isPublished;
    private boolean readByCurrentUser; // transient/per-student view state

    public Notice() {
        this.priority = Priority.NORMAL;
        this.isPublished = true;
    }

    public Notice(int id, String title, String content, String category, Priority priority, 
                  String postedBy, String postedRole, String postedDate, String targetAudience, 
                  boolean isPublished, boolean readByCurrentUser) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.priority = priority != null ? priority : Priority.NORMAL;
        this.postedBy = postedBy;
        this.postedRole = postedRole;
        this.postedDate = postedDate;
        this.targetAudience = targetAudience != null ? targetAudience : "ALL";
        this.isPublished = isPublished;
        this.readByCurrentUser = readByCurrentUser;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getPostedRole() { return postedRole; }
    public void setPostedRole(String postedRole) { this.postedRole = postedRole; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean isPublished) { this.isPublished = isPublished; }

    public boolean isReadByCurrentUser() { return readByCurrentUser; }
    public void setReadByCurrentUser(boolean readByCurrentUser) { this.readByCurrentUser = readByCurrentUser; }
}
