package me.deejack.jamc.items;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import me.deejack.jamc.gameobjects.Drawable;
import me.deejack.jamc.world.Coordinates;
import me.deejack.jamc.world.World;

public class Item {
  private final String name;
  private final int id;
  private final TextureRegion image;

  public Item(String name, int id, TextureRegion image) {
    this.name = name;
    this.id = id;
    this.image = image;

  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public TextureRegion getImage() {
    return image;
  }
}
