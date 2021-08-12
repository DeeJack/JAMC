package me.deejack.jamc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.world.World;

import javax.swing.plaf.RootPaneUI;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerMovementProcessor implements InputProcessor {
  private final HashSet<Integer> pressedKey = new HashSet<>(); // Contains the currently pressed keys
  private final Player player;
  private final World world;

  public PlayerMovementProcessor(Player player, World world) {
    this.player = player;
    this.world = world;
  }

  @Override
  public boolean keyDown(int keyCode) {
    pressedKey.add(keyCode);
    var camera = (PerspectiveCamera) player.getCamera();
    switch (keyCode) {
      case Keys.C -> {
        camera.fieldOfView = 10;
      }
      case Keys.CONTROL_LEFT -> player.setVelocity(Player.RUNNING_VELOCITY);
    }
    return true;
  }

  @Override
  public boolean keyUp(int keyCode) {
    var camera = (PerspectiveCamera) player.getCamera();
    switch (keyCode) {
      case Keys.C -> {
        camera.fieldOfView = 90;
      }
      case Keys.F1 -> player.setFlying(!player.isFlying());
      case Keys.CONTROL_LEFT -> player.setVelocity(Player.WALKING_VELOCITY);
    }
    pressedKey.remove(keyCode);
    return true;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    if (!Gdx.input.isCursorCatched())
      return false;
    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    var camera = player.getCamera();
    var angleK = 0.3F; // Angle multiplier

    // I did not use the game delta time because this method is called the same number of times whether it's 1500fps or 60fps
    if (screenX - width / 2 != 0) {
      var angle = angleK * -1 * (screenX - width / 2);
      player.rotateCameraX(Vector3.Y, angle); // Rotate the camera around the Y axis
    }
    if (screenY - height / 2 != 0) {
      var angle = angleK * -1 * (screenY - height / 2);
      var rotationAxis = camera.direction.cpy().crs(camera.up);
      player.rotateCameraY(rotationAxis, angle); // Rotate the camera around the cross product of the camera direction and the camera up vector
    }
    Gdx.input.setCursorPosition(width / 2, height / 2); // Reset the position to the center of the screen
    return true;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }


  /**
   * Called every frame, it moves the player based on the pressed keys (there is not "key_pressed" event
   *
   * @param gameDeltaTime The game delta time
   */
  public void update(float gameDeltaTime) {
    final float movementSpeed = player.getVelocity();
    final int flightSpeed = 10;
    var camera = (PerspectiveCamera) player.getCamera();
    for (var keyCode : pressedKey) {
      switch (keyCode) {
        case Keys.A -> {
          var direction = player.getDirection().cpy();
          var horizontal = direction.crs(camera.up);
          horizontal.scl(-movementSpeed * gameDeltaTime, 0F, -movementSpeed * gameDeltaTime);
          var finalPosition = camera.position.cpy().add(horizontal).add(0, 0.5F, 0);
          if (!world.checkCollision(finalPosition))
            camera.translate(horizontal);
        }
        case Keys.D -> {
          var direction = player.getDirection().cpy();
          var horizontal = direction.crs(camera.up);
          horizontal.scl(movementSpeed * gameDeltaTime, 0F, movementSpeed * gameDeltaTime);
          var finalPosition = camera.position.cpy().add(horizontal).add(0, 0.5F, 0);
          if (!world.checkCollision(finalPosition))
            camera.translate(horizontal);
        }
        case Keys.W -> {
          //var direction = camera.direction.cpy();
          var direction = player.getDirection().cpy();
          //var direction = camera.direction.
          direction.scl(movementSpeed * gameDeltaTime, 0, movementSpeed * gameDeltaTime);
          var finalPosition = camera.position.cpy().add(direction).add(0, 0.5F, 0);
          if (!world.checkCollision(finalPosition))
            camera.translate(direction);
        }
        case Keys.S -> {
          var direction = player.getDirection().cpy();
          direction.scl(-movementSpeed * gameDeltaTime, 0, -movementSpeed * gameDeltaTime);
          var finalPosition = camera.position.cpy().add(direction).add(0, 0.5F, 0);
          if (!world.checkCollision(finalPosition))
            camera.translate(direction);
        }
        case Keys.SHIFT_LEFT -> {
          var direction = new Vector3(0, -flightSpeed * gameDeltaTime, 0);
          var finalPosition = camera.position.cpy().add(direction).add(0, 0.5F, 0);
          System.out.println(finalPosition);
          if (!world.checkCollision(finalPosition))
            camera.translate(direction);
        }
        case Keys.SPACE -> {
          Vector3 direction = new Vector3();
          if (player.isFlying()) {
            direction.set(0, flightSpeed * gameDeltaTime, 0);
            var finalPosition = camera.position.cpy().add(direction).add(0, 0.5F, 0);
            if (!world.checkCollision(finalPosition))
              camera.translate(direction);
          }
          else {
            if (player.isJumping() || !world.checkCollision(player)) // If it's not on the ground, stop
              break;

            // TODO: the jumping is terrible...
            var jumpThread = new JumpThread(player, gameDeltaTime);
            jumpThread.start();
          }
        }
      }
    }
  }

  private static class JumpThread extends Thread {
    private final Player player;
    private final PerspectiveCamera camera;
    private float gameDeltaTime;

    public JumpThread(Player player, float gameDeltaTime) {
      this.player = player;
      this.camera = (PerspectiveCamera) player.getCamera();
      this.gameDeltaTime = gameDeltaTime;
    }

    @Override
    public void run() {
      Vector3 direction = new Vector3(0, 10, 0);
      final var finalPosition = camera.position.cpy().add(direction);
      player.setJumping(true);
      //var initialPosition = camera.position.cpy();
      //var initialPosition = camera.position.cpy();
      while (camera.position.y < finalPosition.y) {
        float jumpSpeed = 500 * gameDeltaTime;
        //direction.clamp(0.5F, 0.5F);
        //camera.position.lerp(finalPosition, alpha);
        camera.position.y += jumpSpeed;
        //camera.position.set(initialPosition);
        // camera.position.set(initialPosition);
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      try {
        Thread.sleep(30);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Jump complete");
      player.setJumping(false);
    }
  }
}
