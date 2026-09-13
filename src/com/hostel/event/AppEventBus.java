package com.hostel.event;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton Central Event Bus coordinating cross-module reactivity.
 * Guarantees listener callbacks execute safely on Swing's Event Dispatch Thread (EDT).
 */
public class AppEventBus {
    private static final AppEventBus INSTANCE = new AppEventBus();

    private final Map<AppEvent.EventType, List<AppEventListener>> specificListeners = new EnumMap<>(AppEvent.EventType.class);
    private final List<AppEventListener> globalListeners = new CopyOnWriteArrayList<>();

    private AppEventBus() {
        for (AppEvent.EventType type : AppEvent.EventType.values()) {
            specificListeners.put(type, new CopyOnWriteArrayList<>());
        }
    }

    public static AppEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(AppEvent.EventType type, AppEventListener listener) {
        if (type == null || listener == null) return;
        specificListeners.get(type).add(listener);
    }

    public void subscribeAll(AppEventListener listener) {
        if (listener == null) return;
        globalListeners.add(listener);
    }

    public void unsubscribe(AppEvent.EventType type, AppEventListener listener) {
        if (type == null || listener == null) return;
        specificListeners.get(type).remove(listener);
    }

    public void unsubscribeAll(AppEventListener listener) {
        if (listener == null) return;
        globalListeners.remove(listener);
        for (List<AppEventListener> list : specificListeners.values()) {
            list.remove(listener);
        }
    }

    public void publish(AppEvent event) {
        if (event == null) return;

        Runnable dispatcher = () -> {
            // Notify specific listeners
            List<AppEventListener> typeListeners = specificListeners.get(event.getType());
            if (typeListeners != null) {
                for (AppEventListener listener : typeListeners) {
                    try {
                        listener.onEvent(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // Notify global listeners
            for (AppEventListener listener : globalListeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            dispatcher.run();
        } else {
            SwingUtilities.invokeLater(dispatcher);
        }
    }

    public void publish(AppEvent.EventType type, Object payload, String description) {
        publish(new AppEvent(type, payload, description));
    }
}
