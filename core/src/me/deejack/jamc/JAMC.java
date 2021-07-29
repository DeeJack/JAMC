package me.deejack.jamc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import me.deejack.jamc.game.Hud;
import me.deejack.jamc.game.UserInterface;
import me.deejack.jamc.input.GameInputProcessor;
import me.deejack.jamc.input.PlayerMovementProcessor;
import me.deejack.jamc.input.UIInputProcessor;
import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.World;

public class JAMC implements ApplicationListener {
  private final float GAME_TIME_FACTOR = 2;
  private PlayerMovementProcessor movementProcessor;
  private World world;
  private Player currentPlayer;
  private Hud hud;

  @Override
  public void create() {
    var camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set the width,
    // height
    // and a FOV of 70
    camera.position.set(10f, 10f, 10f); // we set the position 10 pixels to the right, 10 up and 10 to the back (z
    // is positive towards the viewer)
    camera.lookAt(0f, 0f, 0f); // We look at the origin, where the object will be placed
    camera.near = 1f; // we set the near and far values
    camera.far = 300f;
    camera.update();
    currentPlayer = new Player(camera);

    world = new World(); // Create the world
    world.create();

    hud = new Hud(); // Initialize the hud (crosshair, fps counter etc.)
    hud.create();

    Gdx.input.setCursorCatched(true); // Hide the cursor
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

    // Face culling let openGL render only the faces the camera is seeing, not the
    // one behind!
    Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
    Gdx.gl20.glCullFace(GL20.GL_BACK);
    // By changing the front face it's possible to see that it's actually working,
    // as only the back would be loaded using the code in the line after this
    // Gdx.gl20.glFrontFace(GL20.GL_CW);

    // Add the input processors, first the UI, then the logic part (breaking/placing blocks), then the movement
    InputMultiplexer multipleInput = new InputMultiplexer();
    var uiInputProcessor = new UIInputProcessor(new UserInterface(hud));
    movementProcessor = new PlayerMovementProcessor(currentPlayer, world);

    multipleInput.addProcessor(uiInputProcessor);
    multipleInput.addProcessor(new GameInputProcessor(world, currentPlayer));
    multipleInput.addProcessor(movementProcessor);
    Gdx.input.setInputProcessor(multipleInput);
  }

  public void render() {
    float gameDeltaTime = Gdx.graphics.getDeltaTime() * GAME_TIME_FACTOR;
    movementProcessor.update(gameDeltaTime); // Process the pressed keys

    //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    ScreenUtils.clear(1, 0, 0, 1);

    world.render(currentPlayer.getCamera()); // render the world

    // Hud
    hud.render();
  }

  @Override
  public void dispose() {
    world.dispose();
    hud.dispose();
  }

  @Override
  public void resize(int width, int height) {
    currentPlayer.getCamera().viewportHeight = height;
    currentPlayer.getCamera().viewportWidth = width;
    currentPlayer.getCamera().update();
    hud.resize(width, height);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }
}
