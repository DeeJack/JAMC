package me.deejack.jamc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import me.deejack.jamc.game.UserInterface;
import me.deejack.jamc.input.GameInputProcessor;
import me.deejack.jamc.input.PlayerMovementProcessor;
import me.deejack.jamc.input.UIInputProcessor;
import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.World;

public class JAMC implements ApplicationListener {
	private final float GAME_FACTOR = 2;
	private final UserInterface userInterface = new UserInterface();
	private PlayerMovementProcessor movementProcessor;
	private UIInputProcessor uiInputProcessor;
	private World world;
	private Player currentPlayer;
	private OrthographicCamera hudCamera;
	private BitmapFont font;
	private SpriteBatch batch;

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

		hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// hudCamera.setToOrtho(false, Gdx.graphics.getWidth(),
		hudCamera.position.set(Gdx.graphics.getWidth() / 2.F, Gdx.graphics.getHeight() / 2.F, 1.F);
		batch = new SpriteBatch();

		world = new World();
		world.create();

		font = new BitmapFont();

		Gdx.input.setCursorCatched(true);
		// Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
		Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

		// Face culling let openGL render only the faces the camera is seeing, not the
		// one behind!
		Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl20.glCullFace(GL20.GL_BACK);
		// By changing the front face it's possible to see that it's actually working,
		// as only the back would be loaded using the code in the line after this
		// Gdx.gl20.glFrontFace(GL20.GL_CW);
		InputMultiplexer multipleInput = new InputMultiplexer();
		uiInputProcessor = new UIInputProcessor(userInterface);
		movementProcessor = new PlayerMovementProcessor(currentPlayer, world);

		multipleInput.addProcessor(uiInputProcessor);
		multipleInput.addProcessor(new GameInputProcessor(world, currentPlayer));
		multipleInput.addProcessor(movementProcessor);
		Gdx.input.setInputProcessor(multipleInput);
	}

	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		float gameDeltaTime = deltaTime * GAME_FACTOR;
		//if (gameDeltaTime > 0.033f)
		movementProcessor.update(gameDeltaTime);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		ScreenUtils.clear(1, 0, 0, 1);

		currentPlayer.getCamera().update();
		world.render(currentPlayer.getCamera());

		hudCamera.update();
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, hudCamera.viewportHeight);
		batch.end();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
