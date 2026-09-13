package com.hostel.event;

public class AppEvent {
    public enum EventType {
        USER_LOGGED_IN,
        USER_LOGGED_OUT,
        STUDENT_CREATED,
        STUDENT_UPDATED,
        STUDENT_DELETED,
        ROOM_ALLOCATED,
        ROOM_UPDATED,
        FEE_CREATED,
        FEE_PAID,
        VISITOR_CHECKED_IN,
        VISITOR_CHECKED_OUT,
        LEAVE_REQUESTED,
        LEAVE_APPROVED,
        LEAVE_REJECTED,
        COMPLAINT_FILED,
        COMPLAINT_STATUS_CHANGED,
        COMPLIANCE_RECORDED,
        SUSTAINABILITY_LOGGED,
        ASSET_ADDED,
        ASSET_CONDITION_FLAGGED,
        NOTICE_POSTED,
        MESS_FEEDBACK_SUBMITTED,
        NOTIFICATION_RECEIVED,
        DATA_REFRESH_REQUESTED,
        DATA_CHANGED,
        THEME_CHANGED
    }

    private final EventType type;
    private final Object payload;
    private final String description;
    private final long timestamp;

    public AppEvent(EventType type, Object payload, String description) {
        this.type = type;
        this.payload = payload;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public EventType getType() { return type; }
    public Object getPayload() { return payload; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
}
