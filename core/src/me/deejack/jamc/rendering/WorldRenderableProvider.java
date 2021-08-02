package me.deejack.jamc.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.deejack.jamc.world.Block;

import java.util.Arrays;

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

  /**
   * Create the world
   *
   * @param tiles     The textures to be used
   * @param chunksOnX The number of chunks on the x-axis
   * @param chunksOnZ The number of chunks on the z-axis
   */
  public WorldRenderableProvider(TextureRegion[][] tiles, Texture fullTexture, int chunksOnX, int chunksOnZ) {
    this.chunks = new Chunk[chunksOnX * chunksOnZ];

    int currentChunk = 0;
    for (int x = 0; x < chunksOnX; x++) {
      for (int z = 0; z < chunksOnZ; z++) {
        chunks[currentChunk++] = new Chunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, x * CHUNK_SIZE_X, 0, z * CHUNK_SIZE_Z);
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

    this.meshes = new Mesh[chunksOnX * chunksOnZ];
    for (int i = 0; i < meshes.length; i++) {
      meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * Chunk.VERTEXES_PER_FACE * Chunk.CUBE_FACES, indices.length,
              VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
      meshes[i].setIndices(indices);
    }

    this.materials = new Material[chunksOnX * chunksOnZ];
    for (int chunk = 0; chunk < materials.length; chunk++) {
      for (int block = 0; block < chunks[chunk].getBlocks().length; block++) {
        materials[chunk] = new Material(new TextureAttribute(TextureAttribute.Diffuse, fullTexture));
      }
      /*materials[i] = new Material(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(0.5f, 1f), MathUtils.random(
              0.5f, 1f), MathUtils.random(0.5f, 1f), 1));*/
    }

    this.dirty = new boolean[chunksOnX * chunksOnZ];
    Arrays.fill(dirty, true);

    this.numOfIndices = new int[chunksOnX * chunksOnZ];
    Arrays.fill(numOfIndices, (short) 0);
  }

  public void placeBlock(int x, int y, int z, Block block) {
    chunks[x / CHUNK_SIZE_X + z / CHUNK_SIZE_Z].set(x % CHUNK_SIZE_X, y, z % CHUNK_SIZE_Z, block);
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

    System.out.println("Render " + lastRenderedChunks + " chunks");
  }
}
