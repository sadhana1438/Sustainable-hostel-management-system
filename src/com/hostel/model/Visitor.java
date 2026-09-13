package com.hostel.model;

import java.io.Serializable;

public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum VisitorStatus {
        INSIDE("Currently Inside", "#10B981"),
        CHECKED_OUT("Checked Out", "#64748B"),
        REJECTED("Entry Denied", "#EF4444");

        private final String label;
        private final String colorHex;

        VisitorStatus(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String visitorName;
    private String phone;
    private String relation; // e.g. "Parent", "Guardian", "Sibling", "Academic Peer"
    private int studentId;
    private String studentName;
    private String roomNumber;
    private String purpose;
    private String visitDate; // "YYYY-MM-DD"
    private String checkInTime; // "HH:mm"
    private String checkOutTime; // "HH:mm"
    private String idProofType; // e.g. "National ID", "Driver License", "College ID"
    private String idProofNumber;
    private VisitorStatus status;
    private String securityOfficer;

    public Visitor() {
        this.status = VisitorStatus.INSIDE;
    }

    public Visitor(int id, String visitorName, String phone, String relation, int studentId, 
                   String studentName, String roomNumber, String purpose, String visitDate, 
                   String checkInTime, String checkOutTime, String idProofType, 
                   String idProofNumber, VisitorStatus status, String securityOfficer) {
        this.id = id;
        this.visitorName = visitorName;
        this.phone = phone;
        this.relation = relation;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.purpose = purpose;
        this.visitDate = visitDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.idProofType = idProofType;
        this.idProofNumber = idProofNumber;
        this.status = status != null ? status : VisitorStatus.INSIDE;
        this.securityOfficer = securityOfficer;
    }

    public boolean isCurrentlyInside() {
        return this.status == VisitorStatus.INSIDE;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { 
        this.checkOutTime = checkOutTime;
        if (checkOutTime != null && !checkOutTime.isEmpty()) {
            this.status = VisitorStatus.CHECKED_OUT;
        }
    }

    public String getIdProofType() { return idProofType; }
    public void setIdProofType(String idProofType) { this.idProofType = idProofType; }

    public String getIdProofNumber() { return idProofNumber; }
    public void setIdProofNumber(String idProofNumber) { this.idProofNumber = idProofNumber; }

    public VisitorStatus getStatus() { return status; }
    public void setStatus(VisitorStatus status) { this.status = status; }

    public String getSecurityOfficer() { return securityOfficer; }
    public void setSecurityOfficer(String securityOfficer) { this.securityOfficer = securityOfficer; }
}
