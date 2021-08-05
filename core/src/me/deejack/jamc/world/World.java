package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.rendering.WorldRenderableProvider;

import java.util.List;

public class World {
  public static final int BLOCK_DISTANCE = 4;
  private final TextureRegion[][] tiles;
  private final Texture fullTexture;
  private Environment environment;
  private ModelBatch batch;
  //private Array<ModelInstance> instances;
  //private List<Block> blocks;
  //private ModelCache modelCache;
  private WorldRenderableProvider testWorld;

  public World(TextureRegion[][] tiles, Texture fullTexture) {
    this.tiles = tiles;
    this.fullTexture = fullTexture;
  }

  public void create() {
    DefaultShader.Config config = new DefaultShader.Config();
    config.defaultCullFace = 0;

    //batch = new ModelBatch(Gdx.files.internal("shaders/vertex.glsl"), Gdx.files.internal("shaders/frag.glsl"));
    batch = new ModelBatch(new DefaultShaderProvider(config));
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    //blocks = new ArrayList<>();
    //instances = new Array<>();

    //modelCache = new ModelCache();

    /*for (int x = 0; x < 50; ++x) {
      for (int z = 0; z < 50; ++z) {
        for (int y = -10; y < 0; y++) {
          Block newBlock = Blocks.GRASS.createBlock(x * BLOCK_DISTANCE, y * BLOCK_DISTANCE, z * BLOCK_DISTANCE, fullTexture, tiles);
        }
      }
    }*/

    testWorld = new WorldRenderableProvider(tiles, fullTexture, 4);
    for (int x = 0; x < 15; x++) {
      for (int y = 0; y < 7; y++) {
        for (int z = 0; z < 15; z++) {
          var grass = Blocks.GRASS.createBlock(x, y, z, fullTexture, tiles);
          var dirt = Blocks.DIRT.createBlock(x, y, z, fullTexture, tiles);
          testWorld.placeBlock(x, y, z, y == 6 ? grass : dirt);
        }
      }
    }
    for (int x = 16; x < 32; x++) {
      for (int y = 0; y < 7; y++) {
        for (int z = 16; z < 32; z++) {
          var grass = Blocks.STONE.createBlock(x, y, z, fullTexture, tiles);
          testWorld.placeBlock(x, y, z, grass);
        }
      }
    }
    for (int x = 16; x < 32; x++) {
      for (int y = 0; y < 7; y++) {
        for (int z = 0; z < 16; z++) {
          var grass = Blocks.ASD.createBlock(x, y, z, fullTexture, tiles);
          testWorld.placeBlock(x, y, z, grass);
        }
      }
    }
    for (int x = 0; x < 16D; x++) {
      for (int y = 0; y < 7; y++) {
        for (int z = 16; z < 32; z++) {
          var grass = Blocks.OAK_WOOD_PLANK.createBlock(x, y, z, fullTexture, tiles);
          testWorld.placeBlock(x, y, z, grass);
        }
      }
    }

    var grass = Blocks.GRASS.createBlock(0, 0, 0, fullTexture, tiles);
    //testWorld.placeBlock(0, 0, 0, grass);
  }

  public void render(Camera camera) {
    camera.update(); // Update the player's camera
    batch.begin(camera);
    /*for (var block : blocks) {
      if (block.isVisible(camera))
        batch.render(block.getModel());
    }*/

    batch.render(testWorld, environment);
    //batch.render(modelCache, environment);

    //batch.render(instances, environment);

    /*var pickRay = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    var end = new Vector3();
    pickRay.getEndPoint(end, 10).sub(0, 0, 2);
    DebugHud.INSTANCE.renderLine(camera, new DebugHud.Line(pickRay.origin, end));*/


    batch.end();
  }

  public void dispose() {
    batch.dispose();
  }

  public void placeBlock(Blocks block, Vector3 coordinates) {
    Block newBlock = block.createBlock(coordinates.x, coordinates.y, coordinates.z, fullTexture, tiles);
    testWorld.placeBlock((int) coordinates.x, (int) coordinates.y, (int) coordinates.z, newBlock);
  }

  public void destroyBlock(Block block) {
    testWorld.destroyBlock((int) block.getCoordinates().x, (int) block.getCoordinates().y, (int) block.getCoordinates().z);
  }

  public List<Block> getNearBlocks(Vector3 position) {
    return testWorld.getNearBlocks(position);
  }
}
