package com.hostel.model;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RoomStatus {
        AVAILABLE("Available", "#10B981"),
        PARTIAL("Partially Occupied", "#F59E0B"),
        FULL("Full", "#EF4444"),
        MAINTENANCE("Under Maintenance", "#64748B");

        private final String label;
        private final String colorHex;

        RoomStatus(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }

        public String getLabel() { return label; }
        public String getColorHex() { return colorHex; }
    }

    private int id;
    private String roomNumber;
    private String block; // e.g. "A Block", "B Block", "Green Wing"
    private int floor;
    private int capacity;
    private int occupied;
    private double monthlyRent;
    private String roomType; // e.g. "AC 2-Sharing", "Non-AC 3-Sharing", "Single Deluxe"
    private RoomStatus status;
    private String amenities;

    public Room() {
        this.status = RoomStatus.AVAILABLE;
    }

    public Room(int id, String roomNumber, String block, int floor, int capacity, int occupied, 
                double monthlyRent, String roomType, RoomStatus status, String amenities) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.block = block;
        this.floor = floor;
        this.capacity = capacity;
        this.occupied = occupied;
        this.monthlyRent = monthlyRent;
        this.roomType = roomType;
        this.amenities = amenities;
        this.status = calculateStatus(capacity, occupied, status);
    }

    public int getAvailableBeds() {
        return Math.max(0, capacity - occupied);
    }

    public boolean isFull() {
        return occupied >= capacity;
    }

    public void recalculateStatus() {
        if (this.status == RoomStatus.MAINTENANCE) return;
        if (occupied >= capacity) {
            this.status = RoomStatus.FULL;
        } else if (occupied > 0) {
            this.status = RoomStatus.PARTIAL;
        } else {
            this.status = RoomStatus.AVAILABLE;
        }
    }

    private RoomStatus calculateStatus(int cap, int occ, RoomStatus explicit) {
        if (explicit == RoomStatus.MAINTENANCE) return RoomStatus.MAINTENANCE;
        if (occ >= cap) return RoomStatus.FULL;
        if (occ > 0) return RoomStatus.PARTIAL;
        return RoomStatus.AVAILABLE;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { 
        this.capacity = capacity;
        recalculateStatus();
    }

    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { 
        this.occupied = occupied;
        recalculateStatus();
    }

    public double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
}
