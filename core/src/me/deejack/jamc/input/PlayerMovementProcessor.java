package me.deejack.jamc.input;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
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
        System.out.println("AAAAAA");
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        System.out.println("asd");
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
        System.out.println("SS");
        // TODO Auto-generated method stub
        return false;
    }

    public void update(float gameDeltaTime) {
        final int movementSpeed = 2;
        var camera = player.getCamera();
        for (var keyCode : pressedKey) {
            switch (keyCode) {
                case Keys.A: {
                    var direction = camera.direction.cpy();
                    var horizontal = direction.crs(camera.up);
                    horizontal.scl(-2 * gameDeltaTime, 0, -2 * gameDeltaTime).nor();
                    camera.translate(direction);
                    break;
                }
                case Keys.D: {
                    var direction = camera.direction.cpy();
                    var horizontal = direction.crs(camera.up);
                    horizontal.scl(2 * gameDeltaTime, 0, 2 * gameDeltaTime).nor();
                    camera.translate(direction);
                    break;
                }
                case Keys.W: {                    
                    var direction = camera.direction.cpy();
                    direction.scl(2 * gameDeltaTime, 0, 2 * gameDeltaTime).nor();
                    camera.translate(direction);
                    break;
                }
                case Keys.S: {
                    var direction = camera.direction.cpy();
                    direction.scl(-2 * gameDeltaTime, 0, -2 * gameDeltaTime).nor();
                    camera.translate(direction);
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
