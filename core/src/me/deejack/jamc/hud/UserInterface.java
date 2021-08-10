package me.deejack.jamc.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;

public class UserInterface {
  private final Hud hud;
  private boolean paused = false;

  public UserInterface(Hud hud) {
    this.hud = hud;
  }

  public void escMenu() {
    paused = !paused;
    Gdx.input.setCursorCatched(!paused);
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    if (paused)
      Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
    else
      Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);

  }

  public boolean isGamePaused() {
    return paused;
  }

  public void toggleInventory() {
    if (hud.getInventoryHud().isOpen())
      hud.getInventoryHud().close();
    else
      hud.getInventoryHud().open();
  }

  public void updateCursorPosition(int x, int y) {
    if (hud.getInventoryHud().isOpen())
      hud.getInventoryHud().updateMouseCursor(x, y);
  }
}
