package com.hostel.model;

import java.io.Serializable;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Category {
        PLUMBING("Plumbing & Washroom"),
        ELECTRICAL("Electrical & Power"),
        FURNITURE("Carpentry & Furniture"),
        CLEANLINESS("Housekeeping & Hygiene"),
        INTERNET_WIFI("Network / Wi-Fi"),
        NOISE_DISTURBANCE("Noise / Discipline"),
        OTHER("General Maintenance");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Status {
        PENDING("Pending Triage", "#EF4444"),
        IN_PROGRESS("In Progress", "#3B82F6"),
        RESOLVED("Resolved", "#10B981");

        private final String label;
        private final String colorHex;

        Status(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String complaintId; // e.g. "CMP-2024-042"
    private int studentId;
    private String studentName;
    private String roomNumber;
    private Category category;
    private String description;
    private String priority; // "Low", "Medium", "High", "Urgent"
    private String filedDate;
    private String assignedTo; // Technician name or staff
    private Status status;
    private String resolutionNotes;
    private String resolvedDate;

    public Complaint() {
        this.status = Status.PENDING;
        this.priority = "Medium";
    }

    public Complaint(int id, String complaintId, int studentId, String studentName, String roomNumber,
                     Category category, String description, String priority, String filedDate,
                     String assignedTo, Status status, String resolutionNotes, String resolvedDate) {
        this.id = id;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.category = category != null ? category : Category.OTHER;
        this.description = description;
        this.priority = priority != null ? priority : "Medium";
        this.filedDate = filedDate;
        this.assignedTo = assignedTo;
        this.status = status != null ? status : Status.PENDING;
        this.resolutionNotes = resolutionNotes;
        this.resolvedDate = resolvedDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getFiledDate() { return filedDate; }
    public void setFiledDate(String filedDate) { this.filedDate = filedDate; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(String resolvedDate) { this.resolvedDate = resolvedDate; }
}
