package me.deejack.jamc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.hud.UserInterface;

public class UIInputProcessor implements InputProcessor {
  private final UserInterface ui;
  private final Player currentPlayer;

  public UIInputProcessor(UserInterface userInterface, Player currentPlayer) {
    this.ui = userInterface;
    this.currentPlayer = currentPlayer;
  }

  @Override
  public boolean keyDown(int keyCode) {
    if (ui.isGamePaused() && keyCode != Keys.ESCAPE)
      return true;
    switch (keyCode) {
      case Keys.ESCAPE:
        if (ui.isInventoryOpen())
          ui.toggleInventory();
        else
          ui.escMenu();
        return true;
      case Keys.NUM_1:
      case Keys.NUM_2:
      case Keys.NUM_3:
      case Keys.NUM_4:
      case Keys.NUM_5:
      case Keys.NUM_6:
      case Keys.NUM_7:
      case Keys.NUM_8:
      case Keys.NUM_9:
        currentPlayer.getInventory().setSelectedSlot(keyCode - 8 + 1);
        return true;
      case Keys.F11:
        if (Gdx.graphics.isFullscreen())
          Gdx.graphics.setWindowedMode(1280, 720);
        else Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        break;
      case Keys.F12:
        JAMC.DEBUG = false;
        break;
      case Keys.E:
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        ui.toggleInventory();
    }
    return ui.isGamePaused();
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    ui.pressMouseButton(screenX, screenY);
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    ui.updateCursorPosition(screenX, screenY);
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    currentPlayer.getInventory().setSelectedSlot(currentPlayer.getInventory().getSelectedSlot() + (int) amountY);
    return ui.isGamePaused() || ui.isInventoryOpen();
  }

  @Override
  public boolean touchCancelled(int a, int b, int c, int d) {
    return false;
  }

}