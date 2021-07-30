package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
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
  private final Coordinates coordinates;
  private final ModelInstance modelInstance;
  private final BoundingBox boundingBox = new BoundingBox();
  private boolean selected = false;

  public Block(String name, int id, Coordinates coordinates, Model model) {
    this.name = name;
    this.id = id;
    this.coordinates = coordinates;
    this.modelInstance = new ModelInstance(model);
    modelInstance.transform.translate(coordinates.x(), coordinates.y(), coordinates.z());
    modelInstance.calculateBoundingBox(boundingBox);
  }

  @Override
  public ModelInstance getModel() {
    return modelInstance;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public float distanceFrom(float x, float y, float z) {
    return new Vector3(coordinates.x(), coordinates.y(), coordinates.z()).dst(x, y, z);
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
}
