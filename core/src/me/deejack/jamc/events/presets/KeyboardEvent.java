package me.deejack.jamc.events.presets;

import me.deejack.jamc.events.EventData;

import java.util.Objects;

public interface KeyboardEvent {
  void onKeyPress(KeyboardData keyData);

  void onKeyDown(KeyboardData keyData);

  void onKeyUp(KeyboardData keyData);

  public final class KeyboardData extends EventData {
    private final int keyCode;

    public KeyboardData(int keyCode) {
      this.keyCode = keyCode;
    }

    public int getKeyCode() {
      return keyCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (KeyboardData) obj;
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
}
