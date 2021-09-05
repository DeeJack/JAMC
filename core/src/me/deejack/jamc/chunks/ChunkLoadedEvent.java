package me.deejack.jamc.chunks;

import me.deejack.jamc.events.EventCollection;
import me.deejack.jamc.events.EventData;
import me.deejack.jamc.events.EventHandler;
import me.deejack.jamc.events.EventType;
import me.deejack.jamc.events.presets.PlayerEvent;
import me.deejack.jamc.rendering.WorldRenderableProvider;
import me.deejack.jamc.utils.WorldUtils;

import java.util.ArrayList;

public class ChunkLoadedEvent implements EventCollection {
  private final WorldRenderableProvider worldProvider;
  private final ChunkLoader chunkLoader;
  private int currentChunkIndex = -1;

  public ChunkLoadedEvent(WorldRenderableProvider worldProvider, ChunkLoader chunkLoader) {
    this.worldProvider = worldProvider;
    this.chunkLoader = chunkLoader;
  }

  @EventType(eventType = EventType.EventTypes.PLAYER_MOVE)
  public void onPlayerMove(PlayerEvent.PlayerEventData playerEvent) {
    int newIndex = worldProvider.getChunkIndex(playerEvent.getTargetPosition().x, playerEvent.getTargetPosition().z);
    if (currentChunkIndex != newIndex) {
      System.out.println("Changed chunk to " + newIndex);
      EventHandler.call(EventType.EventTypes.CHUNK_CHANGE, new ChunkEventData(newIndex));
      this.currentChunkIndex = newIndex;
    }
  }

  @EventType(eventType = EventType.EventTypes.CHUNK_CHANGE)
  public void onChunkChanged(ChunkEventData chunkEvent) {
    System.out.println("Current index: " + chunkEvent.chunkIndex);
    int chunksToLoad = 32;
    int chunkToLoad = chunkEvent.chunkIndex - (WorldUtils.CHUNKS_PER_ROW * (chunksToLoad - 2)) - (int) Math.floor(chunksToLoad / 2F); // Starting with the one on the left of the top one
    int chunksLoaded = 0;
    //var chunksToRender = new ArrayList<Chunk>();
    var chunksToRender2 = new ArrayList<Integer>();

    for (int row = chunksToLoad - 2; chunksLoaded < chunksToLoad * chunksToLoad; ) {
      if (chunkToLoad >= 0 && chunkToLoad < worldProvider.getChunks().length) {
        //chunksToRender.add(chunkLoader.getChunk());
        // TODO: The problem is that 'currentChunkIndex' is an index of the 16x16 array [WorldRenderableProvider], which is different from the index of the 32x32 array [ChunkLoader]
        chunksToRender2.add(chunkToLoad);
        chunksLoaded++;
      }
      if (chunkToLoad > worldProvider.getChunks().length)
        break;
      chunkToLoad++;
      if (chunkToLoad == chunkEvent.chunkIndex - (WorldUtils.CHUNKS_PER_ROW * row) + Math.ceil(chunksToLoad / 2F)) {
        chunkToLoad = chunkEvent.chunkIndex - (WorldUtils.CHUNKS_PER_ROW * (row - 1)) - (int) Math.floor(chunksToLoad / 2F);
        row--;
      }
    }
    System.out.println(chunksToRender2);

    for (Integer chunkIndex : chunksToRender2) {
      var chunk = chunkLoader.getChunk(chunkIndex);
      //var chunk = chunkLoader.getChunk((int) serializableChunk.getOffset().x, (int) serializableChunk.getOffset().z);

      if (chunk != null) {
        System.out.print("Transformed index " + chunkIndex + " to " + transformIndex32(chunkIndex));
        worldProvider.addChunk(transformIndex32(chunkIndex), chunk);
      }
    }
    /*
    for (var serializableChunk : chunksToRender) {
      var chunk = chunkLoader.getChunk((int) serializableChunk.getOffset().x, (int) serializableChunk.getOffset().z);
      //var chunk = new Chunk((int) serializableChunk.getCoordinates().x, (int) serializableChunk.getCoordinates().y, (int) serializableChunk.getCoordinates().z);
      //chunk.setBlocks(serializableChunk.getBlocks());
      if (chunk != null)
        worldProvider.addChunk(chunk);
    }*/
  }

  private int transformIndex32(int index32) {
    int row = (int) Math.floor(index32 / 32F) + 16;
    row = (int) Math.floor(row / 2F);
    int column = index32 % 32 + 16;
    column = (int) Math.floor(column / 2F);
    return column + (row * 16);
  }

  public static class ChunkEventData extends EventData {
    private final int chunkIndex;

    public ChunkEventData(int chunkIndex) {
      this.chunkIndex = chunkIndex;
    }

    public int getChunkIndex() {
      return chunkIndex;
    }
  }
}


