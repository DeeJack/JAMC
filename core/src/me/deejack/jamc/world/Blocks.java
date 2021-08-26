package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.textures.TextureCache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Blocks {
  STONE("Stone", 0, 1),
  // STONE("Stone", 0, 1, 2, 3, 4, 5, 6);
  ASD("asd", 2, 0, 1, 2, 3, 4, 5),
  GRASS("Grass", 1, 0, 2, 3, 3, 3, 3),
  DIRT("Dirt", 3, 2),
  OAK_WOOD_PLANK("Oak Wood Plank", 4, 4),
  GOLD_ORE("Gold ore", 5, 32),
  IRON_ORE("Iron ore", 6, 33),
  COAL_ORE("Coal ore", 7, 34),
  BEDROCK("Bedrock", 8, 17),
  DIAMOND_ORE("Diamond ore", 9, 50),
  REDSTONE_ORE("Redstone ore", 10, 51);

  public final static int TEXTURE_PER_ROW = 16;
  private final static Map<Blocks, Model> cache = new HashMap<>();

  private final String name;
  private final int id;
  private final int topTextureId;
  private final int bottomTextureId;
  private final int leftTextureId;
  private final int rightTextureId;
  private final int frontTextureId;
  private final int backTextureId;

  Blocks(String name, int id, int topTextureId) {
    this(name, id, topTextureId, topTextureId, topTextureId, topTextureId, topTextureId, topTextureId);
  }

  Blocks(String name, int id, int topTextureId, int bottomTextureId, int leftTextureId,
         int rightTextureId, int frontTextureId, int backTextureId) {
    this.name = name;
    this.id = id;
    this.topTextureId = topTextureId;
    this.bottomTextureId = bottomTextureId;
    this.frontTextureId = frontTextureId;
    this.backTextureId = backTextureId;
    this.leftTextureId = leftTextureId;
    this.rightTextureId = rightTextureId;
  }

  public static Optional<Blocks> fromId(int id) {
    return Arrays.stream(Blocks.values()).filter(block -> block.id == id).findFirst();
  }

  public Block createBlock(float x, float y, float z) {
    Texture fullTexture = TextureCache.getFullTexture();
    TextureRegion[][] tiles = TextureCache.getTiles();
    if (cache.containsKey(this)) {
      return new Block(name, id, new Vector3(x, y, z), cache.get(this),
              tiles[topTextureId / TEXTURE_PER_ROW][topTextureId % TEXTURE_PER_ROW], tiles[bottomTextureId / TEXTURE_PER_ROW][bottomTextureId % TEXTURE_PER_ROW],
              tiles[leftTextureId / TEXTURE_PER_ROW][leftTextureId % TEXTURE_PER_ROW], tiles[rightTextureId / TEXTURE_PER_ROW][rightTextureId % TEXTURE_PER_ROW],
              tiles[frontTextureId / TEXTURE_PER_ROW][frontTextureId % TEXTURE_PER_ROW], tiles[backTextureId / TEXTURE_PER_ROW][backTextureId % TEXTURE_PER_ROW]);
    }
    int attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
    var modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    //var model = modelBuilder.createBox(World.BLOCK_DISTANCE / 2, World.BLOCK_DISTANCE / 2, World.BLOCK_DISTANCE / 2,
    //        new Material(ColorAttribute.createDiffuse(Color.BLACK)), attributes);
    MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes,
            new Material(TextureAttribute.createDiffuse(fullTexture)));
    meshBuilder.setUVRange(tiles[backTextureId % TEXTURE_PER_ROW][backTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 4, 0), new Vector3(4, 0, 0),
            new Vector3(4, 4, 0), new Vector3(0, 0, -1)); // Back face
    meshBuilder.setUVRange(tiles[frontTextureId % TEXTURE_PER_ROW][frontTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(0, 0, 4), new Vector3(0, 4, 4), new Vector3(4, 0, 4),
            new Vector3(4, 4, 4), new Vector3(0, 0, 1)); // Front face
    meshBuilder.setUVRange(tiles[bottomTextureId % TEXTURE_PER_ROW][bottomTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 0, 4), new Vector3(4, 0, 4),
            new Vector3(4, 0, 0), new Vector3(0, -1, 1)); // Bottom face
    meshBuilder.setUVRange(tiles[topTextureId % TEXTURE_PER_ROW][topTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(0, 4, 0), new Vector3(0, 4, 4), new Vector3(4, 4, 4),
            new Vector3(4, 4, 0), new Vector3(0, 1, 0)); // Top face
    meshBuilder.setUVRange(tiles[leftTextureId % TEXTURE_PER_ROW][leftTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(0, 4, 0), new Vector3(0, 4, 4), new Vector3(0, 0, 4),
            new Vector3(0, 0, 0), new Vector3(-1, 0, 0)); // Left face
    meshBuilder.setUVRange(tiles[rightTextureId % TEXTURE_PER_ROW][rightTextureId % TEXTURE_PER_ROW]);
    meshBuilder.rect(new Vector3(4, 4, 0), new Vector3(4, 4, 4), new Vector3(4, 0, 4),
            new Vector3(4, 0, 0), new Vector3(1, 0, 0)); // Right face
    var model = modelBuilder.end();
    cache.put(this, model);

    return new Block(name, id, new Vector3(x, y, z), model,
            tiles[topTextureId / TEXTURE_PER_ROW][topTextureId % TEXTURE_PER_ROW], tiles[bottomTextureId / TEXTURE_PER_ROW][bottomTextureId % TEXTURE_PER_ROW],
            tiles[leftTextureId / TEXTURE_PER_ROW][leftTextureId % TEXTURE_PER_ROW], tiles[rightTextureId / TEXTURE_PER_ROW][rightTextureId % TEXTURE_PER_ROW],
            tiles[frontTextureId / TEXTURE_PER_ROW][frontTextureId % TEXTURE_PER_ROW], tiles[backTextureId / TEXTURE_PER_ROW][backTextureId % TEXTURE_PER_ROW]);
  }

  public int getId() {
    return id;
  }
}
