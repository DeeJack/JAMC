package me.deejack.jamc.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface Event<T extends EventData> {
    void onEvent(T eventData);
}

class EventBus {
    // TODO: raw use of Event... what to do???
    private Map<EventType, List<Event>> subscribers = new HashMap<>();
    //private Map<EventType, List<Event<EventData>>> subscribers = new HashMap<>();

    public void publish(EventType eventType, EventData eventData) {
        var eventSubscribers = subscribers.getOrDefault(eventType, new ArrayList<>());

        eventSubscribers.forEach(event -> event.onEvent(eventData));
    }

    public <T extends EventData> void subscribe(EventType eventType, Event<T> event) {
        var eventSubscribers = subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());

        eventSubscribers.add(event);
    }
}

class KeyboardEventData extends EventData {

}

class DemoEvent {
    public void start() {
        var bus = new EventBus();
        bus.subscribe(EventType.KEY_PRESS, this::onKeyPress);
        bus.publish(EventType.KEY_PRESS, new KeyboardEventData());
    }

    public void onKeyPress(KeyboardEventData data) {
        System.out.println("Yes!");
    }

    public static void main(String[] args) {
        var demoEvent = new DemoEvent();
        demoEvent.start();
    }
}


