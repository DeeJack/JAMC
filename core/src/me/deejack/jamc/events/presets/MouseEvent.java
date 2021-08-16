package me.deejack.jamc.events.presets;

import com.badlogic.gdx.Input;
import me.deejack.jamc.events.EventData;

public interface MouseEvent {
  void onButtonPressed(KeyboardEvent.KeyboardData keyData);

  void onMouseMoved(KeyboardEvent.KeyboardData keyData);

  void onScrolled(KeyboardEvent.KeyboardData keyData);

  final class MouseMoveData extends EventData {
    private final int mouseX;
    private final int mouseY;
    private final float angleX;
    private final float angleY;

    public MouseMoveData(int mouseX, int mouseY, float angleX, float angleY) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.angleX = angleX;
      this.angleY = angleY;
    }

    public int getMouseX() {
      return mouseX;
    }

    public int getMouseY() {
      return mouseY;
    }

    public float getAngleX() {
      return angleX;
    }

    public float getAngleY() {
      return angleY;
    }
  }

  final class MousePressData extends EventData {
    private final int buttonPressed;
    private final int screenX;
    private final int screenY;

    /**
     * @param screenX
     * @param screenY
     * @param buttonPressed See {@link Input.Buttons}
     */
    public MousePressData(int screenX, int screenY, int buttonPressed) {
      this.screenX = screenX;
      this.screenY = screenY;
      this.buttonPressed = buttonPressed;
    }

    public int getButtonPressed() {
      return buttonPressed;
    }

    public int getScreenX() {
      return screenX;
    }

    public int getScreenY() {
      return screenY;
    }
  }

  final class MouseScrollData extends EventData {
    private final float scrollAmount;

    public MouseScrollData(float scrollAmount) {
      this.scrollAmount = scrollAmount;
    }

    public float getButtonPressed() {
      return scrollAmount;
    }
  }
}
