package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public enum Blocks {
  STONE("Stone", 0, 1, 1, 1, 1, 1, 1),
  // STONE("Stone", 0, 1, 2, 3, 4, 5, 6);
  ASD("asd", 2, 0, 1, 2, 3, 4, 5),
  GRASS("Grass", 1, 0, 2, 3, 3, 3, 3),
  DIRT("Dirt", 3, 2, 2, 2, 2, 2, 2),
  OAK_WOOD_PLANK("Oak Wood Plank", 4, 4, 4, 4, 4, 4, 4);

  private final static int TEXTURE_SIZE = 16;
  private final static Map<Blocks, Model> cache = new HashMap<>();

  private final String name;
  private final int id;
  private final int topTextureId;
  private final int bottomTextureId;
  private final int leftTextureId;
  private final int rightTextureId;
  private final int frontTextureId;
  private final int backTextureId;

  private Blocks(String name, int id, int topTextureId, int bottomTextureId, int leftTextureId,
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

  public Block createBlock(float x, float y, float z, Texture fullTexture, TextureRegion[][] tiles) {
    if (cache.containsKey(this)) {
      return new Block(name, id, new Vector3(x, y, z), cache.get(this),
              tiles[0][topTextureId], tiles[0][bottomTextureId], tiles[0][leftTextureId], tiles[0][rightTextureId], tiles[0][frontTextureId], tiles[0][backTextureId]);
    }
    System.out.println("Creating model");
    int attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
    var modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    //var model = modelBuilder.createBox(World.BLOCK_DISTANCE / 2, World.BLOCK_DISTANCE / 2, World.BLOCK_DISTANCE / 2,
    //        new Material(ColorAttribute.createDiffuse(Color.BLACK)), attributes);
    MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes,
            new Material(TextureAttribute.createDiffuse(fullTexture)));
    meshBuilder.setUVRange(tiles[0][backTextureId]);
    meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 4, 0), new Vector3(4, 0, 0),
            new Vector3(4, 4, 0), new Vector3(0, 0, -1)); // Back face
    meshBuilder.setUVRange(tiles[0][frontTextureId]);
    meshBuilder.rect(new Vector3(0, 0, 4), new Vector3(0, 4, 4), new Vector3(4, 0, 4),
            new Vector3(4, 4, 4), new Vector3(0, 0, 1)); // Front face
    meshBuilder.setUVRange(tiles[0][bottomTextureId]);
    meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 0, 4), new Vector3(4, 0, 4),
            new Vector3(4, 0, 0), new Vector3(0, -1, 1)); // Bottom face
    meshBuilder.setUVRange(tiles[0][topTextureId]);
    meshBuilder.rect(new Vector3(0, 4, 0), new Vector3(0, 4, 4), new Vector3(4, 4, 4),
            new Vector3(4, 4, 0), new Vector3(0, 1, 0)); // Top face
    meshBuilder.setUVRange(tiles[0][leftTextureId]);
    meshBuilder.rect(new Vector3(0, 4, 0), new Vector3(0, 4, 4), new Vector3(0, 0, 4),
            new Vector3(0, 0, 0), new Vector3(-1, 0, 0)); // Left face
    meshBuilder.setUVRange(tiles[0][rightTextureId]);
    meshBuilder.rect(new Vector3(4, 4, 0), new Vector3(4, 4, 4), new Vector3(4, 0, 4),
            new Vector3(4, 0, 0), new Vector3(1, 0, 0)); // Right face
    var model = modelBuilder.end();
    cache.put(this, model);

    /**
     * MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes,
     *             new Material(TextureAttribute.createDiffuse(fullTexture)));
     *     meshBuilder.setUVRange(tiles[0][backTextureId]);
     *     meshBuilder.rect(new Vector3(2, -2, -2), new Vector3(-2, -2, -2), new Vector3(-2, 2, -2),
     *             new Vector3(2, 2, -2), new Vector3(0, 0, -1)); // Back face
     *     meshBuilder.setUVRange(tiles[0][frontTextureId]);
     *     meshBuilder.rect(new Vector3(-2, -2, 2), new Vector3(2, -2, 2), new Vector3(2, 2, 2),
     *             new Vector3(-2, 2, 2), new Vector3(0, 0, -1));
     *     meshBuilder.setUVRange(tiles[0][bottomTextureId]);
     *     meshBuilder.rect(new Vector3(2, -2, 2), new Vector3(-2, -2, 2), new Vector3(-2, -2, -2),
     *             new Vector3(2, -2, -2), new Vector3(0, -1, 0));
     *     meshBuilder.setUVRange(tiles[0][topTextureId]);
     *     meshBuilder.rect(new Vector3(-2, 2, -2), new Vector3(-2, 2, 2), new Vector3(2, 2, 2),
     *             new Vector3(2, 2, -2), new Vector3(0, 1, 0));
     *     meshBuilder.setUVRange(tiles[0][leftTextureId]);
     *     meshBuilder.rect(new Vector3(-2, -2, -2), new Vector3(-2, -2, 2), new Vector3(-2, 2, 2),
     *             new Vector3(-2, 2, -2), new Vector3(-1, 0, 0));
     *     meshBuilder.setUVRange(tiles[0][rightTextureId]);
     *     meshBuilder.rect(new Vector3(2, -2, 2), new Vector3(2, -2, -2), new Vector3(2, 2, -2),
     *             new Vector3(2, 2, 2), new Vector3(1, 0, 0));
     */

    return new Block(name, id, new Vector3(x, y, z), model,
            tiles[0][topTextureId], tiles[0][bottomTextureId], tiles[0][leftTextureId], tiles[0][rightTextureId], tiles[0][frontTextureId], tiles[0][backTextureId]);
  }
}
