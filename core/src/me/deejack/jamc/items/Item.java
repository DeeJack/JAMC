package me.deejack.jamc.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {
  private final String name;
  private final int id;
  private final TextureRegion image;
  private int quantity = 1;

  public Item(String name, int id, TextureRegion image) {
    this.name = name;
    this.id = id;
    this.image = image;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
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
