package me.deejack.jamc.input;

import com.badlogic.gdx.InputProcessor;
import me.deejack.jamc.events.EventHandler;
import me.deejack.jamc.events.EventType;
import me.deejack.jamc.events.presets.KeyboardEvent;
import me.deejack.jamc.events.presets.MouseEvent;

public class EventInputProcessor implements InputProcessor {
  @Override
  public boolean keyDown(int keycode) {
    var eventData = new KeyboardEvent.KeyboardData(keycode);
    EventHandler.call(EventType.EventTypes.KEY_DOWN, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean keyUp(int keycode) {
    var eventData = new KeyboardEvent.KeyboardData(keycode);
    EventHandler.call(EventType.EventTypes.KEY_UP, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    var eventData = new MouseEvent.MousePressData(screenX, screenY, button);
    EventHandler.call(EventType.EventTypes.MOUSE_CLICK, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    var eventData = new MouseEvent.MouseScrollData(amountY);
    EventHandler.call(EventType.EventTypes.MOUSE_SCROLL, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean touchCancelled(int a, int b, int c, int d) {
    return false;
  }
}
