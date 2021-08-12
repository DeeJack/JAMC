package me.deejack.jamc.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.hud.utils.DebugHud;

public class Hud {
  private final InventoryHud inventoryHud;
  private OrthographicCamera hudCamera;
  private BitmapFont font;
  private SpriteBatch batch;
  private Texture crosshair;
  private InventoryBar inventoryBar;
  private Player currentPlayer;

  public Hud(InventoryHud inventoryHud) {
    this.inventoryHud = inventoryHud;
  }

  public void create(Player currentPlayer) {
    this.currentPlayer = currentPlayer;

    hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    hudCamera.position.set(Gdx.graphics.getWidth() / 2.F, Gdx.graphics.getHeight() / 2.F, 1.F);
    batch = new SpriteBatch();

    font = new BitmapFont();
    createCrosshair();
    inventoryBar = new InventoryBar(currentPlayer.getInventory());
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
    inventoryHud.render(batch);

    //if (JAMC.DEBUG) {
    for (var text : DebugHud.INSTANCE.getTextToRender()) {
      batch.setProjectionMatrix(hudCamera.combined);
      font.draw(batch, text.text(), text.x(), text.y());
    }
    // }

    DebugHud.INSTANCE.displayText(1, "Position: " + currentPlayer.getPosition() + "/" + currentPlayer.getCamera().position, 10, 10);

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

  public Camera getCamera() {
    return hudCamera;
  }

  public InventoryHud getInventoryHud() {
    return inventoryHud;
  }
}
