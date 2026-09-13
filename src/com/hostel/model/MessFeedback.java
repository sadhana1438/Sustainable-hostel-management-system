package com.hostel.model;

import java.io.Serializable;

public class MessFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private String studentName;
    private String roomNumber;
    private String mealType; // "Breakfast", "Lunch", "Snacks", "Dinner"
    private int rating; // 1 to 5 stars
    private String category; // "Taste & Quality", "Hygiene & Cleanliness", "Portion Size", "Staff Service", "Special Request"
    private String comments;
    private boolean isFoodComplaint;
    private String status; // "Received", "Reviewed", "Action Taken"
    private String dateSubmitted;

    public MessFeedback() {
        this.status = "Received";
    }

    public MessFeedback(int id, int studentId, String studentName, String roomNumber,
                        String mealType, int rating, String category, String comments,
                        boolean isFoodComplaint, String status, String dateSubmitted) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.mealType = mealType;
        this.rating = rating;
        this.category = category;
        this.comments = comments;
        this.isFoodComplaint = isFoodComplaint;
        this.status = status != null ? status : "Received";
        this.dateSubmitted = dateSubmitted;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public boolean isFoodComplaint() { return isFoodComplaint; }
    public void setFoodComplaint(boolean isFoodComplaint) { this.isFoodComplaint = isFoodComplaint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(String dateSubmitted) { this.dateSubmitted = dateSubmitted; }
}
