package me.deejack.jamc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.World;

public class GameInputProcessor implements InputProcessor {
    private final World world;
    private final Player player;

    public GameInputProcessor(World world, Player player) {
        this.world = world;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keyCode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        var position = player.getCamera().position;
        if (button == Input.Buttons.LEFT) {
            for (var block : world.getNearBlocks(position)) {
                var pickRay = player.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                var direction = pickRay.direction.scl(13);
                pickRay.set(player.getCamera().position, direction);

                var boundingBox = block.getBoundingBox();
                block.getModel().calculateBoundingBox(boundingBox);
                boundingBox.mul(block.getModel().transform);

                Vector3 intersection = new Vector3();
                Vector3 end = new Vector3();
                pickRay.getEndPoint(end, 10);
                //System.out.println(end);
                //pickRay.direction.scl(13);

                
                if (Intersector.intersectRayBoundsFast(pickRay, boundingBox))
                    block.toggleSelection();

                
                if (Intersector.intersectRayBounds(pickRay, boundingBox, intersection)) {
                    System.out.println("YEP");
                    block.toggleSelection();
                    // world.destroyBlock(block);
                }
            }
        }
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
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
