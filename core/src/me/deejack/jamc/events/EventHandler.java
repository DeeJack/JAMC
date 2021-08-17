package me.deejack.jamc.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandler {
  private static final EventHandler INSTANCE = new EventHandler();
  // TODO: raw use of Event... what to do???
  private final Map<EventType, List<Event<EventData>>> subscribers = new HashMap<>();

  private EventHandler() {
    if (INSTANCE != null)
      throw new AssertionError("Singleton already instanced");
  }

  public static EventHandler getInstance() {
    return INSTANCE;
  }

  public static void registerEvent(EventType eventType, Event<EventData> event) {
    INSTANCE.subscribe(eventType, event);
  }

  public static void call(EventType eventType, EventData eventData) {
    INSTANCE.publish(eventType, eventData);
  }

  public void publish(EventType eventType, EventData eventData) {
    System.out.println("Called " + eventType);
    var eventSubscribers = subscribers.getOrDefault(eventType, new ArrayList<>());

    eventSubscribers.forEach(event -> event.onEvent(eventData));
    //eventData.setCancelled(true);
  }

  public <T extends EventData> void subscribe(EventType eventType, Event<T> event) {
    System.out.println("Subscribed to " + eventType);
    var eventSubscribers = subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());

    eventSubscribers.add((Event<EventData>) event);
  }
}
