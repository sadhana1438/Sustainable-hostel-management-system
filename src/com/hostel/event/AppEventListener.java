package com.hostel.event;

@FunctionalInterface
public interface AppEventListener {
    void onEvent(AppEvent event);
}
