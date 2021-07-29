package me.deejack.jamc.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class Player {
  private final Camera camera;
  private float cameraAngle = -30;
  private int movementSpeed = 2;

  public Player(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera() {
    return camera;
  }

  public void move(Vector3 target, float gameDeltaTime) {
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
}
