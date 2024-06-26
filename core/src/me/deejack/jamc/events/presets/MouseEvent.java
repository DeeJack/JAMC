package me.deejack.jamc.events.presets;

import com.badlogic.gdx.Input;
import me.deejack.jamc.events.EventCollection;
import me.deejack.jamc.events.EventData;
import me.deejack.jamc.events.EventType;

public interface MouseEvent extends EventCollection {
  void onMouseButtonPressed(MousePressData buttonPressedData);

  void onMouseMoved(MouseMoveData mouseMoveData);

  void onScrolled(MouseScrollData mouseScrollData);

  @EventType(eventType = EventType.EventTypes.MOUSE_MOVE)
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

  @EventType(eventType = EventType.EventTypes.MOUSE_CLICK)
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

  @EventType(eventType = EventType.EventTypes.MOUSE_SCROLL)
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
