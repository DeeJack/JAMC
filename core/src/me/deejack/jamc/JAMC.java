package me.deejack.jamc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.ScreenUtils;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.hud.Hud;
import me.deejack.jamc.hud.InventoryHud;
import me.deejack.jamc.hud.UserInterface;
import me.deejack.jamc.hud.settings.SettingsPage;
import me.deejack.jamc.hud.utils.DebugHud;
import me.deejack.jamc.input.*;
import me.deejack.jamc.items.Items;
import me.deejack.jamc.world.World;

public class JAMC implements ApplicationListener {
  public final static float GAME_TIME_FACTOR = 2;
  public static float MASTER_VOLUME = 0;
  public static boolean DEBUG = false;
  private PlayerMovementProcessor movementProcessor;
  private World world;
  private Player currentPlayer;
  private Hud hud;
  private GLProfiler profiler;
  private SettingsPage mainSettingsPage;
  private Music music;

  @Override
  public void create() {
    var camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set the width, height and a FOV of 70
    camera.position.set(32f, 65f, 32f); // we set the position 10 pixels to the right, 10 up and 10 to the back (z
    // is positive towards the viewer)
    //camera.lookAt(0f, 0f, 0f); // We look at the origin, where the object will be placed
    camera.near = 1f; // we set the near and far values
    camera.near = 0.5f;
    camera.far = 300f;
    camera.update();
    currentPlayer = new Player(camera);

    // TODO: remove
    currentPlayer.getInventory().addItem(Items.GRASS.createItem(), 0);
    currentPlayer.getInventory().addItem(Items.ASD.createItem(), 1);
    currentPlayer.getInventory().addItem(Items.STONE.createItem(), 2);
    currentPlayer.getInventory().addItem(Items.GRASS.createItem(), 5);

    var index = currentPlayer.getInventory().getSlots() - 1;
    for (var itemType : Items.values()) {
      currentPlayer.getInventory().addItem(itemType.createItem(), index--);
    }

    // TODO: custom name for the world
    world = new World("asd", currentPlayer); // Create the world
    world.create();

    hud = new Hud(new InventoryHud(currentPlayer.getInventory())); // Initialize the hud (crosshair, fps counter etc.)
    hud.create(currentPlayer);

    Gdx.input.setCursorCatched(true); // Hide the cursor
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

    // Face culling let openGL render only the faces the camera is seeing, not the
    // one behind!
    //Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
    //Gdx.gl20.glCullFace(GL20.GL_BACK);
    //Gdx.gl20.glDisable(GL20.GL_CULL_FACE);

    Gdx.gl20.glEnable(Gdx.gl20.GL_CULL_FACE);
    Gdx.gl20.glCullFace(Gdx.gl20.GL_BACK);
    // By changing the front face it's possible to see that it's actually working,
    // as only the back would be loaded using the code in the line after this
    // Gdx.gl20.glFrontFace(GL20.GL_CW);

    // Add the input processors, first the UI, then the logic part (breaking/placing blocks), then the movement
    // music = Gdx.app.getAudio().newMusic(Gdx.files.internal("music/music.mp3"));
    InputMultiplexer multipleInput = new InputMultiplexer();

    mainSettingsPage = new SettingsPage(currentPlayer, music);

    var uiInputProcessor = new UIInputProcessor(new UserInterface(hud, mainSettingsPage), currentPlayer);
    movementProcessor = new PlayerMovementProcessor(currentPlayer, world);

    if (DEBUG)
      multipleInput.addProcessor(new DebugInputProcessor(hud.getCamera()));

    multipleInput.addProcessor(mainSettingsPage.getStage());
    multipleInput.addProcessor(uiInputProcessor);
    multipleInput.addProcessor(new EventInputProcessor());
    multipleInput.addProcessor(new GameInputProcessor(world, currentPlayer));
    multipleInput.addProcessor(movementProcessor);
    Gdx.input.setInputProcessor(multipleInput);

    profiler = new GLProfiler(Gdx.graphics);
    profiler.enable();

    //EventHandler.registerEvent(new TestEvent());
    // music.setVolume(MASTER_VOLUME / 100F);
    // music.play();
  }

  public void render() {
    profiler.reset();

    float gameDeltaTime = Gdx.graphics.getDeltaTime() * GAME_TIME_FACTOR;

    movementProcessor.update(gameDeltaTime); // Process the pressed keys

    //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    ScreenUtils.clear(1, 0, 0, 1);

    world.render(currentPlayer.getCamera(), gameDeltaTime); // render the world

    var pickRay = currentPlayer.getCamera().getPickRay(currentPlayer.getCamera().viewportWidth / 2F, currentPlayer.getCamera().viewportHeight / 2F);
    //var end = new Vector3();
    //pickRay.getEndPoint(end, 6).add(0, 0, 1);
    // var ray = new Ray(currentPlayer.getPosition().add(0, 0, 1), currentPlayer.getCamera().direction.cpy().add(0, 0, 1));
    //DebugHud.INSTANCE.renderLine(currentPlayer.getCamera(), new DebugHud.Line(pickRay.origin, end));
    DebugHud.INSTANCE.renderLine(currentPlayer.getCamera(), new DebugHud.Line(currentPlayer.getPosition().cpy().add(0, 0, 0.01F),
            pickRay.origin.cpy().add(pickRay.direction.cpy().scl(15)).add(0, 0, 0.01F)));
//DebugHud.INSTANCE.renderLine(currentPlayer.getCamera(), new DebugHud.Line(currentPlayer.getPosition().add(0, 0, 1), currentPlayer.getPosition().add(currentPlayer.getCamera().direction.cpy().add(0, 0, 1))));

    if (mainSettingsPage.isOpened())
      mainSettingsPage.render();

    // Hud
    hud.render();


    var drawCalls = profiler.getDrawCalls();
    var textureBinds = profiler.getTextureBindings();
    var shadersSwitches = profiler.getShaderSwitches();
    var glCalls = profiler.getCalls();
    var vertexes = profiler.getVertexCount();
    var fps = Gdx.graphics.getFramesPerSecond();
    if (DEBUG)
      System.out.println("Draw calls: " + drawCalls + ", textureBinds: " + textureBinds + ", fps: " + fps + ", shadersSwitches: " + shadersSwitches + ", glCalls: " + glCalls + ", vertexCount: " + vertexes.total);
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
