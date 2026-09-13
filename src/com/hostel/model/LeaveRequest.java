package com.hostel.model;

import java.io.Serializable;

public class LeaveRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum LeaveType {
        HOME_VISIT("Home Visit"),
        MEDICAL_LEAVE("Medical Emergency / Appointment"),
        ACADEMIC_EVENT("Academic / Conference / Competition"),
        CASUAL_OUTING("Night Outing / Personal");

        private final String label;
        LeaveType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum LeaveStatus {
        PENDING("Pending Approval", "#F59E0B"),
        APPROVED("Approved", "#10B981"),
        REJECTED("Rejected", "#EF4444");

        private final String label;
        private final String colorHex;

        LeaveStatus(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String requestId; // e.g. "LR-2024-108"
    private int studentId;
    private String studentName;
    private String studentRollNo;
    private String roomNumber;
    private LeaveType leaveType;
    private String fromDate; // "YYYY-MM-DD"
    private String toDate;   // "YYYY-MM-DD"
    private String reason;
    private String destinationAddress;
    private String parentConsentPhone;
    private boolean parentAcknowledged;
    private LeaveStatus status;
    private String appliedDate;
    private String reviewedBy;
    private String reviewComments;
    private String reviewedDate;

    public LeaveRequest() {
        this.status = LeaveStatus.PENDING;
    }

    public LeaveRequest(int id, String requestId, int studentId, String studentName, String studentRollNo,
                        String roomNumber, LeaveType leaveType, String fromDate, String toDate,
                        String reason, String destinationAddress, String parentConsentPhone,
                        boolean parentAcknowledged, LeaveStatus status, String appliedDate,
                        String reviewedBy, String reviewComments, String reviewedDate) {
        this.id = id;
        this.requestId = requestId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.roomNumber = roomNumber;
        this.leaveType = leaveType != null ? leaveType : LeaveType.HOME_VISIT;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.destinationAddress = destinationAddress;
        this.parentConsentPhone = parentConsentPhone;
        this.parentAcknowledged = parentAcknowledged;
        this.status = status != null ? status : LeaveStatus.PENDING;
        this.appliedDate = appliedDate;
        this.reviewedBy = reviewedBy;
        this.reviewComments = reviewComments;
        this.reviewedDate = reviewedDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public String getParentConsentPhone() { return parentConsentPhone; }
    public void setParentConsentPhone(String parentConsentPhone) { this.parentConsentPhone = parentConsentPhone; }

    public boolean isParentAcknowledged() { return parentAcknowledged; }
    public void setParentAcknowledged(boolean parentAcknowledged) { this.parentAcknowledged = parentAcknowledged; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getAppliedDate() { return appliedDate; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }

    public String getReviewedDate() { return reviewedDate; }
    public void setReviewedDate(String reviewedDate) { this.reviewedDate = reviewedDate; }
}
