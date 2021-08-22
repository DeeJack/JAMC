package me.deejack.jamc.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import me.deejack.jamc.hud.settings.SettingsPage;

public class UserInterface {
  private final Hud hud;
  private final SettingsPage mainSettings;
  private boolean paused = false;

  public UserInterface(Hud hud, SettingsPage mainSettings) {
    this.hud = hud;
    this.mainSettings = mainSettings;
  }

  public void escMenu() {
    paused = !paused;
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    Gdx.input.setCursorCatched(!paused);
    if (paused) {
      Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
      mainSettings.open();
    } else {
      Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
      mainSettings.close();
    }
  }

  public boolean isGamePaused() {
    return paused;
  }

  public boolean isInventoryOpen() {
    return hud.getInventoryHud().isOpen();
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

  public void pressMouseButton(int x, int y) {
    if (hud.getInventoryHud().isOpen())
      hud.getInventoryHud().onMousePressed(x, y);
  }
}
