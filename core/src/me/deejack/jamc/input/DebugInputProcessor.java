package me.deejack.jamc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import me.deejack.jamc.game.utils.DebugHud;

public class DebugInputProcessor implements InputProcessor {
  private boolean vsyncEnabled = false;
  private final Camera camera;

  public DebugInputProcessor(Camera camera) {
    this.camera = camera;
  }

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case Input.Keys.F1 -> {
        vsyncEnabled ^= true;
        Gdx.graphics.setVSync(vsyncEnabled);
        DebugHud.INSTANCE.displayText(0, "VSYNC: " + (vsyncEnabled ? "On" : "Off"), (int) camera.viewportWidth - 100, (int) camera.viewportHeight - 10);
        //DebugHud.INSTANCE.displayText("VSYNC2: " + (vsyncEnabled ? "On" : "Off"), 0, 0);
        //DebugHud.INSTANCE.displayText("VSYNC3: " + (vsyncEnabled ? "On" : "Off"), 100, 100);
      }
    }
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
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
    return false;
  }
}
