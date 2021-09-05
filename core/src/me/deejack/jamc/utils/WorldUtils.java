package me.deejack.jamc.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.rendering.Chunk;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.World;

import java.util.Random;

public final class WorldUtils {
  public static final int CHUNK_SIZE_X = 16;
  public static final int CHUNK_SIZE_Y = 128;
  public static final int CHUNK_SIZE_Z = 16;
  public static final int CHUNKS_PER_ROW = 32;

  private final static Random random = new Random();

  private WorldUtils() {
    throw new AssertionError("Uninstantiable");
  }

  public static Vector3 toBlockCoordinates(Vector3 worldCoordinates) {
    return worldCoordinates.cpy().scl(1F / World.BLOCK_DISTANCE);
  }

  public static Vector3 toWorldCoordinates(Vector3 blockCoordinates) {
    return blockCoordinates.cpy().scl(World.BLOCK_DISTANCE);
  }

  public static Vector2 toBlockCoordinates(Vector2 worldCoordinates) {
    return worldCoordinates.cpy().scl(1F / World.BLOCK_DISTANCE);
  }

  public static Vector2 toWorldCoordinates(Vector2 blockCoordinates) {
    return blockCoordinates.cpy().scl(World.BLOCK_DISTANCE);
  }

  public static int getChunkIndex(float x, float z) {
    var minAddingValueX = 0.0001F;
    var minAddingValueZ = 0.0001F;
    // This whole formula was made with more like a trial-error procedure, so I can't really tell what it does, but it works.
    int rowIndex = (int) Math.ceil((z + minAddingValueZ) / CHUNK_SIZE_Z) + (int) Math.floor(CHUNKS_PER_ROW / 2F) - 1;
    rowIndex %= 16;
    int colIndex = (int) (Math.ceil((x + minAddingValueX) / CHUNK_SIZE_X) + (int) Math.floor(CHUNKS_PER_ROW / 2F) - 1);
    colIndex %= 16;
    return (rowIndex * CHUNKS_PER_ROW) + colIndex;
  }

  public static void generateChunk(Chunk chunk) {
    Blocks blockType = Blocks.values()[random.nextInt(Blocks.values().length)];
    for (int x = 0; x < CHUNK_SIZE_X; x++) {
      for (int z = 0; z < CHUNK_SIZE_Z; z++) {
        for (int y = 0; y < 6; y++) {
          chunk.set(x, y, z, blockType);
          //.createBlock(x + chunk.getOffset().x / World.BLOCK_DISTANCE, y,
          //       z + chunk.getOffset().z / World.BLOCK_DISTANCE));
        }
      }
    }
  }
}
