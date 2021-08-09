package me.deejack.jamc.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureCache {
  private final int TEXTURE_SIZE = 16;
  private TextureRegion tiles[][];
  private Texture fullTexture;
  private static TextureCache INSTANCE = new TextureCache();

  private TextureCache() {
    if (INSTANCE != null)
      throw new AssertionError();
  }

  {
    var textureAtlas = new TextureAtlas(Gdx.files.internal("models/minecraft.atlas"));
    var cubeTextureRegion = textureAtlas.findRegion("minecraft");
    cubeTextureRegion.setRegionX(2);
    fullTexture = cubeTextureRegion.getTexture();
    tiles = cubeTextureRegion.split(TEXTURE_SIZE, TEXTURE_SIZE);
  }

  public static TextureRegion[][] getTiles() {
    return INSTANCE.tiles;
  }

  public static Texture getFullTexture() {
    return INSTANCE.fullTexture;
  }
}
