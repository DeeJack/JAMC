package me.deejack.jamc.events;

public interface Event {
  void onEvent(EventData eventData);

  default Priority getPriority() {
    return Priority.NORMAL;
  }
}