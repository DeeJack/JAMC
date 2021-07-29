package me.deejack.jamc.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hud {
  private OrthographicCamera hudCamera;
  private BitmapFont font;
  private SpriteBatch batch;
  private Texture crosshair;
  private InventoryBar inventoryBar;

  public void create() {
    hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    hudCamera.position.set(Gdx.graphics.getWidth() / 2.F, Gdx.graphics.getHeight() / 2.F, 1.F);
    batch = new SpriteBatch();

    font = new BitmapFont();
    createCrosshair();
    inventoryBar = new InventoryBar();
    inventoryBar.create();
  }

  private void createCrosshair() {
    var crosshairImage = new Pixmap(16 * 4, 16 * 4, Format.RGBA8888);
    crosshairImage.setColor(1, 1, 1, 1);

    // Vertical
    crosshairImage.drawLine(crosshairImage.getWidth() / 2, crosshairImage.getHeight(), crosshairImage.getWidth() / 2, -crosshairImage.getHeight());
    // Horizontal
    crosshairImage.drawLine(-crosshairImage.getWidth(), crosshairImage.getHeight() / 2, crosshairImage.getWidth(), crosshairImage.getHeight() / 2);
    crosshair = new Texture(crosshairImage);
  }

  public void render() {
    hudCamera.update();
    batch.setProjectionMatrix(hudCamera.combined);
    batch.begin();
    font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, hudCamera.viewportHeight);
    batch.draw(crosshair, (hudCamera.viewportWidth / 2) - (crosshair.getWidth() / 2), (hudCamera.viewportHeight / 2) - (crosshair.getHeight() / 2));
    inventoryBar.render(batch, hudCamera.viewportWidth);

    batch.end();
  }

  public void resize(int width, int height) {
    hudCamera.viewportHeight = height;
    hudCamera.viewportWidth = width;
    hudCamera.position.set(width / 2.F, height / 2.F, 1.F);

    hudCamera.update();
  }

  public void dispose() {
    batch.dispose();
    font.dispose();
    crosshair.dispose();
  }

  public void selectInventoryBarSlot(int slot) {
    inventoryBar.selectSlot(slot);
  }
}
