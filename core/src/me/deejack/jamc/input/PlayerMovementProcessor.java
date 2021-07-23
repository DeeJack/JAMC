package me.deejack.jamc.input;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import me.deejack.jamc.player.Player;

public class PlayerMovementProcessor implements InputProcessor {
    // private final KeyPressEvent keyPressEvent;
    private final HashSet<Integer> pressedKey = new HashSet<>();
    private final Player player;

    public PlayerMovementProcessor(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keyCode) {
        pressedKey.add(keyCode);
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        pressedKey.remove(keyCode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        var camera = player.getCamera();

        if (screenX - width / 2 != 0) {
            var angle = -1 * 20 * (screenX - width / 2) * Gdx.graphics.getDeltaTime();
            // camera.rotate(new Vector3(0, 1, 0), angle);
            camera.rotateAround(camera.position, new Vector3(0, 1, 0), angle);
        }
        if (screenY - height / 2 != 0) {
            var angle = -1 * 20 * (screenY - height / 2) * Gdx.graphics.getDeltaTime();
            // camera.rotate(camera.up, angle);
            var rotationAxis = camera.direction.cpy().crs(camera.up);
            camera.rotateAround(camera.position, rotationAxis, angle);
            // if (player.getCamera().up.isPerpendicular(new Vector3(1, 0, 0)))

            // player.getCamera().rotate(new Vector3(1, 0, 0), angle);
        }

        Gdx.input.setCursorPosition(width / 2, height / 2);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }

    public void update(float gameDeltaTime) {
        final int movementSpeed = 20;
        var camera = player.getCamera();
        for (var keyCode : pressedKey) {
            switch (keyCode) {
                case Keys.A: {
                    var cosineAngleBetween = (Vector3.X.dot(camera.direction.cpy().crs(camera.up)))
                            / (camera.direction.cpy().crs(camera.up).len());
                    float xMovement = -movementSpeed * gameDeltaTime * (float) cosineAngleBetween;
                    float zMovement = -movementSpeed * gameDeltaTime
                            * (float) Math.sqrt(1 - cosineAngleBetween * cosineAngleBetween);
                    camera.translate(xMovement, 0, zMovement);
                    // player.getCamera().translate(-2 * gameDeltaTime, 0, 0);
                    break;
                }
                case Keys.D: {
                    var cosineAngleBetween = (Vector3.X.dot(camera.direction.cpy().crs(camera.up)))
                            / (Vector3.X.len() * camera.direction.cpy().crs(camera.up).len());
                    float xMovement = movementSpeed * gameDeltaTime * (float) cosineAngleBetween;
                    float zMovement = -movementSpeed * gameDeltaTime
                            * (float) Math.sqrt(1 - cosineAngleBetween * cosineAngleBetween);
                    camera.translate(xMovement, 0, zMovement);
                    break;
                }
                case Keys.W: {
                    // Calculate the cosine of the angle between the camera direction and the X axis
                    // Using the formula <DIR, X> / (||DIR|| * ||X||) where ||X|| is one
                    var direction = camera.direction.cpy();
                    var cosineAngleBetween = (direction.dot(Vector3.X)) / (Vector3.X.len() * camera.direction.len());
                    var sineAngleBetween = (float) Math.sqrt(1 - cosineAngleBetween * cosineAngleBetween);
                    System.out.println("Pos: " + camera.position + ", dir: " + direction + "cos: " + cosineAngleBetween
                            + ", sin: " + sineAngleBetween);
                    float xMovement = movementSpeed * gameDeltaTime * (float) cosineAngleBetween;
                    float zMovement = -movementSpeed * gameDeltaTime * (float) sineAngleBetween;
                    if (direction.z > 0)
                        zMovement *= -1;
                    camera.translate(new Vector3(xMovement, 0, zMovement));
                    break;
                }
                case Keys.S: {
                    // Calculate the cosine of the angle between the camera direction and the X axis
                    // Using the formula <DIR, X> / (||DIR|| * ||X||) where ||X|| is one
                    var direction = camera.direction.cpy();
                    var cosineAngleBetween = (direction.dot(Vector3.X)) / (Vector3.X.len() * camera.direction.len());
                    var sineAngleBetween = (float) Math.sqrt(1 - cosineAngleBetween * cosineAngleBetween);
                    System.out.println("Pos: " + camera.position + ", dir: " + direction + "cos: " + cosineAngleBetween
                            + ", sin: " + sineAngleBetween);
                    float xMovement = -movementSpeed * gameDeltaTime * (float) cosineAngleBetween;
                    float zMovement = movementSpeed * gameDeltaTime * (float) sineAngleBetween;
                    if (direction.z > 0)
                        zMovement *= -1;
                    camera.translate(new Vector3(xMovement, 0, zMovement));
                    break;
                }
                case Keys.SHIFT_LEFT:
                    camera.translate(0, -2 * gameDeltaTime, 0);
                    break;
                case Keys.SPACE:
                    camera.translate(0, 2 * gameDeltaTime, 0);
                    break;
            }
        }
    }
}
