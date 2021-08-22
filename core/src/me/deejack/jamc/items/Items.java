package me.deejack.jamc.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.deejack.jamc.textures.TextureCache;
import me.deejack.jamc.world.Blocks;

import java.util.HashMap;
import java.util.Map;

public enum Items {
  STONE("Stone", 0, Categories.BLOCK, 1),
  GRASS("Grass", 1, Categories.BLOCK, 3),
  ASD("Asd", 2, Categories.BLOCK, 3),
  DIRT("Dirt", 3, Categories.BLOCK, 2),
  OAK_WOOD_PLANK("Oak Wood Plank", 4, Categories.BLOCK, 4),
  GOLD_ORE("Gold ore", 5, Categories.BLOCK, 32);

  private final static Map<Items, TextureRegion> cache = new HashMap<>();

  private final String name;
  private final int id;
  private final int textureId;
  private final Categories category;

  Items(String name, int id, Categories category, int textureId) {
    this.name = name;
    this.id = id;
    this.textureId = textureId;
    this.category = category;
  }

  public Item createItem() {
    if (cache.containsKey(this)) {
      return new Item(name, id, cache.get(this));
    }
    var tiles = TextureCache.getTiles();
    cache.put(this, tiles[textureId / Blocks.TEXTURE_PER_ROW][textureId % Blocks.TEXTURE_PER_ROW]);
    return new Item(name, id, tiles[textureId / Blocks.TEXTURE_PER_ROW][textureId % Blocks.TEXTURE_PER_ROW]);
  }

  public int getId() {
    return id;
  }
}
