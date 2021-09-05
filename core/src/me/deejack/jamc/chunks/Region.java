package me.deejack.jamc.chunks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.utils.WorldUtils;

public class Region {
  public final static int MAX_LOADED_CHUNKS = 32;
  private final Vector2 coordinates;
  private final SerializableChunk[] loadedChunks = new SerializableChunk[MAX_LOADED_CHUNKS * MAX_LOADED_CHUNKS];

  public Region(Vector2 coordinates) {
    this.coordinates = coordinates;

    var origin = coordinates.cpy().scl(32F);
    for (var row = 0; row < MAX_LOADED_CHUNKS; row++) {
      for (var column = 0; column < MAX_LOADED_CHUNKS; column++) {
        loadedChunks[column + row * MAX_LOADED_CHUNKS] = new SerializableChunk(new Vector3(origin.x + (row * WorldUtils.CHUNK_SIZE_X),
                0, origin.y + (column * WorldUtils.CHUNK_SIZE_Z)));
      }
    }
  }

  public Vector2 getCoordinates() {
    return coordinates;
  }

  public SerializableChunk getChunk(int index) {
    return loadedChunks[index];
  }

  public SerializableChunk[] getLoadedChunks() {
    return loadedChunks;
  }
}
