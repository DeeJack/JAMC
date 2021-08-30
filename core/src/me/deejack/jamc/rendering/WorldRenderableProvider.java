package me.deejack.jamc.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.textures.TextureCache;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class WorldRenderableProvider implements RenderableProvider {
  private final static int CHUNK_SIZE_X = 16;
  private final static int CHUNK_SIZE_Y = 128;
  private final static int CHUNK_SIZE_Z = 16;
  public static int CHUNKS_TO_RENDER = 9;
  /**
   * The chunks currently in memory
   */
  private final Chunk[] chunks;

  /**
   * The meshes, one per chunk
   */
  private final Mesh[] meshes;

  private final Material material;

  private final Player player;

  /**
   * Whether the vertices of the chunks needs to be calculated again
   */
  private final boolean[] dirty;
  private final int[] numOfIndices;
  private final int chunksPerRow;
  private int lastRenderedChunks = 0;

  /**
   * Create the world
   *
   * @param chunksCount The number of chunks
   */
  public WorldRenderableProvider(Player player, int chunksCount) {
    this.player = player;
    var chunksCountPerRow = Math.sqrt(chunksCount); // Chunks on X and Z
    if (chunksCountPerRow != (int) chunksCountPerRow)
      throw new IllegalArgumentException("The number of chunks is not power of 2");

    this.chunks = new Chunk[chunksCount];
    this.chunksPerRow = (int) chunksCountPerRow;

    int currentChunk = 0;

    for (int z = -MathUtils.floor(chunksPerRow / 2F); z < MathUtils.ceil(chunksPerRow / 2F); z++) {
      for (int x = -MathUtils.floor(chunksPerRow / 2F); x < MathUtils.ceil(chunksPerRow / 2F); x++) {
        System.out.println("Origin: " + x * CHUNK_SIZE_X + ", " + z * CHUNK_SIZE_Z);
        this.chunks[currentChunk] = new Chunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, x * CHUNK_SIZE_X, 0, z * CHUNK_SIZE_Z);
        currentChunk++;
      }
    }

    // Chunk.VERTEXES_PER_FACE because only 4 vertices are required, but 2 of them needs to be used 2 times
    /**
     * The indices to draw the faces of the cubes in the chunks
     */
    short[] indices = new short[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * (Chunk.VERTEXES_PER_FACE + 2) * Chunk.CUBE_FACES];
    int currentOffset = 0;
    for (int i = 0; i < indices.length; currentOffset += 4) {//i += 6,
      indices[i++] = (short) currentOffset; // Top left corner
      indices[i++] = (short) (currentOffset + 1); // Bottom left corner
      indices[i++] = (short) (currentOffset + 2); // Bottom right corner

      indices[i++] = (short) (currentOffset + 2); // Bottom right corner
      indices[i++] = (short) (currentOffset + 3); // Top right corner
      indices[i++] = (short) (currentOffset + 0); // Top left corner
    }

    this.meshes = new Mesh[chunksCount];
    for (int i = 0; i < meshes.length; i++) {
      meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * Chunk.VERTEXES_PER_FACE * Chunk.CUBE_FACES, indices.length,
              VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
      meshes[i].setIndices(indices);
    }

    //this.materials = new Material[chunksCount];
    //for (int chunk = 0; chunk < chunks.length; chunk++) {
    //materials[chunk] = new Material(new TextureAttribute(TextureAttribute.Diffuse, TextureCache.getFullTexture()));
      /*materials[i] = new Material(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(0.5f, 1f), MathUtils.random(
              0.5f, 1f), MathUtils.random(0.5f, 1f), 1));*/
    //}
    this.material = new Material(new TextureAttribute(TextureAttribute.Diffuse, TextureCache.getFullTexture()));

    this.dirty = new boolean[chunksCount];
    Arrays.fill(dirty, true);

    this.numOfIndices = new int[chunksCount];
    Arrays.fill(numOfIndices, (short) 0);
  }

  public int getChunkIndex(float x, float z) {
    var minAddingValueX = 0.0001F;
    var minAddingValueZ = 0.0001F;
    // This whole formula was made with more like a trial-error procedure, so I can't really tell what it does, but it works.
    int rowIndex = (int) Math.ceil((z + minAddingValueZ) / CHUNK_SIZE_Z) + (int) Math.floor(chunksPerRow / 2F) - 1;
    rowIndex %= 16;
    int colIndex = (int) (Math.ceil((x + minAddingValueX) / CHUNK_SIZE_X) + (int) Math.floor(chunksPerRow / 2F) - 1);
    colIndex %= 16;
    return (rowIndex * chunksPerRow) + colIndex;
  }

  private Vector2 getChunkCoordinates(float x, float z) {
    var minAddingValueX = 0.0001F;
    var minAddingValueZ = 0.0001F;
    // This whole formula was made with more like a trial-error procedure, so I can't really tell what it does, but it works.
    int rowIndex = (int) Math.ceil((z + minAddingValueZ) / CHUNK_SIZE_Z) + (int) Math.floor(chunksPerRow / 2F) - 1;
    int colIndex = (int) (Math.ceil((x + minAddingValueX) / CHUNK_SIZE_X) + (int) Math.floor(chunksPerRow / 2F) - 1);
    return new Vector2(rowIndex * CHUNK_SIZE_X, colIndex * CHUNK_SIZE_Z);
  }

  public void placeBlock(int x, int y, int z, Block block) {
    int chunkIndex = getChunkIndex(x, z);
    // I have to flip the axis if they are negative because... Otherwise it doesn't work
    if (x < 0)
      x = CHUNK_SIZE_X - (-x % CHUNK_SIZE_X);
    if (z < 0)
      z = CHUNK_SIZE_Z - (-z % CHUNK_SIZE_Z);
    chunks[chunkIndex].set(Math.abs(x % CHUNK_SIZE_X), y, Math.abs(z % CHUNK_SIZE_Z), block);
    dirty[chunkIndex] = true;
  }

  public void destroyBlock(int x, int y, int z) {
    placeBlock(x, y, z, null);
  }

  public List<Block> getNearBlocks(Vector3 position) {
    return getNearBlocks(position, 5);
  }

  public List<Block> getNearBlocks(Vector3 position, int distance) {
    int chunkIndex = getChunkIndex(position.x, position.z);
    if (chunkIndex < 0 || chunkIndex >= chunks.length)
      return new ArrayList<>();

    var chunksToCheck = new ArrayList<Chunk>();
    boolean rightLoaded = false;
    boolean topLoaded = false;
    if ((position.x >= 0 && position.x % CHUNK_SIZE_X < CHUNK_SIZE_X / 2F) || (position.x < 0 && Math.abs(position.x % CHUNK_SIZE_X) >= CHUNK_SIZE_X / 2F)) { // Load the left one
      if (chunkIndex - 1 >= 0)
        chunksToCheck.add(chunks[chunkIndex - 1]);
    } else { // Load the right one
      rightLoaded = true;
      if (chunkIndex + 1 < chunks.length)
        chunksToCheck.add(chunks[chunkIndex + 1]);
    }
    // Load the bottom one
    if ((position.z >= 0 && position.z % CHUNK_SIZE_Z < CHUNK_SIZE_Z / 2F) ||
            (position.z < 0 && Math.abs(position.z % CHUNK_SIZE_Z) >= CHUNK_SIZE_X / 2F)) {
      if (chunkIndex - chunksPerRow >= 0)
        chunksToCheck.add(chunks[chunkIndex - chunksPerRow]);
    } else { // Load the top one
      topLoaded = true;
      if (chunkIndex + chunksPerRow < chunks.length)
        chunksToCheck.add(chunks[chunkIndex + chunksPerRow]);
    }
    if (topLoaded && rightLoaded) { // Load also the chunk between the top and the right one
      if (chunkIndex + chunksPerRow + 1 < chunks.length)
        chunksToCheck.add(chunks[chunkIndex + chunksPerRow + 1]);
    } else if (topLoaded) { // The left one on top
      if (chunkIndex + chunksPerRow - 1 < chunks.length)
        chunksToCheck.add(chunks[chunkIndex + chunksPerRow - 1]);
    } else if (rightLoaded) { // Bottom right
      if (chunkIndex - chunksPerRow + 1 >= 0)
        chunksToCheck.add(chunks[chunkIndex - chunksPerRow + 1]);
    } else { // Bottom left
      if (chunkIndex - chunksPerRow - 1 >= 0)
        chunksToCheck.add(chunks[chunkIndex - chunksPerRow - 1]);
    }
    final int maxDistance = distance;
    var nearBlocks = new ArrayList<>(chunks[chunkIndex].getRenderedBlocks());
    for (var chunk : chunksToCheck)
      nearBlocks.addAll(chunk.getRenderedBlocks());

    return nearBlocks.stream().filter(Objects::nonNull)
            .filter(block -> block.distanceFrom(position.x, position.y, position.z) < maxDistance)
            .sorted(Comparator.comparingDouble(block -> block.distanceFrom(position.x, position.y, position.z)))
            .collect(Collectors.toList());
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    lastRenderedChunks = 0;
    // TODO: load chunks from the current chunk to the others
    /*int currentChunkIndex = getChunkIndex(player.getPosition().x, player.getPosition().z);
    var chunksToRender = new ArrayList<Integer>();

    int chunkToLoad = currentChunkIndex - (chunksPerRow * (CHUNKS_TO_RENDER - 2)) - (int) Math.floor(CHUNKS_TO_RENDER / 2F); // Starting with the one on the left of the top one
    int chunksLoaded = 0;
    for (int row = CHUNKS_TO_RENDER - 2; chunksLoaded < CHUNKS_TO_RENDER * CHUNKS_TO_RENDER; chunksLoaded++) {
      if (chunkToLoad >= 0 && chunkToLoad < chunks.length) {
        chunksToRender.add(chunkToLoad);
      }
      chunkToLoad++;
      if (chunkToLoad == currentChunkIndex - (chunksPerRow * row) + Math.ceil(CHUNKS_TO_RENDER / 2F)) {
        chunkToLoad = currentChunkIndex - (chunksPerRow * (row - 1)) - (int) Math.floor(CHUNKS_TO_RENDER / 2F);
        row--;
      }
    }

    for (var chunkIndex : chunksToRender) {
      var chunk = chunks[chunkIndex];
      var mesh = meshes[chunkIndex];

      if (dirty[chunkIndex]) { // If the vertices needs to be updated
        var numVertices = chunk.calculateVertices();
        //numVertices = (numVertices / 4 * 6); // Num of indices
        numOfIndices[chunkIndex] = numVertices / 4 * 6;
        mesh.setVertices(chunk.getVertexes(), 0, numVertices * 6); // ???
        dirty[chunkIndex] = false;
      }

      Renderable renderable = pool.obtain();
      renderable.material = material;
      renderable.meshPart.mesh = mesh;
      renderable.meshPart.offset = 0;
      renderable.meshPart.size = numOfIndices[chunkIndex];
      renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
      renderables.add(renderable);
      lastRenderedChunks++;
    }*/

    for (int i = 0; i < chunks.length; i++) {
      var chunk = chunks[i];
      var mesh = meshes[i];

      if (dirty[i]) { // If the vertices needs to be updated
        var numVertices = chunk.calculateVertices();
        //numVertices = (numVertices / 4 * 6); // Num of indices
        numOfIndices[i] = numVertices / 4 * 6;
        mesh.setVertices(chunk.getVertexes(), 0, numVertices * 6); // ???
        dirty[i] = false;
      }

      Renderable renderable = pool.obtain();
      renderable.material = material;
      renderable.meshPart.mesh = mesh;
      renderable.meshPart.offset = 0;
      renderable.meshPart.size = numOfIndices[i];
      renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
      renderables.add(renderable);
      lastRenderedChunks++;
    }

    if (JAMC.DEBUG)
      System.out.println("Render " + lastRenderedChunks + " chunks");
  }

  public Chunk[] getChunks() {
    return chunks;
  }

  public Chunk getChunk(int x, int y, int z) {
    int chunkIndex = getChunkIndex(x, z);
    return chunks[chunkIndex];
  }

  public Block getBlock(int x, int y, int z) {
    int chunkIndex = getChunkIndex(x, z);
    // I have to flip the axis if they are negative because... Otherwise it doesn't work
    if (x < 0)
      x = CHUNK_SIZE_X - (-x % CHUNK_SIZE_X);
    if (z < 0)
      z = CHUNK_SIZE_Z - (-z % CHUNK_SIZE_Z);
    return chunks[chunkIndex].get(x % CHUNK_SIZE_X, y, z % CHUNK_SIZE_Z);
  }

  public void fillChunk(int chunkIndex, Blocks blockType) {
    var chunk = chunks[chunkIndex];
    for (int x = 0; x < CHUNK_SIZE_X; x++) {
      for (int z = 0; z < CHUNK_SIZE_Z; z++) {
        for (int y = 0; y < 6; y++) {
          chunk.set(x, y, z, blockType.createBlock(x + chunk.getOffset().x / World.BLOCK_DISTANCE, y,
                  z + chunk.getOffset().z / World.BLOCK_DISTANCE));
        }
      }
    }
  }

  public void addChunk(Chunk newChunk) {
    var chunkIndex = getChunkIndex(newChunk.getOffset().x, newChunk.getOffset().z);
    chunks[chunkIndex] = newChunk;
    dirty[chunkIndex] = true;
  }
}
