package me.deejack.jamc.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventType {
  EventTypes eventType();

  public enum EventTypes {
    MOUSE_CLICK,
    KEY_PRESS,
    KEY_UP,
    KEY_DOWN,
    MOUSE_MOVE,
    BLOCK_BREAK,
    BLOCK_PLACE,
    BLOCK_CLICK,
    PLAYER_MOVE,
    MESSAGE_SENT,
    COMMAND_SENT,
    INVENTORY_OPEN,
    MOUSE_SCROLL;
  }
}

