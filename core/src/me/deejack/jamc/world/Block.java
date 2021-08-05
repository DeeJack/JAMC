package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import me.deejack.jamc.gameobjects.Drawable;

public class Block implements Drawable {
  private final String name;
  private final int id;
  //private Texture texture;
  private final Vector3 coordinates;
  private final ModelInstance modelInstance;
  private final BoundingBox boundingBox = new BoundingBox();
  private boolean selected = false;

  private final TextureRegion topTexture;
  private final TextureRegion bottomTexture;
  private final TextureRegion leftTexture;
  private final TextureRegion rightTexture;
  private final TextureRegion frontTexture;
  private final TextureRegion backTexture;

  public Block(String name, int id, Vector3 coordinates, Model model, TextureRegion topTexture, TextureRegion bottomTexture,
               TextureRegion leftTexture, TextureRegion rightTexture, TextureRegion frontTexture, TextureRegion backTexture) {
    this.name = name;
    this.id = id;
    this.coordinates = coordinates;
    this.modelInstance = new ModelInstance(model);
    System.out.println(modelInstance.transform);
    modelInstance.transform.translate(coordinates.x * World.BLOCK_DISTANCE, coordinates.y * World.BLOCK_DISTANCE, coordinates.z * World.BLOCK_DISTANCE);
    System.out.println(modelInstance.transform);
    modelInstance.calculateBoundingBox(boundingBox);

    this.topTexture = topTexture;
    this.bottomTexture = bottomTexture;
    this.leftTexture = leftTexture;
    this.rightTexture = rightTexture;
    this.frontTexture = frontTexture;
    this.backTexture = backTexture;
  }

  @Override
  public ModelInstance getModel() {
    return modelInstance;
  }

  public Vector3 getCoordinates() {
    return coordinates;
  }

  public float distanceFrom(float x, float y, float z) {
    return coordinates.cpy().dst(x, y, z);
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

  public void unmark() {
    modelInstance.materials.get(0).remove(ColorAttribute.Diffuse);
  }

  public void mark() {
    modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLUE));
  }

  public void toggleSelection() {
    selected = !selected;
    if (selected)
      modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
    else
      modelInstance.materials.get(0).remove(ColorAttribute.Diffuse);
  }

  public boolean isSelected() {
    return selected;
  }

  public boolean isVisible(Camera cam) {
    return cam.frustum.sphereInFrustum(modelInstance.transform.getTranslation(new Vector3()).add(boundingBox.getCenter(new Vector3())),
            World.BLOCK_DISTANCE);
  }

  public TextureRegion getTopTexture() {
    return topTexture;
  }

  public TextureRegion getBottomTexture() {
    return bottomTexture;
  }

  public TextureRegion getLeftTexture() {
    return leftTexture;
  }

  public TextureRegion getRightTexture() {
    return rightTexture;
  }

  public TextureRegion getFrontTexture() {
    return frontTexture;
  }

  public TextureRegion getBackTexture() {
    return backTexture;
  }
}
