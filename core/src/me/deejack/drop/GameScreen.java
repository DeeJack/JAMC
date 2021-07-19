package me.deejack.drop;

import java.time.Duration;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

public class GameScreen implements Screen {
    private final DropGame game;
    private int points = 0;

    private Texture bucketTexture;
    private Texture waterDropTexture;
    private Sound dropSound;
    private Music rainMusic;

    private OrthographicCamera camera;

    private Rectangle bucket;
    private Array<Rectangle> waterDrops; // A collection that doesn't create garbage like arraylist
    private long lastDropSpawnTime;

    public GameScreen(DropGame game) {
        this.game = game;

        bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
        waterDropTexture = new Texture(Gdx.files.internal("drop.png"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.wav"));

        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        bucket = new Rectangle(camera.viewportWidth / 2 - 32, 20, 64, 64); // x & y: 20; width & heigth: 64

        waterDrops = new Array<>();
        spawnDrop();
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    Vector3 touchPos = new Vector3(); // To avoid creating a lot of useless instances
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getFont().draw(game.getBatch(), "Points: " + points, 10, 450);
        game.getBatch().draw(bucketTexture, bucket.x, bucket.y);

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
        for (Iterator<Rectangle> iter = waterDrops.iterator(); iter.hasNext();) {
            var drop = iter.next();
            game.getBatch().draw(waterDropTexture, drop.x, drop.y);
            drop.y -= 500 * Gdx.graphics.getDeltaTime();
            if (drop.y + 64 < 0)
                iter.remove();
            if (drop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
                points++;
            }
        }
        game.getBatch().end();
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
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        
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
        waterDropTexture.dispose();
        bucketTexture.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        game.dispose();
    }

}
