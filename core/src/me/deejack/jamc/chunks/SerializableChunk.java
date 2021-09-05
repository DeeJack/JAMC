package me.deejack.jamc.chunks;

import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.utils.ResizableArray;
import me.deejack.jamc.utils.WorldUtils;
import me.deejack.jamc.world.Blocks;

public class SerializableChunk {
  private final Vector3 coordinates;
  private ResizableArray<Blocks> blocks = new ResizableArray<>(WorldUtils.CHUNK_SIZE_X * WorldUtils.CHUNK_SIZE_Y * WorldUtils.CHUNK_SIZE_Z);
  private boolean generated = false;

  public SerializableChunk(Vector3 coordinates) {
    this.coordinates = coordinates;
  }

  public void setBlock(int index, Blocks block) {
    blocks.add(index, block);
  }

  public Blocks getBlock(int index) {
    return blocks.get(index);
  }

  public ResizableArray<Blocks> getBlocks() {
    return blocks;
  }

  public void setBlocks(ResizableArray<Blocks> blocks) {
    this.blocks = blocks;
    generated = true;
  }

  public Vector3 getCoordinates() {
    return coordinates;
  }

  public boolean isGenerated() {
    return generated;
  }

  @Override
  public String toString() {
    return "";
  }
}
