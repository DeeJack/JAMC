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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.entities.Entity;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.rendering.WorldRenderableProvider;

import java.util.List;
import java.util.Random;

public class World {
  public static final int BLOCK_DISTANCE = 4;
  private final TextureRegion[][] tiles;
  private final Texture fullTexture;
  private final Player player;
  private final Array<Entity> entities = new Array<>();
  public boolean collision = false;
  private Environment environment;
  private ModelBatch batch;
  private WorldRenderableProvider testWorld;
  private int lastCollisionCheck = 0;

  public World(Player player, TextureRegion[][] tiles, Texture fullTexture) {
    this.player = player;
    entities.add(player);
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

    testWorld = new WorldRenderableProvider(player, 121);
    // TODO: fill chunk method
    testWorld.fillChunk(0, Blocks.GRASS);
    Random random = new Random();

    for (int i = 1; i < 121; i++) {
      Blocks blockType = Blocks.values()[random.nextInt(Blocks.values().length)];
      testWorld.fillChunk(i, blockType);
    }

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        var grass = Blocks.GRASS.createBlock(x, 6, z);
        testWorld.placeBlock(x, 6, z, grass);
      }
    }
  }

  public void render(Camera camera, float gameDeltaTime) {
    camera.update(); // Update the player's camera

    for (var entity : new Array.ArrayIterator<>(entities)) {
      if (entity instanceof Player p && (p.isFlying() || p.isJumping())) // If the entity is a player and he's flying
        continue;

      if (!collision) // If it's not colliding with a block, make it fall  TODO: the collision must be with a block below the player!
        entity.move(new Vector3(0, -10F * entity.getGravityCoefficient(), 0).scl(gameDeltaTime));

      if (lastCollisionCheck >= 5) { // Check the collision every 5 frames
        collision = checkCollision(entity);
        lastCollisionCheck = 0;
      }

      if (entity.getPosition().y < -10) {
        entity.setPosition(entity.getPosition().add(0, 100, 0));
      }
    }
    lastCollisionCheck++;

    batch.begin(camera); // Begin the drawing

    batch.render(testWorld, environment); // Draw the visible chunks

    batch.end(); // End the drawing
  }

  public boolean checkCollision(Vector3 position) {
    //var blocks = testWorld.getNearBlocks(position.cpy().scl(1 / 3F), 5); // Get the blocks in a range of 5 pixels (?)
    var blocks = testWorld.getNearBlocks(position.cpy().scl(1 / (float) World.BLOCK_DISTANCE), 5); // Get the blocks in a range of 5 pixels (?)
    if (JAMC.DEBUG)
      System.out.println("Blocks n°: " + blocks.size() + ", position : " + position);

    // Create the bounding box for the player and translate it to the current position of the player
    var playerBounds = new BoundingBox();
    playerBounds.set(new Vector3(-1.5F, -8, -1.5F), new Vector3(1.5F, 0, 1.5F)); // TODO: do this only one time on the creation of the player!
    playerBounds.mul(new Matrix4().setToTranslation(position));


    for (var block : blocks) {
      boolean result = playerBounds.intersects(block.getBoundingBox()); // Check if the player is colliding with a block

      if (result)
        return true;

    }
    return false;
  }

  public boolean checkCollision(Vector3 playerPosition, Vector3 targetPosition) {
    var playerBounds = new BoundingBox();
    playerBounds.set(new Vector3(-1.5F, -8, -1.5F), new Vector3(1.5F, 0, 1.5F)); // TODO: do this only one time on the creation of the player!
    playerBounds.mul(new Matrix4().setToTranslation(playerPosition));

    var blockBounds = new BoundingBox();
    blockBounds.set(new Vector3(-2, -2, 2), new Vector3(2, 2, -2));
    blockBounds.mul(new Matrix4().setToTranslation(targetPosition));

    return playerBounds.intersects(blockBounds);
  }


  public boolean checkCollision(Entity entity) {
    return checkCollision(entity.getPosition().scl(World.BLOCK_DISTANCE));
  }

  public void dispose() {
    batch.dispose();
  }

  /**
   * Place a block in the world
   *
   * @param block       The type of the block
   * @param coordinates The coordinates of the block (world coordinates)
   */
  public Block placeBlock(Blocks block, Vector3 coordinates) {
    Block newBlock = block.createBlock(coordinates.x, coordinates.y, coordinates.z);
    testWorld.placeBlock((int) coordinates.x, (int) coordinates.y, (int) coordinates.z, newBlock);
    return newBlock;
  }

  /**
   * Remove the block from the world (Only the coordinates in the block are important)
   *
   * @param block The block to remove
   */
  public void destroyBlock(Block block) {
    testWorld.destroyBlock((int) block.getCoordinates().x, (int) block.getCoordinates().y, (int) block.getCoordinates().z);
  }

  /**
   * Get the blocks near a certain position
   *
   * @param position The position (in world's coordinates)
   * @return The blocks near the position
   */
  public List<Block> getNearBlocks(Vector3 position) {
    return testWorld.getNearBlocks(position);
  }

  public Block getBlock(Vector3 coordinates) {
    return testWorld.getBlock((int) coordinates.x, (int) coordinates.y, (int) coordinates.z);
  }
}
