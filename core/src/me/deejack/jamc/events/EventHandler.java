package me.deejack.jamc.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventHandler {
  private static final EventHandler INSTANCE = new EventHandler();
  private final List<Event<EventData>> registeredEvents = new ArrayList<>();

  private EventHandler() {
    if (INSTANCE != null)
      throw new AssertionError("Singleton already instanced");
  }

  public static EventHandler getInstance() {
    return INSTANCE;
  }

  public <T extends EventData> void callEvent(Events eventType, T eventData) {
    registeredEvents.stream()
            .filter(event -> event.getType() == eventType)
            .sorted(Comparator.comparing(Event::getPriority))
            .forEach(event -> event.onEvent(eventData));
  }

  public void registerEvent(Event<EventData> event) {
    registeredEvents.add(event);
  }
}
