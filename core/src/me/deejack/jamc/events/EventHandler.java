package me.deejack.jamc.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventHandler {
  private static final EventHandler INSTANCE = new EventHandler();
  // TODO: raw use of Event... what to do???
  private final Map<EventType.EventTypes, List<EventWrapper>> subscribers = new HashMap<>();

  private EventHandler() {
    if (INSTANCE != null)
      throw new AssertionError("Singleton already instanced");
  }

  public static EventHandler getInstance() {
    return INSTANCE;
  }

  public static void registerEvent(EventType.EventTypes eventType, Event<EventData> event) {
    INSTANCE.subscribe(eventType, event);
  }

  public static void registerEvent(EventCollection event) {
    INSTANCE.subscribe(event);
  }

  public static void call(EventType.EventTypes eventType, EventData eventData) {
    INSTANCE.publish(eventType, eventData);
  }

  public void publish(EventType.EventTypes eventType, EventData eventData) {
    System.out.println("Called " + eventType);
    var eventSubscribers = subscribers.getOrDefault(eventType, new ArrayList<>());

    eventSubscribers.sort(Comparator.comparing(EventWrapper::getPriority));
    eventSubscribers.forEach(event -> event.call(eventData));
    //eventData.setCancelled(true);
  }

  public <T extends EventData> void subscribe(EventType.EventTypes eventType, Event<T> event) {
    System.out.println("Subscribed to " + eventType);
    var eventSubscribers = subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());

    var eventMethod = event.getClass().getMethods()[0];
    Priority.Priorities priority = Priority.Priorities.NORMAL;

    if (eventMethod.isAnnotationPresent(Priority.class)) {
      priority = eventMethod.getAnnotation(Priority.class).priority();
    }

    eventSubscribers.add(new EventWrapper(event, eventMethod, priority));
  }

  public <T extends EventData> void subscribe(EventCollection event) {
    var eventClass = event.getClass();
    for (var method : eventClass.getMethods()) {
      System.out.println("Registering method");
      if (method.getParameterCount() != 1)
        continue;

      var eventDataParameter = method.getParameters()[0];
      if (!(eventDataParameter.getType().getSuperclass() == EventData.class || (eventDataParameter.getType().getSuperclass() != null &&
              eventDataParameter.getType().getSuperclass().getSuperclass() == EventData.class))) // IF the parameter is subclass of 'EventData'
        continue;
      System.out.println("Method accepted");
      EventType.EventTypes eventType = null;
      if (method.isAnnotationPresent(EventType.class))
        eventType = method.getAnnotation(EventType.class).eventType();
      else if (eventDataParameter.getType().isAnnotationPresent(EventType.class))
        eventType = eventDataParameter.getType().getAnnotation(EventType.class).eventType();
      else if (eventClass.getInterfaces().length != 0) { // Search for the method in the interfaces it implements
        boolean foundType = false;
        for (var implementedInterface : eventClass.getInterfaces()) {
          try {
            Method superMethod = implementedInterface.getMethod(method.getName(), method.getParameterTypes());
            if (superMethod.isAnnotationPresent(EventType.class)) { // If the super method has the event type, perfect
              eventType = superMethod.getAnnotation(EventType.class).eventType();
              foundType = true;
              break;
            }
          } catch (NoSuchMethodException ignored) {
          }
        }
        if (!foundType) // If the type wasn't found
          continue;
      } else
        continue;
      System.out.println("Parameter of EventData class");
      Priority.Priorities priority = Priority.Priorities.NORMAL;
      if (method.isAnnotationPresent(Priority.class)) {
        priority = method.getAnnotation(Priority.class).priority();
      }

      System.out.println("Subscribed to " + eventType);
      var eventSubscribers = subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());

      eventSubscribers.add(new EventWrapper(event, method, priority));
    }
  }

  public final static class EventWrapper {
    private final Priority.Priorities priority;
    private final Method event;
    private final Object instance;

    public EventWrapper(Object instance, Method event, Priority.Priorities priority) {
      this.instance = instance;
      this.event = event;
      this.priority = priority;
    }

    public void call(EventData data) {
      try {
        event.invoke(instance, data);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    public Priority.Priorities getPriority() {
      return priority;
    }
  }
}
