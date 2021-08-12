package me.deejack.jamc.rendering;

import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a chunk of the world, it's used to optimize the rendering of the world, so that only the top, bottom and
 * lateral blocks are rendered, instead of
 */
public class Chunk {
  public final static int VERTEXES_PER_FACE = 4; // Using indexes, we can have 4 vertices per faces
  public final static int CUBE_FACES = 6; // A cube has 6 faces

  /**
   * The height, width and depth of the current chunk
   */
  private final int height;
  private final int width;
  private final int depth;

  /**
   * The vertexes that needs to be rendered
   */
  private final Vector3[] vertexes;

  /**
   * The blocks in the chunk
   */
  private final Block[] blocks;

  /**
   * The offset of the chunk compared to the world's coordinates
   */
  private final Vector3 offset = new Vector3();

  private final List<Block> renderedBlocks = new ArrayList<>();

  /**
   * The offset needed to get the another block with the index of a certain block in the blocks' array
   */
  private final int topOffset;
  private final int bottomOffset;
  private final int leftOffset;
  private final int rightOffset;
  private final int frontOffset;
  private final int backOffset;

  public Chunk(int width, int height, int depth, int x, int y, int z) {
    this.height = height;
    this.width = width;
    this.depth = depth;
    this.vertexes = new Vector3[height * width * depth * VERTEXES_PER_FACE * CUBE_FACES];
    this.blocks = new Block[height * width * depth];
    this.offset.set(x * World.BLOCK_DISTANCE, y, z * World.BLOCK_DISTANCE);

    this.topOffset = width * depth; // The block on top of another block can be obtained adding width * depth (a full cicle)
    this.bottomOffset = -topOffset; // The bottom one is the opposite of the top offset
    this.leftOffset = -1;  // The left one is the previous block
    this.rightOffset = 1; // The right one is the next block
    this.frontOffset = -width; // The block at the front is -width (z is positive in this sense ↓)
    this.backOffset = -frontOffset; // The opposite of the front side
  }

  /**
   * Create the chunk with the default size
   *
   * @param x The origin of the chunk in world's coordinates
   * @param y The origin of the chunk in world's coordinates
   * @param z The origin of the chunk in world's coordinates
   */
  public Chunk(int x, int y, int z) {
    this(16, 256, 16, x, y, z);
  }

  public void set(int x, int y, int z, Block block) {
    blocks[x + z * width + y * width * depth] = block;
  }

  public Block get(int x, int y, int z) {
    return blocks[x + z * width + y * width * depth];
  }

  public int createTopFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getTopTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top left vertex
    vertexes[currentOffset++] = new Vector3(0, 1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Bottom left vertex (of the top face)
    vertexes[currentOffset++] = new Vector3(0, 1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(0, 1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top right vertex
    vertexes[currentOffset++] = new Vector3(0, 1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int createBottomFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getBottomTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Top left vertex
    vertexes[currentOffset++] = new Vector3(0, -1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z); // Bottom left vertex (of the bottom face)
    vertexes[currentOffset++] = new Vector3(0, -1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(0, -1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Top right vertex
    vertexes[currentOffset++] = new Vector3(0, -1, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int createLeftFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getLeftTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Top left vertex (of the right face)
    vertexes[currentOffset++] = new Vector3(-1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Bottom left vertex
    vertexes[currentOffset++] = new Vector3(-1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(-1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top right vertex
    vertexes[currentOffset++] = new Vector3(-1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int createRightFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getRightTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Top left vertex (of the right face)
    vertexes[currentOffset++] = new Vector3(1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Bottom left vertex
    vertexes[currentOffset++] = new Vector3(1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top right vertex
    vertexes[currentOffset++] = new Vector3(1, 0, 0); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int createFrontFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getFrontTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top left vertex (of the front face)
    vertexes[currentOffset++] = new Vector3(0, 0, 1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z); // Bottom left vertex
    vertexes[currentOffset++] = new Vector3(0, 0, 1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(0, 0, 1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z); // Top right vertex
    vertexes[currentOffset++] = new Vector3(0, 0, 1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int createBackFace(int x, int y, int z, int currentOffset, Block block) {
    var texture = block.getBackTexture();
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Top left vertex (of the front face)
    vertexes[currentOffset++] = new Vector3(0, 0, -1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Bottom left vertex
    vertexes[currentOffset++] = new Vector3(0, 0, -1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y, offset.z + z + World.BLOCK_DISTANCE); // Bottom right vertex
    vertexes[currentOffset++] = new Vector3(0, 0, -1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV2(), Integer.MIN_VALUE); // Texture coords
    vertexes[currentOffset++] = new Vector3(offset.x + x + World.BLOCK_DISTANCE, offset.y + y + World.BLOCK_DISTANCE, offset.z + z + World.BLOCK_DISTANCE); // Top right vertex
    vertexes[currentOffset++] = new Vector3(0, 0, -1); // Normal
    vertexes[currentOffset++] = new Vector3(texture.getU2(), texture.getV(), Integer.MIN_VALUE); // Texture coords

    return currentOffset;
  }

  public int calculateVertices() {
    int currentBlockIndex = 0;
    int currentOffset = 0;
    renderedBlocks.clear();
    for (int y = 0; y < height; y++) {
      for (int z = 0; z < depth; z++) {
        for (int x = 0; x < width; x++, currentBlockIndex++) {
          if (blocks[currentBlockIndex] == null) // It's a block of air
            continue;
          boolean rendered = false;

          if (y == height - 1 || blocks[currentBlockIndex + topOffset] == null) { // It's at the top of the chunk or the block on top of this block is air
            currentOffset = createTopFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (y == 0 || blocks[currentBlockIndex + bottomOffset] == null) { // If it's the bottom block of the chunk or the block on the bottom of this block is air
            currentOffset = createBottomFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (x == 0 || blocks[currentBlockIndex + leftOffset] == null) { // If it's the left-most block of the chunk or the block on the left of this block is air
            currentOffset = createLeftFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (x == width - 1 || blocks[currentBlockIndex + rightOffset] == null) { // If it's the right-most block of the chunk or the block on the right of this block is air
            currentOffset = createRightFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (z == 0 || blocks[currentBlockIndex + frontOffset] == null) { // If it's the block on the front of the chunk or the block on the front of this block is air
            currentOffset = createFrontFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (z == depth - 1 || blocks[currentBlockIndex + backOffset] == null) { // If it's the block on the back of the chunk or the block on the front of this back is air
            currentOffset = createBackFace(x * World.BLOCK_DISTANCE, y * World.BLOCK_DISTANCE, z * World.BLOCK_DISTANCE, currentOffset, blocks[currentBlockIndex]);
            rendered = true;
          }

          if (rendered)
            renderedBlocks.add(blocks[currentBlockIndex]);
        }
      }
    }
    vertexes[currentOffset + 1] = null;
    //Arrays.fill(vertexes, currentOffset, currentOffset + 1, null);
    //System.out.println("Current offset: " + currentOffset);
    return currentOffset * 3 / 6; // 3 because 1 vector3 represent 3 floats
  }

  public float[] getVertexes() {
    float[] floatVertices = new float[vertexes.length * 3];
    int index = 0;
    for (int i = 0; i < floatVertices.length && index < vertexes.length; i += 3, index++) {
      if (vertexes[index] == null)
        break;
      floatVertices[i] = vertexes[index].x;
      floatVertices[i + 1] = vertexes[index].y;
      if (vertexes[index].z == Integer.MIN_VALUE) { // It's Integer.MIN_VALUE only for the texture, which only needs a Vector2
        i--;
      } else
        floatVertices[i + 2] = vertexes[index].z;
    }
    return floatVertices;
  }

  public Block[] getBlocks() {
    return blocks;
  }

  public List<Block> getRenderedBlocks() {
    return renderedBlocks;
  }

  public Vector3 getOffset() {
    return offset;
  }
}
