package me.deejack.jamc.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class World {
  public static final int BLOCK_DISTANCE = 4;
  private Environment environment;
  private ModelBatch batch;
  private Array<ModelInstance> instances;
  private List<Block> blocks;

  private ModelCache modelCache;

  private final TextureRegion[][] tiles;
  private final Texture fullTexture;

  public World(TextureRegion[][] tiles, Texture fullTexture) {
    this.tiles = tiles;
    this.fullTexture = fullTexture;
  }

  public void create() {
    batch = new ModelBatch();
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    blocks = new ArrayList<>();
    instances = new Array<>();

    modelCache = new ModelCache();

    for (int i = 0; i < 50; ++i) {
      for (int j = 0; j < 50; ++j) {
        Block newBlock = Blocks.GRASS.createBlock(i * BLOCK_DISTANCE, 0, j * BLOCK_DISTANCE, fullTexture, tiles);
        blocks.add(newBlock);
        instances.add(newBlock.getModel());
      }
    }

    updateCache();
  }

  public void render(Camera camera) {
    camera.update(); // Update the player's camera
    batch.begin(camera);
    /*for (var block : blocks) {
      if (block.isVisible(camera))
        batch.render(block.getModel());
    }*/
    batch.render(modelCache, environment);
    //batch.render(instances, environment);

    /*var pickRay = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    var end = new Vector3();
    pickRay.getEndPoint(end, 10).sub(0, 0, 2);
    DebugHud.INSTANCE.renderLine(camera, new Line(pickRay.origin, end));*/

    batch.end();
  }

  public void dispose() {
    batch.dispose();
    modelCache.dispose();
  }

  public void updateCache() {
    modelCache.dispose();
    modelCache.begin();
    modelCache.add(instances);
    modelCache.end();
  }

  public void placeBlock(Blocks block, Coordinates coordinates) {
    Block newBlock = block.createBlock(coordinates.x(), coordinates.y(), coordinates.z(), fullTexture, tiles);
    blocks.add(newBlock);
    instances.add(newBlock.getModel());
    updateCache();
  }

  public void destroyBlock(Block block) {
    instances.removeValue(block.getModel(), false);
    blocks.remove(block);
    updateCache();
  }

  public Set<Block> getNearBlocks(Vector3 position) {
    final int maxDistance = 15;
    var nearBlocks = new HashSet<>(blocks);
    nearBlocks.removeIf(block -> block.distanceFrom(position.x, position.y, position.z) > maxDistance);
    return nearBlocks;
  }

  public List<Block> getBlocks() {
    return blocks;
  }
}
