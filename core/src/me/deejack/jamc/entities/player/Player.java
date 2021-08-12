package me.deejack.jamc.entities.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.entities.Entity;
import me.deejack.jamc.world.World;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player implements Entity {
  public static final float WALKING_VELOCITY = 8;
  public static final float RUNNING_VELOCITY = 16;

  private final Camera camera;
  private final int movementSpeed = 2;
  private final Inventory inventory = new Inventory(40);
  private final AtomicBoolean jumping = new AtomicBoolean(false);
  private float cameraAngleY = -0;
  private float cameraAngleX = -0;
  private boolean flying = false;
  private float velocity = WALKING_VELOCITY;

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
    cameraAngleX += angle;
    cameraAngleX %= 360;
    //directionX.rotate(rotationAxis, angle);
  }

  public void rotateCameraY(Vector3 rotationAxis, float angle) {
    final int maxAngle = 85;
    angle %= maxAngle;
    if ((cameraAngleY <= -maxAngle && angle < 0) || (cameraAngleY >= maxAngle && angle > 0))
      return;
    camera.rotateAround(camera.position, rotationAxis, angle);
    cameraAngleY += angle;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Vector3 getPosition() {
    return camera.position.cpy().scl(1 / (float) World.BLOCK_DISTANCE);
    //return camera.position.cpy();
  }

  @Override
  public void setPosition(Vector3 add) {
    camera.position.set(add);
  }

  @Override
  public float getVelocity() {
    return velocity;
  }

  @Override
  public void setVelocity(float velocity) {
    this.velocity = velocity;
  }

  @Override
  public float getGravityCoefficient() {
    return 4;
  }

  public boolean isFlying() {
    return flying;
  }

  public void setFlying(boolean flying) {
    this.flying = flying;
  }

  public Vector3 getDirection() {
    System.out.println("Camera angle x: " + cameraAngleX);
    return new Vector3(MathUtils.sin(-MathUtils.degreesToRadians * cameraAngleX), 0, -MathUtils.cos(MathUtils.degreesToRadians * cameraAngleX));
  }

  public boolean isJumping() {
    return jumping.get();
  }

  public void setJumping(boolean isJumping) {
    this.jumping.set(isJumping);
  }
}
