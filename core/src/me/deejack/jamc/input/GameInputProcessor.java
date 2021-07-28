package me.deejack.jamc.input;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;

import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.Coordinates;
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

    /**
     * Find the block that is pointed by the camera (in the middle of the screen)
     * from the near blocks, return {@link Optional#empty()} there isn't a block
     * @param outIntersection A vector3 in which will be put the intersection point between the ray of the camera and the block
     * @return The block pointed by the camera, or {@link Optional#empty()}
     */
    private Optional<Block> findPickedBlock(Vector3 outIntersection) {
        var position = player.getCamera().position;
        var pickRay = player.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        for (var block : world.getNearBlocks(position)) {
            var direction = pickRay.direction.scl(13);
            pickRay.set(player.getCamera().position, direction);

            var boundingBox = block.getBoundingBox();
            block.getModel().calculateBoundingBox(boundingBox);
            boundingBox.mul(block.getModel().transform);

            if (Intersector.intersectRayBounds(pickRay, boundingBox, outIntersection)) {
                return Optional.of(block);
            }
        }
        return Optional.empty();
    }

    /**
     * Find the coordinate in which the new block will be placed, based on the intersection with the block clicked
     * @param pointedBlock The block pointed by the camera
     * @param intersection The intersection point between the camera and the block
     * @return The coordinates in which the new block will be placed
     */
    private Coordinates findNextFreeBlock(Block pointedBlock, Vector3 intersection) {
        var pointedCoords = pointedBlock.getCoordinates();
        System.out.println(pointedCoords + " - " + intersection);
        if (pointedCoords.z() - 2 == intersection.z) { // Front
            System.out.println("Front");
            return new Coordinates(pointedCoords.x(), pointedCoords.y(), pointedCoords.z() - World.BLOCK_DISTANCE);
        } else if (pointedCoords.z() + 2 == intersection.z) { // Back
            System.out.println("Back");
            return new Coordinates(pointedCoords.x(), pointedCoords.y(), pointedCoords.z() + World.BLOCK_DISTANCE);
        } else if (pointedCoords.x() + 2 == intersection.x) { // left
            System.out.println("Left");
            return new Coordinates(pointedCoords.x() + World.BLOCK_DISTANCE, pointedCoords.y(), pointedCoords.z());
        } else if (pointedCoords.x() - 2 == intersection.x) { // right
            System.out.println("Right");
            return new Coordinates(pointedCoords.x() - World.BLOCK_DISTANCE, pointedCoords.y(), pointedCoords.z());
        } else if (pointedCoords.y() - 2 == intersection.y) { // Bot
            System.out.println("Bot");
            return new Coordinates(pointedCoords.x(), pointedCoords.y() - World.BLOCK_DISTANCE, pointedCoords.z());
        } else { // Top
            System.out.println("Top");
            return new Coordinates(pointedCoords.x(), pointedCoords.y() + World.BLOCK_DISTANCE, pointedCoords.z());
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            var block = findPickedBlock(new Vector3());
            block.ifPresent(world::destroyBlock);
        } else if (button == Input.Buttons.RIGHT) {
            Vector3 intersection = new Vector3();
            var block = findPickedBlock(intersection).orElse(null);
            if (block == null)
                return false;
            var nextCoords = findNextFreeBlock(block, intersection);
            var newBlock = Blocks.GRASS;
            world.placeBlock(newBlock, nextCoords);
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
