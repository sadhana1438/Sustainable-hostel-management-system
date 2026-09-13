package com.hostel.model;

import java.io.Serializable;

public class PaymentTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PaymentMethod {
        ONLINE_UPI("UPI / QR", "#4F46E5"),
        NET_BANKING("Net Banking", "#0EA5E9"),
        DEBIT_CREDIT_CARD("Debit/Credit Card", "#8B5CF6"),
        BANK_CHALLAN("Bank Challan", "#F59E0B"),
        CASH_DESK("Hostel Cash Desk", "#10B981"),
        ONLINE_GATEWAY("Razorpay Gateway", "#C26D4D");

        private final String label;
        private final String colorHex;

        PaymentMethod(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private int feeId;
    private int studentId;
    private String studentName;
    private String receiptNo;
    private double amount;
    private PaymentMethod paymentMethod;
    private String transactionRef;
    private String paymentDate;
    private String recordedBy;
    private String notes;

    public PaymentTransaction() {}

    public PaymentTransaction(int id, int feeId, int studentId, String studentName, String receiptNo,
                              double amount, PaymentMethod paymentMethod, String transactionRef,
                              String paymentDate, String recordedBy, String notes) {
        this.id = id;
        this.feeId = feeId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.receiptNo = receiptNo;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.ONLINE_UPI;
        this.transactionRef = transactionRef;
        this.paymentDate = paymentDate;
        this.recordedBy = recordedBy;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
