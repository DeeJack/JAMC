package me.deejack.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.time.Duration;
import java.util.Iterator;

public class Drop extends ApplicationAdapter {
  Vector3 touchPos = new Vector3(); // To avoid creating a lot of useless instances
  private Texture bucketTexture;
  private Texture waterDropTexture;
  private Sound dropSound;
  private Music rainMusic;
  private OrthographicCamera camera;
  private SpriteBatch batch;
  private Rectangle bucket;
  private Array<Rectangle> waterDrops; // A collection that doesn't create garbage like arraylist
  private long lastDropSpawnTime;

  @Override
  public void create() {
    bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
    waterDropTexture = new Texture(Gdx.files.internal("drop.png"));
    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.wav"));

    rainMusic.setLooping(true);
    rainMusic.play();

    batch = new SpriteBatch(); // The sprite batch is used to render 2d images
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    bucket = new Rectangle(camera.viewportWidth / 2 - 32, 20, 64, 64); // x & y: 20; width & heigth: 64

    waterDrops = new Array<>();
    spawnDrop();
  }

  @Override
  public void render() {
    ScreenUtils.clear(0, 0, 0.2f, 1);
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(bucketTexture, bucket.x, bucket.y);

    if (Gdx.input.isTouched() || Gdx.input.isCursorCatched()) {
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      bucket.x += 200 * Gdx.graphics.getDeltaTime();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      bucket.x -= 200 * Gdx.graphics.getDeltaTime();
    }
    if (bucket.x < 0)
      bucket.x = 0;
    if (bucket.x > camera.viewportWidth - bucket.width)
      bucket.x = camera.viewportWidth - bucket.width;
    if (TimeUtils.nanoTime() - lastDropSpawnTime > Duration.ofSeconds(1).toNanos())
      spawnDrop();
    for (Iterator<Rectangle> iter = waterDrops.iterator(); iter.hasNext(); ) {
      var drop = iter.next();
      batch.draw(waterDropTexture, drop.x, drop.y);
      drop.y -= 500 * Gdx.graphics.getDeltaTime();
      if (drop.y + 64 < 0)
        iter.remove();
      if (drop.overlaps(bucket)) {
        dropSound.play();
        iter.remove();
      }
    }
    batch.end();
  }

  private void spawnDrop() {
    Rectangle waterDrop = new Rectangle();
    waterDrop.x = MathUtils.random(camera.viewportWidth - 64);
    waterDrop.y = camera.viewportHeight - 64;
    waterDrop.height = 64;
    waterDrop.width = 64;
    waterDrops.add(waterDrop);
    lastDropSpawnTime = TimeUtils.nanoTime();
  }

  @Override
  public void dispose() {
    waterDropTexture.dispose();
    bucketTexture.dispose();
    batch.dispose();
    dropSound.dispose();
    rainMusic.dispose();
  }
}
