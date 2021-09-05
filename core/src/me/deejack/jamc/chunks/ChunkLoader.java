package me.deejack.jamc.chunks;

import com.badlogic.gdx.math.Vector2;
import me.deejack.jamc.rendering.Chunk;
import me.deejack.jamc.utils.WorldUtils;

public class ChunkLoader {
  private final static int MAX_REGIONS_LOADED = 1;
  private final Region[] regions = new Region[MAX_REGIONS_LOADED];

  public ChunkLoader() {
    regions[0] = new Region(new Vector2(0, 0));
  }

  public Chunk getChunk(int x, int z) {
    var chunkIndex = WorldUtils.getChunkIndex(x, z);
    return getChunk(chunkIndex);
  }

  public Chunk getChunk(int chunkIndex) {
    if (chunkIndex < 0 || chunkIndex >= (Region.MAX_LOADED_CHUNKS * Region.MAX_LOADED_CHUNKS))
      return null;
    var serializableChunk = regions[0].getChunk(chunkIndex);
    var chunk = new Chunk((int) serializableChunk.getCoordinates().x, 0, (int) serializableChunk.getCoordinates().z);
    chunk.setBlocks(serializableChunk.getBlocks());
    if (!serializableChunk.isGenerated()) {
      System.out.println("Chunk generated (" + chunkIndex + ")!");
      WorldUtils.generateChunk(chunk);
      serializableChunk.setBlocks(chunk.getBlocks());
    }
    return chunk;
  }
}
