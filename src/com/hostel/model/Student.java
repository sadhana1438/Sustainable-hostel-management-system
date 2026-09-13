package com.hostel.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ACTIVE("Active", "#10B981"),
        INACTIVE("Inactive", "#64748B"),
        SUSPENDED("Suspended", "#EF4444"),
        ON_LEAVE("On Leave", "#F59E0B");

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
    private String studentId; // e.g. "STU-2024-001"
    private String name;
    private String department;
    private String year; // e.g. "1st Year", "2nd Year", "3rd Year", "4th Year"
    private String contact;
    private String email;
    private String parentContact;
    private String emergencyContact;
    private String bloodGroup;
    private int roomId;
    private String roomNumber;
    private String block;
    private Status status;
    private String admissionDate;

    public Student() {
        this.status = Status.ACTIVE;
    }

    public Student(int id, String studentId, String name, String department, String year, String contact, String email, 
                   String parentContact, String emergencyContact, String bloodGroup, int roomId, String roomNumber, 
                   String block, Status status, String admissionDate) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.contact = contact;
        this.email = email;
        this.parentContact = parentContact;
        this.emergencyContact = emergencyContact;
        this.bloodGroup = bloodGroup;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.block = block;
        this.status = status != null ? status : Status.ACTIVE;
        this.admissionDate = admissionDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }
}
