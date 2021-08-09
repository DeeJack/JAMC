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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.world.Block;

import java.util.*;
import java.util.stream.Collectors;

public class WorldRenderableProvider implements RenderableProvider {
  private final static int CHUNK_SIZE_X = 16;
  private final static int CHUNK_SIZE_Y = 256;
  private final static int CHUNK_SIZE_Z = 16;

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
  private int lastRenderedChunks = 0;

  private final int chunksOnX;
  private final int chunksOnZ;

  private int chunkOffset = 0;

  /**
   * Create the world
   *
   * @param tiles     The textures to be used
   * @param chunks The number of chunks
   */
  public WorldRenderableProvider(TextureRegion[][] tiles, Texture fullTexture, int chunks) {
    this.chunks = new Chunk[chunks];

    if (chunks % 2 == 1)
      throw new IllegalArgumentException("Chunk num odd");
    this.chunksOnX = this.chunksOnZ = chunks / 2;

    int currentChunk = 0;

    for (int z = 0; z <  chunks / 2; z++) {
      for (int x = 0; x < chunks / 2; x++) {
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

    this.meshes = new Mesh[chunks];
    for (int i = 0; i < meshes.length; i++) {
      meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * Chunk.VERTEXES_PER_FACE * Chunk.CUBE_FACES, indices.length,
              VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
      meshes[i].setIndices(indices);
    }

    this.materials = new Material[chunks];
    for (int chunk = 0; chunk < materials.length; chunk++) {
      for (int block = 0; block < this.chunks[chunk].getBlocks().length; block++) {
        materials[chunk] = new Material(new TextureAttribute(TextureAttribute.Diffuse, fullTexture));
      }
      /*materials[i] = new Material(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(0.5f, 1f), MathUtils.random(
              0.5f, 1f), MathUtils.random(0.5f, 1f), 1));*/
    }

    this.dirty = new boolean[chunks];
    Arrays.fill(dirty, true);

    this.numOfIndices = new int[chunks];
    Arrays.fill(numOfIndices, (short) 0);
  }

  public void placeBlock(int x, int y, int z, Block block) {
    int chunk = x / CHUNK_SIZE_X + z / CHUNK_SIZE_Z * (chunksOnX);
    chunks[chunk].set(x % CHUNK_SIZE_X, y, z % CHUNK_SIZE_Z, block);
    dirty[chunk] = true;
  }

  public void destroyBlock(int x, int y, int z) {
    placeBlock(x, y, z, null);
  }

  public List<Block> getNearBlocks(Vector3 position) {
    return getNearBlocks(position, 5);
  }

  public List<Block> getNearBlocks(Vector3 position, int distance) {
    var chunk = chunks[((int) position.x / CHUNK_SIZE_X + (int) position.z / CHUNK_SIZE_Z * (chunksOnX))];
    final int maxDistance = distance;
    var nearBlocks = new ArrayList<>(chunk.getRenderedBlocks());

    return nearBlocks.stream().filter(Objects::nonNull)
            .filter(block -> block.distanceFrom(position.x, position.y, position.z) < maxDistance)
            .sorted(Comparator.comparingDouble(block -> block.distanceFrom(position.x, position.y, position.z)))
            .collect(Collectors.toList());
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
    return chunks[x / CHUNK_SIZE_X + z / CHUNK_SIZE_Z * (chunksOnX)];
  }
}
