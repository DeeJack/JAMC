package me.deejack.jamc.events.presets;

import me.deejack.jamc.events.EventCollection;
import me.deejack.jamc.events.EventData;
import me.deejack.jamc.events.EventType;

public interface KeyboardEvent extends EventCollection {
  @EventType(eventType = EventType.EventTypes.KEY_PRESS)
  void onKeyPress(KeyboardData keyData);

  @EventType(eventType = EventType.EventTypes.KEY_DOWN)
  void onKeyDown(KeyboardData keyData);

  @EventType(eventType = EventType.EventTypes.KEY_UP)
  void onKeyUp(KeyboardData keyData);

  public final class KeyboardData extends EventData {
    private final int keyCode;

    public KeyboardData(int keyCode) {
      this.keyCode = keyCode;
    }

    public int getKeyCode() {
      return keyCode;
    }
  }
}
