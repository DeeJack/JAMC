package me.deejack.jamc.items;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.textures.TextureCache;
import me.deejack.jamc.world.Coordinates;

import java.util.HashMap;
import java.util.Map;

public enum Items {
  STONE("Stone", 0, Categories.BLOCKS, 1),
  // STONE("Stone", 0, 1, 2, 3, 4, 5, 6);
  ASD("asd", 2, Categories.BLOCKS, 3),
  GRASS("Grass", 1, Categories.BLOCKS, 3);

  private final static int TEXTURE_SIZE = 16;
  private final static Map<Items, TextureRegion> cache = new HashMap<>();

  private final String name;
  private final int id;
  private final int textureId;
  private final Categories category;

  private Items(String name, int id, Categories category, int textureId) {
    this.name = name;
    this.id = id;
    this.textureId = textureId;
    this.category = category;
  }

  public Item createItem() {
    var tiles = TextureCache.getTiles();
    if (cache.containsKey(this)) {
      return new Item(name, id, cache.get(this));
    }
    cache.put(this, tiles[0][textureId]);
    return new Item(name, id, tiles[0][textureId]);
  }
}
