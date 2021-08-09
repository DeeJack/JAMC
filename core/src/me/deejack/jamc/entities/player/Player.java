package me.deejack.jamc.entities.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import me.deejack.jamc.entities.Entity;

public class Player implements Entity {
  private final Camera camera;
  private final int movementSpeed = 2;
  private final Inventory inventory = new Inventory(40);
  private float cameraAngle = -0;
  private boolean flying = false;

  public Player(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera() {
    return camera;
  }

  @Override
  public void move(Vector3 target) {
    //target.scl(-movementSpeed * gameDeltaTime, 0, -movementSpeed * gameDeltaTime).nor();
    camera.translate(target);
  }

  public void rotateCameraX(Vector3 rotationAxis, float angle) {
    camera.rotateAround(camera.position, rotationAxis, angle);
  }

  public void rotateCameraY(Vector3 rotationAxis, float angle) {
    final int maxAngle = 85;
    angle %= maxAngle;
    if ((cameraAngle <= -maxAngle && angle < 0) || (cameraAngle >= maxAngle && angle > 0))
      return;
    rotateCameraX(rotationAxis, angle);
    cameraAngle += angle;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Vector3 getPosition() {
    return camera.position.cpy().scl(1 / 3F);
    //return camera.position.cpy();
  }

  @Override
  public void setPosition(Vector3 add) {
    camera.position.set(add);
  }

  @Override
  public float getGravityCoefficient() {
    return 2;
  }

  public void setFlying(boolean flying) {
    this.flying = flying;
  }

  public boolean isFlying() {
    return flying;
  }
}
