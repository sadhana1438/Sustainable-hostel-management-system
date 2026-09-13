package com.hostel.model;

import java.io.Serializable;

public class FeeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum FeeStatus {
        PAID("Paid", "#10B981"),
        PARTIAL("Partial", "#F59E0B"),
        PENDING("Pending", "#EF4444"),
        OVERDUE("Overdue", "#B91C1C");

        private final String label;
        private final String colorHex;

        FeeStatus(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private int studentId;
    private String studentName;
    private String studentRollNo;
    private String roomNumber;
    private String academicTerm; // e.g. "Fall 2024 - Term 1"
    private double hostelFee;
    private double messFee;
    private double utilityDeposit;
    private double totalFee;
    private double paidAmount;
    private double balance;
    private String dueDate;
    private FeeStatus status;
    private String lastPaymentDate;

    public FeeRecord() {
        this.status = FeeStatus.PENDING;
    }

    public FeeRecord(int id, int studentId, String studentName, String studentRollNo, String roomNumber,
                     String academicTerm, double hostelFee, double messFee, double utilityDeposit,
                     double paidAmount, String dueDate, String lastPaymentDate) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.roomNumber = roomNumber;
        this.academicTerm = academicTerm;
        this.hostelFee = hostelFee;
        this.messFee = messFee;
        this.utilityDeposit = utilityDeposit;
        this.totalFee = hostelFee + messFee + utilityDeposit;
        this.paidAmount = paidAmount;
        this.balance = Math.max(0, this.totalFee - paidAmount);
        this.dueDate = dueDate;
        this.lastPaymentDate = lastPaymentDate;
        this.status = calculateStatus();
    }

    public FeeRecord(int id, int studentId, String studentName, String studentRollNo, String roomNumber,
                     double totalFee, double paidAmount, double balance, FeeStatus status,
                     String academicTerm, String dueDate) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.roomNumber = roomNumber;
        this.academicTerm = academicTerm;
        this.hostelFee = totalFee * 0.7;
        this.messFee = totalFee * 0.2;
        this.utilityDeposit = totalFee * 0.1;
        this.totalFee = totalFee;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.status = status != null ? status : calculateStatus();
        this.dueDate = dueDate;
        this.lastPaymentDate = "";
    }

    public FeeStatus calculateStatus() {
        if (paidAmount >= totalFee && totalFee > 0) {
            return FeeStatus.PAID;
        } else if (paidAmount > 0) {
            return FeeStatus.PARTIAL;
        } else {
            return FeeStatus.PENDING;
        }
    }

    public void recordPayment(double amount, String paymentDate) {
        this.paidAmount += amount;
        this.balance = Math.max(0, this.totalFee - this.paidAmount);
        this.lastPaymentDate = paymentDate;
        this.status = calculateStatus();
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

    public String getAcademicTerm() { return academicTerm; }
    public void setAcademicTerm(String academicTerm) { this.academicTerm = academicTerm; }

    public double getHostelFee() { return hostelFee; }
    public void setHostelFee(double hostelFee) { 
        this.hostelFee = hostelFee;
        this.totalFee = this.hostelFee + this.messFee + this.utilityDeposit;
        this.balance = Math.max(0, this.totalFee - this.paidAmount);
        this.status = calculateStatus();
    }

    public double getMessFee() { return messFee; }
    public void setMessFee(double messFee) { 
        this.messFee = messFee;
        this.totalFee = this.hostelFee + this.messFee + this.utilityDeposit;
        this.balance = Math.max(0, this.totalFee - this.paidAmount);
        this.status = calculateStatus();
    }

    public double getUtilityDeposit() { return utilityDeposit; }
    public void setUtilityDeposit(double utilityDeposit) { 
        this.utilityDeposit = utilityDeposit;
        this.totalFee = this.hostelFee + this.messFee + this.utilityDeposit;
        this.balance = Math.max(0, this.totalFee - this.paidAmount);
        this.status = calculateStatus();
    }

    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { 
        this.paidAmount = paidAmount;
        this.balance = Math.max(0, this.totalFee - this.paidAmount);
        this.status = calculateStatus();
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public FeeStatus getStatus() { return status; }
    public void setStatus(FeeStatus status) { this.status = status; }

    public String getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(String lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
}
