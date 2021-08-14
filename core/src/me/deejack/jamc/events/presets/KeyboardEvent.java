package me.deejack.jamc.events.presets;

import me.deejack.jamc.events.EventData;

import java.util.Objects;

public interface KeyboardEvent {
}

final class KeyPressData extends EventData {
  private final int keyCode;

  KeyPressData(int keyCode) {
    this.keyCode = keyCode;
  }

  public int getKeyCode() {
    return keyCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (KeyPressData) obj;
    return this.keyCode == that.keyCode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyCode);
  }

  @Override
  public String toString() {
    return "KeyPressData[" +
            "keyCode=" + keyCode + ']';
  }

}
