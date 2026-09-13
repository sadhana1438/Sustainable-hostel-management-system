package com.hostel.model;

import java.io.Serializable;

public class ComplianceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private String studentName;
    private String studentRollNo;
    private String roomNumber;
    private String ruleViolated; // e.g. "Unauthorized Electrical Appliances", "Late Entry Violation", "Noise Curfew"
    private String violationDate;
    private String description;
    private String actionTaken; // e.g. "Written Warning Issued", "Fine Imposed", "Parent Notified", "Community Duty"
    private double fineAmount;
    private String recordedBy;
    private boolean finePaid;

    public ComplianceRecord() {}

    public ComplianceRecord(int id, int studentId, String studentName, String studentRollNo,
                            String roomNumber, String ruleViolated, String violationDate,
                            String description, String actionTaken, double fineAmount,
                            String recordedBy, boolean finePaid) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.roomNumber = roomNumber;
        this.ruleViolated = ruleViolated;
        this.violationDate = violationDate;
        this.description = description;
        this.actionTaken = actionTaken;
        this.fineAmount = fineAmount;
        this.recordedBy = recordedBy;
        this.finePaid = finePaid;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRuleViolated() { return ruleViolated; }
    public void setRuleViolated(String ruleViolated) { this.ruleViolated = ruleViolated; }

    public String getViolationDate() { return violationDate; }
    public void setViolationDate(String violationDate) { this.violationDate = violationDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }
}
