package com.hostel.model;

import java.io.Serializable;

/**
 * Represents an authenticated user in the hostel management system.
 * Supports three distinct roles: ADMIN, WARDEN, and STUDENT.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        ADMIN("Administrator", "Full system access & configuration"),
        WARDEN("Hostel Warden", "Disciplinary, leave & maintenance administration"),
        STUDENT("Hostel Resident", "Personal details, leave, fees & complaints");

        private final String displayName;
        private final String description;

        Role(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private int id;
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private String email;
    private String phone;
    private String studentId; // If role is STUDENT
    private String roomNumber; // If role is STUDENT
    private String avatarInitial;

    public User() {}

    public User(int id, String username, String password, String fullName, Role role, String email, String phone, String studentId, String roomNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
        this.avatarInitial = fullName != null && !fullName.isEmpty() ? fullName.substring(0, 1).toUpperCase() : "U";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { 
        this.fullName = fullName;
        this.avatarInitial = fullName != null && !fullName.isEmpty() ? fullName.substring(0, 1).toUpperCase() : "U";
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getAvatarInitial() { return avatarInitial; }
}
