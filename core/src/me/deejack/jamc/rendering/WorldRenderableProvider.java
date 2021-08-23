package me.deejack.jamc.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class WorldRenderableProvider implements RenderableProvider {
  private final static int CHUNK_SIZE_X = 16;
  private final static int CHUNK_SIZE_Y = 256;
  private final static int CHUNK_SIZE_Z = 16;
  public static boolean SHOW_BOUNDING_BOXES = false;
  /**
   * The chunks currently in memory
   */
  private final Chunk[] chunks;

  /**
   * The indices to draw the faces of the cubes in the chunks
   */
  private final short[] indices;

  /**
   * The meshes, one per chunk
   */
  private final Mesh[] meshes;

  /**
   * Only for testing, I need to use the textures
   */
  private final Material[] materials;

  /**
   * Whether the vertices of the chunks needs to be calculated again
   */
  private final boolean[] dirty;
  private final int[] numOfIndices;
  private final int chunksPerRow;
  private final int chunkOffset = 0;
  private int lastRenderedChunks = 0;

  /**
   * Create the world
   *
   * @param tiles       The textures to be used
   * @param chunksCount The number of chunks
   */
  public WorldRenderableProvider(TextureRegion[][] tiles, Texture fullTexture, int chunksCount) {
    var chunksCountPerRow = Math.sqrt(chunksCount); // Chunks on X and Z
    if (chunksCountPerRow != (int) chunksCountPerRow)
      throw new IllegalArgumentException("Chunk's num not power of 2");

    this.chunks = new Chunk[chunksCount];
    this.chunksPerRow = (int) chunksCountPerRow;

    int currentChunk = 0;

    for (int z = -MathUtils.floor(chunksPerRow / 2F); z < MathUtils.ceil(chunksPerRow / 2F); z++) {
      for (int x = -MathUtils.floor(chunksPerRow / 2F); x < MathUtils.ceil(chunksPerRow / 2F); x++) {
        System.out.println("Origin: " + x * CHUNK_SIZE_X + ", " + z * CHUNK_SIZE_Z);
        this.chunks[currentChunk++] = new Chunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, x * CHUNK_SIZE_X, 0, z * CHUNK_SIZE_Z);
      }
    }

    // Chunk.VERTEXES_PER_FACE because only 4 vertices are required, but 2 of them needs to be used 2 times
    this.indices = new short[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * (Chunk.VERTEXES_PER_FACE + 2) * Chunk.CUBE_FACES];
    int currentOffset = 0;
    for (int i = 0; i < indices.length; i += 6, currentOffset += 4) {
      indices[i] = (short) currentOffset; // Top left corner
      indices[i + 1] = (short) (currentOffset + 1); // Bottom left corner
      indices[i + 2] = (short) (currentOffset + 2); // Bottom right corner
      indices[i + 3] = (short) (currentOffset + 2); // Bottom right corner
      indices[i + 4] = (short) (currentOffset + 3); // Top right corner
      indices[i + 5] = (short) (currentOffset); // Top left corner
    }

    this.meshes = new Mesh[chunksCount];
    for (int i = 0; i < meshes.length; i++) {
      meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * Chunk.VERTEXES_PER_FACE * Chunk.CUBE_FACES, indices.length,
              VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
      meshes[i].setIndices(indices);
    }

    this.materials = new Material[chunksCount];
    for (int chunk = 0; chunk < chunks.length; chunk++) {
      //for (int block = 0; block < this.chunks[chunk].getBlocks().length; block++) {
      materials[chunk] = new Material(new TextureAttribute(TextureAttribute.Diffuse, fullTexture));
      //}
      /*materials[i] = new Material(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(0.5f, 1f), MathUtils.random(
              0.5f, 1f), MathUtils.random(0.5f, 1f), 1));*/
    }

    this.dirty = new boolean[chunksCount];
    Arrays.fill(dirty, true);

    this.numOfIndices = new int[chunksCount];
    Arrays.fill(numOfIndices, (short) 0);
  }

  private int getChunkIndex(float x, float z) {
    // +0.001F because otherwise the (0, 0) [x, y] point would be in the wrong chunk, it shouldn't interfere with the other points
    return (MathUtils.ceil(((x + 0.001F) / CHUNK_SIZE_X) +
            MathUtils.floor(z / CHUNK_SIZE_Z) * chunksPerRow)) + chunksPerRow;
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
    System.out.println(chunkIndex);
    if (chunkIndex < 0 || chunkIndex >= chunks.length)
      return new ArrayList<>();

    var chunksToCheck = new ArrayList<Chunk>();
    boolean rightLoaded = false;
    boolean topLoaded = false;
    System.out.println(position);
    if ((position.x >= 0 && position.x % CHUNK_SIZE_X < CHUNK_SIZE_X / 2F) || (position.x < 0 && Math.abs(position.x % CHUNK_SIZE_X) >= CHUNK_SIZE_X / 2F)) { // Load the left one
      System.out.println("Left loaded");
      if (chunkIndex - 1 >= 0)
        chunksToCheck.add(chunks[chunkIndex - 1]);
    } else { // Load the right one
      System.out.println("Right loaded");
      rightLoaded = true;
      if (chunkIndex + 1 < chunks.length)
        chunksToCheck.add(chunks[chunkIndex + 1]);
    }
    if ((position.z >= 0 && position.z % CHUNK_SIZE_Z < CHUNK_SIZE_Z / 2F) || (position.z < 0 && Math.abs(position.z % CHUNK_SIZE_Z) >= CHUNK_SIZE_X / 2F)) {
      System.out.println("Bottom loaded");
      if (chunkIndex - chunksPerRow >= 0)
        chunksToCheck.add(chunks[chunkIndex - chunksPerRow]);
    } else {
      System.out.println("Top loaded");
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

  private List<Chunk> getAdiacentChunks(int chunkIndex) {
    return null;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    lastRenderedChunks = 0;
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
      renderable.material = materials[i];
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
    if (chunkIndex != 0)
      System.out.println("Chunk: " + chunkIndex);
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
          System.out.println("X: " + (x + chunk.getOffset().x / World.BLOCK_DISTANCE) + ", z: " + (z + chunk.getOffset().z / World.BLOCK_DISTANCE));
        }
      }
    }
  }
}
