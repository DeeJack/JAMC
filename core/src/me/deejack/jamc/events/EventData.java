package me.deejack.jamc.events;

public abstract class EventData {
  private boolean isCancelled = false;

  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }
}
