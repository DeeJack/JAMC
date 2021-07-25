package me.deejack.jamc.input;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.World;

public class PlayerMovementProcessor implements InputProcessor {
    private final HashSet<Integer> pressedKey = new HashSet<>();
    private final Player player;
    private final World world; // TODO: remove after debug

    public PlayerMovementProcessor(Player player, World world) {
        this.player = player;
        this.world = world;
    }

    @Override
    public boolean keyDown(int keyCode) {
        pressedKey.add(keyCode);
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
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
            player.rotateCameraX(Vector3.Y, angle);
            //camera.rotateAround(camera.position, new Vector3(0, 1, 0), angle);
        }
        if (screenY - height / 2 != 0) {
            var angle = -1 * 20 * (screenY - height / 2) * Gdx.graphics.getDeltaTime();
            // camera.rotate(camera.up, angle);
            var rotationAxis = camera.direction.cpy().crs(camera.up);
            player.rotateCameraY(rotationAxis, angle);
            //camera.rotateAround(camera.position, rotationAxis, angle);
            // if (player.getCamera().up.isPerpendicular(new Vector3(1, 0, 0)))

            // player.getCamera().rotate(new Vector3(1, 0, 0), angle);
        }
        Gdx.input.setCursorPosition(width / 2, height / 2);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void update(float gameDeltaTime) {
        final float movementSpeed = 5F;
        final int flightSpeed = 10;
        var camera = player.getCamera();
        for (var keyCode : pressedKey) {
            switch (keyCode) {
                case Keys.A: {
                    var direction = camera.direction.cpy();
                    var horizontal = direction.crs(camera.up);
                    horizontal.scl(-movementSpeed * gameDeltaTime, 0F, -movementSpeed * gameDeltaTime);
                    camera.translate(direction);
                    break;
                }
                case Keys.D: {
                    var direction = camera.direction.cpy();
                    var horizontal = direction.crs(camera.up);
                    horizontal.scl(movementSpeed * gameDeltaTime, 0F, movementSpeed * gameDeltaTime);
                    camera.translate(direction);
                    break;
                }
                case Keys.W: {                    
                    var direction = camera.direction.cpy();
                    direction.scl(movementSpeed * gameDeltaTime, 0, movementSpeed * gameDeltaTime);
                    camera.translate(direction);
                    break;
                }
                case Keys.S: {
                    var direction = camera.direction.cpy();
                    direction.scl(-movementSpeed * gameDeltaTime, 0, -movementSpeed * gameDeltaTime);
                    camera.translate(direction);
                    break;
                }
                case Keys.SHIFT_LEFT:
                    camera.translate(0, -flightSpeed * gameDeltaTime, 0);
                    break;
                case Keys.SPACE:
                    camera.translate(0, flightSpeed * gameDeltaTime, 0);
                    break;
            }
            for (var block: world.getBlocks())
                block.unselect();
            for (var block : world.getNearBlocks(player.getCamera().position)) {
                block.select();
            }
        }
    }
}
