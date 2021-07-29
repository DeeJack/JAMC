package me.deejack.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
  private final DropGame game;
  private OrthographicCamera camera;

  public MainMenuScreen(DropGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0f, 0f, 0.2f, 1f);

    camera.update();

    var batch = game.getBatch();
    batch.begin();

    game.getFont().draw(batch, "Welcome to Drop", 100, 150);
    game.getFont().draw(batch, "Click to start", 100, 250);

    batch.end();

    if (Gdx.input.isTouched() || Gdx.input.isCursorCatched()) {
      game.setScreen(new GameScreen(game));
      dispose();
    }
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
  }

}
